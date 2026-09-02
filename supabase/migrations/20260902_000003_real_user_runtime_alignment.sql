begin;

-- Alignment migration for the production fixes applied on 2026-09-02.
-- Keep this idempotent: production already contains these changes, while a
-- clean database built from Git must arrive at the same security model.

drop index if exists public.harmony_one_active_couple_per_user;

create or replace function public.harmony_is_real_user(p_user_id uuid default auth.uid())
returns boolean
language sql
stable
security definer
set search_path = public, auth
as $$
  select p_user_id is not null
     and exists (
       select 1
       from auth.users u
       where u.id = p_user_id
         and coalesce(u.is_anonymous, false) = false
     );
$$;

-- The earlier restrictive catch-all policy is replaced by explicit checks on
-- every actual capability. This makes the permission model readable and avoids
-- accidentally turning a helper policy into a broad write grant later.
drop policy if exists harmony_profiles_real_users_only on public.harmony_profiles;
drop policy if exists harmony_couples_real_users_only on public.harmony_couples;
drop policy if exists harmony_couple_members_real_users_only on public.harmony_couple_members;
drop policy if exists harmony_couple_invites_real_users_only on public.harmony_couple_invites;
drop policy if exists harmony_question_rounds_real_users_only on public.harmony_question_rounds;
drop policy if exists harmony_question_answers_real_users_only on public.harmony_question_answers;

drop policy if exists harmony_profiles_insert_own on public.harmony_profiles;
create policy harmony_profiles_insert_own
on public.harmony_profiles
for insert
to authenticated
with check (public.harmony_is_real_user() and user_id = auth.uid());

drop policy if exists harmony_profiles_select_related on public.harmony_profiles;
create policy harmony_profiles_select_related
on public.harmony_profiles
for select
to authenticated
using (
  public.harmony_is_real_user()
  and (
    user_id = auth.uid()
    or exists (
      select 1
      from public.harmony_couple_members mine
      join public.harmony_couple_members theirs
        on theirs.couple_id = mine.couple_id
      where mine.user_id = auth.uid()
        and theirs.user_id = harmony_profiles.user_id
    )
  )
);

drop policy if exists harmony_profiles_update_own on public.harmony_profiles;
create policy harmony_profiles_update_own
on public.harmony_profiles
for update
to authenticated
using (public.harmony_is_real_user() and user_id = auth.uid())
with check (public.harmony_is_real_user() and user_id = auth.uid());

drop policy if exists harmony_couples_select_member on public.harmony_couples;
create policy harmony_couples_select_member
on public.harmony_couples
for select
to authenticated
using (public.harmony_is_real_user() and public.harmony_is_couple_member(id));

drop policy if exists harmony_members_select_same_couple on public.harmony_couple_members;
create policy harmony_members_select_same_couple
on public.harmony_couple_members
for select
to authenticated
using (
  public.harmony_is_real_user()
  and public.harmony_is_couple_member(couple_id)
);

drop policy if exists harmony_invites_select_own on public.harmony_couple_invites;
create policy harmony_invites_select_own
on public.harmony_couple_invites
for select
to authenticated
using (public.harmony_is_real_user() and created_by = auth.uid());

drop policy if exists harmony_rounds_insert_member on public.harmony_question_rounds;
create policy harmony_rounds_insert_member
on public.harmony_question_rounds
for insert
to authenticated
with check (
  public.harmony_is_real_user()
  and public.harmony_is_couple_member(couple_id)
);

drop policy if exists harmony_rounds_select_member on public.harmony_question_rounds;
create policy harmony_rounds_select_member
on public.harmony_question_rounds
for select
to authenticated
using (
  public.harmony_is_real_user()
  and public.harmony_is_couple_member(couple_id)
);

drop policy if exists harmony_answers_insert_own on public.harmony_question_answers;
create policy harmony_answers_insert_own
on public.harmony_question_answers
for insert
to authenticated
with check (
  public.harmony_is_real_user()
  and user_id = auth.uid()
  and exists (
    select 1
    from public.harmony_question_rounds r
    where r.id = harmony_question_answers.round_id
      and public.harmony_is_couple_member(r.couple_id)
  )
);

drop policy if exists harmony_answers_select_own on public.harmony_question_answers;
create policy harmony_answers_select_own
on public.harmony_question_answers
for select
to authenticated
using (public.harmony_is_real_user() and user_id = auth.uid());

drop policy if exists harmony_answers_update_own on public.harmony_question_answers;
create policy harmony_answers_update_own
on public.harmony_question_answers
for update
to authenticated
using (public.harmony_is_real_user() and user_id = auth.uid())
with check (
  public.harmony_is_real_user()
  and user_id = auth.uid()
  and exists (
    select 1
    from public.harmony_question_rounds r
    where r.id = harmony_question_answers.round_id
      and public.harmony_is_couple_member(r.couple_id)
  )
);

create or replace function public.disconnect_user_for_account_deletion(p_user_id uuid)
returns boolean
language plpgsql
security definer
set search_path = public
as $$
declare
  v_couple_id uuid;
  v_member_ids uuid[];
begin
  if p_user_id is null then
    raise exception 'invalid_user_id' using errcode = 'invalid_parameter_value';
  end if;

  select m.couple_id into v_couple_id
  from public.harmony_couple_members m
  where m.user_id = p_user_id
  limit 1;

  if v_couple_id is not null then
    select array_agg(m.user_id) into v_member_ids
    from public.harmony_couple_members m
    where m.couple_id = v_couple_id;

    update public.harmony_couple_invites
    set expires_at = least(expires_at, now())
    where used_at is null
      and expires_at > now()
      and created_by = any(v_member_ids);

    delete from public.harmony_couple_members
    where couple_id = v_couple_id;

    update public.harmony_couples
    set ended_at = coalesce(ended_at, now())
    where id = v_couple_id;
  else
    update public.harmony_couple_invites
    set expires_at = least(expires_at, now())
    where created_by = p_user_id
      and used_at is null
      and expires_at > now();
  end if;

  return true;
end;
$$;

create or replace function public.update_harmony_profile(p_display_name text)
returns table (user_id uuid, display_name text, avatar_url text)
language plpgsql
security definer
set search_path = public, auth
as $$
declare
  v_user_id uuid := auth.uid();
  v_name text := btrim(coalesce(p_display_name, ''));
begin
  if not public.harmony_is_real_user(v_user_id) then
    raise exception 'not_authenticated_or_anonymous' using errcode = 'insufficient_privilege';
  end if;

  if length(v_name) < 1 or length(v_name) > 60 then
    raise exception 'invalid_display_name' using errcode = 'invalid_parameter_value';
  end if;

  insert into public.harmony_profiles(user_id, display_name)
  values (v_user_id, v_name)
  on conflict(user_id) do update
  set display_name = excluded.display_name,
      updated_at = now();

  return query
  select p.user_id, p.display_name, p.avatar_url
  from public.harmony_profiles p
  where p.user_id = v_user_id;
end;
$$;

-- Trigger helpers are internal implementation details and must never be RPCs.
revoke all on function public.harmony_enforce_couple_size() from public, anon, authenticated;

-- Account deletion can only be initiated by the verified Edge Function using
-- the service role. The Android client cannot pass an arbitrary user id.
revoke all on function public.disconnect_user_for_account_deletion(uuid)
from public, anon, authenticated;
grant execute on function public.disconnect_user_for_account_deletion(uuid) to service_role;

revoke all on function public.update_harmony_profile(text) from public, anon;
grant execute on function public.update_harmony_profile(text) to authenticated, service_role;

revoke all on function public.harmony_is_real_user(uuid) from public, anon;
grant execute on function public.harmony_is_real_user(uuid) to authenticated, service_role;

-- These RPCs intentionally cross RLS boundaries but validate auth.uid(), real
-- user state and couple membership internally before returning any data.
revoke all on function public.submit_question_answer(text, integer, text) from public, anon;
revoke all on function public.get_pack_question_results(text) from public, anon;
grant execute on function public.submit_question_answer(text, integer, text) to authenticated, service_role;
grant execute on function public.get_pack_question_results(text) to authenticated, service_role;

commit;
