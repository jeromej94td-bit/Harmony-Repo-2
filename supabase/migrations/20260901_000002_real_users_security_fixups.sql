begin;

-- Keep the checked-in migrations aligned with the production fixes that were
-- verified by the two-user rollback smoke test.

create or replace function public.create_partner_invite()
returns table (code text, expires_at timestamptz)
language plpgsql
volatile
security definer
set search_path = public, auth
as $$
declare
  v_user_id uuid := auth.uid();
  v_code text;
  v_hash text;
  v_expires_at timestamptz := now() + interval '24 hours';
  v_alphabet constant text := 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
  i integer;
  j integer;
begin
  if v_user_id is null then
    raise exception 'not_authenticated' using errcode = 'insufficient_privilege';
  end if;

  if exists (
    select 1 from auth.users u
    where u.id = v_user_id and coalesce(u.is_anonymous, false)
  ) then
    raise exception 'anonymous_accounts_not_supported' using errcode = 'insufficient_privilege';
  end if;

  if exists (
    select 1 from public.harmony_couple_members m where m.user_id = v_user_id
  ) then
    raise exception 'already_paired' using errcode = 'check_violation';
  end if;

  update public.harmony_couple_invites as inv
  set expires_at = least(inv.expires_at, now())
  where inv.created_by = v_user_id
    and inv.used_at is null
    and inv.expires_at > now();

  for i in 1..20 loop
    v_code := '';
    for j in 1..6 loop
      v_code := v_code || substr(
        v_alphabet,
        1 + floor(random() * length(v_alphabet))::int,
        1
      );
    end loop;

    v_hash := encode(extensions.digest(v_code, 'sha256'), 'hex');

    begin
      insert into public.harmony_couple_invites(created_by, code_hash, expires_at)
      values (v_user_id, v_hash, v_expires_at);

      return query select v_code, v_expires_at;
      return;
    exception when unique_violation then
      null;
    end;
  end loop;

  raise exception 'invite_code_generation_failed';
end;
$$;

create or replace function public.join_partner_invite(p_code text)
returns uuid
language plpgsql
volatile
security definer
set search_path = public, auth
as $$
declare
  v_user_id uuid := auth.uid();
  v_normalized text := regexp_replace(upper(coalesce(p_code, '')), '[^A-Z0-9]', '', 'g');
  v_hash text;
  v_invite public.harmony_couple_invites%rowtype;
  v_couple_id uuid;
begin
  if v_user_id is null then
    raise exception 'not_authenticated' using errcode = 'insufficient_privilege';
  end if;

  if exists (
    select 1 from auth.users u
    where u.id = v_user_id and coalesce(u.is_anonymous, false)
  ) then
    raise exception 'anonymous_accounts_not_supported' using errcode = 'insufficient_privilege';
  end if;

  if length(v_normalized) <> 6 then
    raise exception 'invalid_invite_code' using errcode = 'invalid_parameter_value';
  end if;

  if exists (
    select 1 from public.harmony_couple_members m where m.user_id = v_user_id
  ) then
    raise exception 'already_paired' using errcode = 'check_violation';
  end if;

  v_hash := encode(extensions.digest(v_normalized, 'sha256'), 'hex');

  select inv.* into v_invite
  from public.harmony_couple_invites inv
  where inv.code_hash = v_hash
  for update;

  if not found or v_invite.used_at is not null or v_invite.expires_at <= now() then
    raise exception 'invite_not_available' using errcode = 'invalid_parameter_value';
  end if;

  if v_invite.created_by = v_user_id then
    raise exception 'cannot_pair_with_self' using errcode = 'check_violation';
  end if;

  if exists (
    select 1 from public.harmony_couple_members m
    where m.user_id = v_invite.created_by
  ) then
    raise exception 'inviter_already_paired' using errcode = 'check_violation';
  end if;

  insert into public.harmony_couples default values returning id into v_couple_id;

  insert into public.harmony_couple_members(couple_id, user_id)
  values
    (v_couple_id, v_invite.created_by),
    (v_couple_id, v_user_id);

  update public.harmony_couple_invites as inv
  set used_at = now(), used_by = v_user_id
  where inv.id = v_invite.id;

  update public.harmony_couple_invites as inv
  set expires_at = least(inv.expires_at, now())
  where inv.used_at is null
    and inv.expires_at > now()
    and inv.created_by in (v_invite.created_by, v_user_id);

  return v_couple_id;
exception
  when unique_violation then
    raise exception 'already_paired' using errcode = 'check_violation';
end;
$$;

-- Service-role helper used by delete-account before auth.users is removed.
create or replace function public.disconnect_user_for_account_deletion(p_user_id uuid)
returns boolean
language plpgsql
security definer
set search_path = public
as $$
declare
  v_couple_id uuid;
begin
  select m.couple_id into v_couple_id
  from public.harmony_couple_members m
  where m.user_id = p_user_id
  limit 1;

  if v_couple_id is not null then
    delete from public.harmony_couple_members where couple_id = v_couple_id;
    update public.harmony_couples
    set ended_at = coalesce(ended_at, now())
    where id = v_couple_id;
  end if;

  update public.harmony_couple_invites as inv
  set expires_at = least(inv.expires_at, now())
  where inv.created_by = p_user_id
    and inv.used_at is null
    and inv.expires_at > now();

  return true;
end;
$$;

-- Signed-in anonymous Supabase users must never become Harmony users/couples.
do $$
declare
  v_table text;
begin
  foreach v_table in array array[
    'harmony_profiles',
    'harmony_couples',
    'harmony_couple_members',
    'harmony_couple_invites',
    'harmony_question_rounds',
    'harmony_question_answers'
  ] loop
    execute format('drop policy if exists %I on public.%I', v_table || '_real_users_only', v_table);
    execute format(
      'create policy %I on public.%I as restrictive for all to authenticated using (coalesce((auth.jwt()->>''is_anonymous'')::boolean, false) = false) with check (coalesce((auth.jwt()->>''is_anonymous'')::boolean, false) = false)',
      v_table || '_real_users_only',
      v_table
    );
  end loop;
end;
$$;

-- SECURITY DEFINER functions are callable only by the roles that need them.
revoke all on function public.create_partner_invite() from public, anon;
revoke all on function public.get_app_session() from public, anon;
revoke all on function public.get_question_round_status(uuid) from public, anon;
revoke all on function public.join_partner_invite(text) from public, anon;
revoke all on function public.leave_current_couple() from public, anon;
revoke all on function public.reset_harmony() from public, anon;
revoke all on function public.reveal_question_result(uuid) from public, anon;
revoke all on function public.harmony_is_couple_member(uuid, uuid) from public, anon;
revoke all on function public.handle_harmony_auth_user_created() from public, anon, authenticated;

revoke all on function public.disconnect_user_for_account_deletion(uuid)
from public, anon, authenticated;

grant execute on function public.create_partner_invite() to authenticated, service_role;
grant execute on function public.get_app_session() to authenticated, service_role;
grant execute on function public.get_question_round_status(uuid) to authenticated, service_role;
grant execute on function public.join_partner_invite(text) to authenticated, service_role;
grant execute on function public.leave_current_couple() to authenticated, service_role;
grant execute on function public.reset_harmony() to authenticated, service_role;
grant execute on function public.reveal_question_result(uuid) to authenticated, service_role;
grant execute on function public.harmony_is_couple_member(uuid, uuid) to authenticated, service_role;
grant execute on function public.disconnect_user_for_account_deletion(uuid) to service_role;

commit;
