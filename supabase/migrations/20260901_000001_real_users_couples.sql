begin;

create extension if not exists pgcrypto;

create table public.harmony_profiles (
  user_id uuid primary key references auth.users(id) on delete cascade,
  display_name text not null default '',
  avatar_url text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table public.harmony_couples (
  id uuid primary key default gen_random_uuid(),
  created_at timestamptz not null default now(),
  ended_at timestamptz
);

create table public.harmony_couple_members (
  couple_id uuid not null references public.harmony_couples(id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  joined_at timestamptz not null default now(),
  primary key (couple_id, user_id)
);

create unique index harmony_one_active_couple_per_user
  on public.harmony_couple_members(user_id);

create table public.harmony_couple_invites (
  id uuid primary key default gen_random_uuid(),
  created_by uuid not null references auth.users(id) on delete cascade,
  code_hash text not null unique,
  expires_at timestamptz not null default (now() + interval '24 hours'),
  used_at timestamptz,
  used_by uuid references auth.users(id) on delete set null,
  created_at timestamptz not null default now()
);

create index harmony_couple_invites_creator_idx
  on public.harmony_couple_invites(created_by, created_at desc);

create table public.harmony_question_rounds (
  id uuid primary key default gen_random_uuid(),
  couple_id uuid not null references public.harmony_couples(id) on delete cascade,
  pack_id text not null,
  question_index integer not null,
  created_at timestamptz not null default now(),
  unique (couple_id, pack_id, question_index)
);

create table public.harmony_question_answers (
  round_id uuid not null references public.harmony_question_rounds(id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  answer_text text not null,
  answered_at timestamptz not null default now(),
  primary key (round_id, user_id)
);

create or replace function public.harmony_touch_profile_updated_at()
returns trigger
language plpgsql
set search_path = public
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

create trigger harmony_profiles_touch_updated_at
before update on public.harmony_profiles
for each row execute function public.harmony_touch_profile_updated_at();

create or replace function public.handle_harmony_auth_user_created()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  if coalesce(new.is_anonymous, false) then
    return new;
  end if;

  insert into public.harmony_profiles(user_id, display_name, avatar_url)
  values (
    new.id,
    coalesce(
      nullif(new.raw_user_meta_data->>'full_name', ''),
      nullif(new.raw_user_meta_data->>'name', ''),
      nullif(split_part(coalesce(new.email, ''), '@', 1), ''),
      'Harmony User'
    ),
    coalesce(new.raw_user_meta_data->>'avatar_url', new.raw_user_meta_data->>'picture')
  )
  on conflict (user_id) do nothing;

  return new;
end;
$$;

drop trigger if exists on_harmony_auth_user_created on auth.users;
create trigger on_harmony_auth_user_created
after insert on auth.users
for each row execute function public.handle_harmony_auth_user_created();

insert into public.harmony_profiles(user_id, display_name, avatar_url)
select
  id,
  coalesce(
    nullif(raw_user_meta_data->>'full_name', ''),
    nullif(raw_user_meta_data->>'name', ''),
    nullif(split_part(coalesce(email, ''), '@', 1), ''),
    'Harmony User'
  ),
  coalesce(raw_user_meta_data->>'avatar_url', raw_user_meta_data->>'picture')
from auth.users
where coalesce(is_anonymous, false) = false
on conflict (user_id) do nothing;

create or replace function public.harmony_is_couple_member(p_couple_id uuid, p_user_id uuid default auth.uid())
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.harmony_couple_members m
    where m.couple_id = p_couple_id
      and m.user_id = p_user_id
  );
$$;

create or replace function public.harmony_enforce_two_members()
returns trigger
language plpgsql
set search_path = public
as $$
begin
  if (select count(*) from public.harmony_couple_members where couple_id = new.couple_id) >= 2 then
    raise exception 'couple_full' using errcode = 'check_violation';
  end if;
  return new;
end;
$$;

create trigger harmony_max_two_members
before insert on public.harmony_couple_members
for each row execute function public.harmony_enforce_two_members();

alter table public.harmony_profiles enable row level security;
alter table public.harmony_couples enable row level security;
alter table public.harmony_couple_members enable row level security;
alter table public.harmony_couple_invites enable row level security;
alter table public.harmony_question_rounds enable row level security;
alter table public.harmony_question_answers enable row level security;

create policy harmony_profiles_select_related
on public.harmony_profiles
for select
to authenticated
using (
  user_id = auth.uid()
  or exists (
    select 1
    from public.harmony_couple_members mine
    join public.harmony_couple_members theirs on theirs.couple_id = mine.couple_id
    where mine.user_id = auth.uid()
      and theirs.user_id = harmony_profiles.user_id
  )
);

create policy harmony_profiles_insert_own
on public.harmony_profiles
for insert
to authenticated
with check (user_id = auth.uid());

create policy harmony_profiles_update_own
on public.harmony_profiles
for update
to authenticated
using (user_id = auth.uid())
with check (user_id = auth.uid());

create policy harmony_couples_select_member
on public.harmony_couples
for select
to authenticated
using (public.harmony_is_couple_member(id));

create policy harmony_members_select_same_couple
on public.harmony_couple_members
for select
to authenticated
using (public.harmony_is_couple_member(couple_id));

create policy harmony_invites_select_own
on public.harmony_couple_invites
for select
to authenticated
using (created_by = auth.uid());

create policy harmony_rounds_select_member
on public.harmony_question_rounds
for select
to authenticated
using (public.harmony_is_couple_member(couple_id));

create policy harmony_rounds_insert_member
on public.harmony_question_rounds
for insert
to authenticated
with check (public.harmony_is_couple_member(couple_id));

create policy harmony_answers_select_own
on public.harmony_question_answers
for select
to authenticated
using (user_id = auth.uid());

create policy harmony_answers_insert_own
on public.harmony_question_answers
for insert
to authenticated
with check (
  user_id = auth.uid()
  and exists (
    select 1
    from public.harmony_question_rounds r
    where r.id = round_id
      and public.harmony_is_couple_member(r.couple_id)
  )
);

create policy harmony_answers_update_own
on public.harmony_question_answers
for update
to authenticated
using (user_id = auth.uid())
with check (
  user_id = auth.uid()
  and exists (
    select 1
    from public.harmony_question_rounds r
    where r.id = round_id
      and public.harmony_is_couple_member(r.couple_id)
  )
);

create or replace function public.get_app_session()
returns table (
  user_id uuid,
  email text,
  display_name text,
  avatar_url text,
  couple_id uuid,
  partner_user_id uuid,
  partner_display_name text,
  partner_avatar_url text
)
language plpgsql
volatile
security definer
set search_path = public, auth
as $$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
begin
  if v_user_id is null then
    raise exception 'not_authenticated' using errcode = 'insufficient_privilege';
  end if;

  if exists (
    select 1 from auth.users u where u.id = v_user_id and coalesce(u.is_anonymous, false)
  ) then
    raise exception 'anonymous_accounts_not_supported' using errcode = 'insufficient_privilege';
  end if;

  insert into public.harmony_profiles(user_id, display_name, avatar_url)
  select
    u.id,
    coalesce(
      nullif(u.raw_user_meta_data->>'full_name', ''),
      nullif(u.raw_user_meta_data->>'name', ''),
      nullif(split_part(coalesce(u.email, ''), '@', 1), ''),
      'Harmony User'
    ),
    coalesce(u.raw_user_meta_data->>'avatar_url', u.raw_user_meta_data->>'picture')
  from auth.users u
  where u.id = v_user_id
  on conflict (user_id) do nothing;

  select m.couple_id into v_couple_id
  from public.harmony_couple_members m
  where m.user_id = v_user_id
  limit 1;

  return query
  select
    me.user_id,
    u.email,
    me.display_name,
    me.avatar_url,
    v_couple_id,
    partner.user_id,
    partner.display_name,
    partner.avatar_url
  from public.harmony_profiles me
  join auth.users u on u.id = me.user_id
  left join public.harmony_couple_members pm
    on pm.couple_id = v_couple_id and pm.user_id <> v_user_id
  left join public.harmony_profiles partner on partner.user_id = pm.user_id
  where me.user_id = v_user_id;
end;
$$;

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

  if exists (select 1 from auth.users u where u.id = v_user_id and coalesce(u.is_anonymous, false)) then
    raise exception 'anonymous_accounts_not_supported' using errcode = 'insufficient_privilege';
  end if;

  if exists (select 1 from public.harmony_couple_members where user_id = v_user_id) then
    raise exception 'already_paired' using errcode = 'check_violation';
  end if;

  update public.harmony_couple_invites
  set expires_at = least(expires_at, now())
  where created_by = v_user_id
    and used_at is null
    and expires_at > now();

  for i in 1..20 loop
    v_code := '';
    for j in 1..6 loop
      v_code := v_code || substr(v_alphabet, 1 + floor(random() * length(v_alphabet))::int, 1);
    end loop;
    v_hash := encode(digest(v_code, 'sha256'), 'hex');

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

  if exists (select 1 from auth.users u where u.id = v_user_id and coalesce(u.is_anonymous, false)) then
    raise exception 'anonymous_accounts_not_supported' using errcode = 'insufficient_privilege';
  end if;

  if length(v_normalized) <> 6 then
    raise exception 'invalid_invite_code' using errcode = 'invalid_parameter_value';
  end if;

  if exists (select 1 from public.harmony_couple_members where user_id = v_user_id) then
    raise exception 'already_paired' using errcode = 'check_violation';
  end if;

  v_hash := encode(digest(v_normalized, 'sha256'), 'hex');

  select * into v_invite
  from public.harmony_couple_invites
  where code_hash = v_hash
  for update;

  if not found or v_invite.used_at is not null or v_invite.expires_at <= now() then
    raise exception 'invite_not_available' using errcode = 'invalid_parameter_value';
  end if;

  if v_invite.created_by = v_user_id then
    raise exception 'cannot_pair_with_self' using errcode = 'check_violation';
  end if;

  if exists (select 1 from public.harmony_couple_members where user_id = v_invite.created_by) then
    raise exception 'inviter_already_paired' using errcode = 'check_violation';
  end if;

  insert into public.harmony_couples default values returning id into v_couple_id;

  insert into public.harmony_couple_members(couple_id, user_id)
  values
    (v_couple_id, v_invite.created_by),
    (v_couple_id, v_user_id);

  update public.harmony_couple_invites
  set used_at = now(), used_by = v_user_id
  where id = v_invite.id;

  update public.harmony_couple_invites
  set expires_at = least(expires_at, now())
  where used_at is null
    and expires_at > now()
    and created_by in (v_invite.created_by, v_user_id);

  return v_couple_id;
exception
  when unique_violation then
    raise exception 'already_paired' using errcode = 'check_violation';
end;
$$;

create or replace function public.leave_current_couple()
returns boolean
language plpgsql
volatile
security definer
set search_path = public
as $$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
begin
  if v_user_id is null then
    raise exception 'not_authenticated' using errcode = 'insufficient_privilege';
  end if;

  select couple_id into v_couple_id
  from public.harmony_couple_members
  where user_id = v_user_id
  limit 1;

  if v_couple_id is null then
    return false;
  end if;

  delete from public.harmony_couple_members where couple_id = v_couple_id;
  update public.harmony_couples set ended_at = coalesce(ended_at, now()) where id = v_couple_id;

  return true;
end;
$$;

create or replace function public.reset_harmony()
returns boolean
language plpgsql
volatile
security definer
set search_path = public
as $$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
begin
  if v_user_id is null then
    raise exception 'not_authenticated' using errcode = 'insufficient_privilege';
  end if;

  select couple_id into v_couple_id
  from public.harmony_couple_members
  where user_id = v_user_id
  limit 1;

  if v_couple_id is not null then
    delete from public.harmony_question_answers
    where round_id in (
      select id from public.harmony_question_rounds where couple_id = v_couple_id
    );
    delete from public.harmony_question_rounds where couple_id = v_couple_id;
    delete from public.harmony_couple_members where couple_id = v_couple_id;
    update public.harmony_couples set ended_at = coalesce(ended_at, now()) where id = v_couple_id;
  end if;

  update public.harmony_couple_invites
  set expires_at = least(expires_at, now())
  where created_by = v_user_id
    and used_at is null
    and expires_at > now();

  return true;
end;
$$;

create or replace function public.disconnect_user_for_account_deletion(p_user_id uuid)
returns boolean
language plpgsql
volatile
security definer
set search_path = public
as $$
declare
  v_couple_id uuid;
begin
  select couple_id into v_couple_id
  from public.harmony_couple_members
  where user_id = p_user_id
  limit 1;

  if v_couple_id is not null then
    delete from public.harmony_couple_members where couple_id = v_couple_id;
    update public.harmony_couples set ended_at = coalesce(ended_at, now()) where id = v_couple_id;
  end if;

  update public.harmony_couple_invites
  set expires_at = least(expires_at, now())
  where created_by = p_user_id
    and used_at is null
    and expires_at > now();

  return true;
end;
$$;

create or replace function public.get_question_round_status(p_round_id uuid)
returns table (my_answered boolean, partner_answered boolean, ready_to_reveal boolean)
language plpgsql
stable
security definer
set search_path = public
as $$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
  v_count integer;
begin
  if v_user_id is null then
    raise exception 'not_authenticated' using errcode = 'insufficient_privilege';
  end if;

  select r.couple_id into v_couple_id
  from public.harmony_question_rounds r
  where r.id = p_round_id;

  if v_couple_id is null or not public.harmony_is_couple_member(v_couple_id, v_user_id) then
    raise exception 'round_not_available' using errcode = 'insufficient_privilege';
  end if;

  select count(distinct a.user_id) into v_count
  from public.harmony_question_answers a
  where a.round_id = p_round_id;

  return query
  select
    exists(select 1 from public.harmony_question_answers a where a.round_id = p_round_id and a.user_id = v_user_id),
    exists(select 1 from public.harmony_question_answers a where a.round_id = p_round_id and a.user_id <> v_user_id),
    v_count = 2;
end;
$$;

create or replace function public.reveal_question_result(p_round_id uuid)
returns table (
  user_id uuid,
  display_name text,
  avatar_url text,
  answer_text text,
  answered_at timestamptz
)
language plpgsql
stable
security definer
set search_path = public
as $$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
  v_count integer;
begin
  if v_user_id is null then
    raise exception 'not_authenticated' using errcode = 'insufficient_privilege';
  end if;

  select r.couple_id into v_couple_id
  from public.harmony_question_rounds r
  where r.id = p_round_id;

  if v_couple_id is null or not public.harmony_is_couple_member(v_couple_id, v_user_id) then
    raise exception 'round_not_available' using errcode = 'insufficient_privilege';
  end if;

  select count(distinct a.user_id) into v_count
  from public.harmony_question_answers a
  where a.round_id = p_round_id;

  if v_count <> 2 then
    return;
  end if;

  return query
  select a.user_id, p.display_name, p.avatar_url, a.answer_text, a.answered_at
  from public.harmony_question_answers a
  join public.harmony_profiles p on p.user_id = a.user_id
  where a.round_id = p_round_id
  order by a.answered_at, a.user_id;
end;
$$;

revoke all on function public.disconnect_user_for_account_deletion(uuid) from public, anon, authenticated;
grant execute on function public.disconnect_user_for_account_deletion(uuid) to service_role;

grant execute on function public.get_app_session() to authenticated;
grant execute on function public.create_partner_invite() to authenticated;
grant execute on function public.join_partner_invite(text) to authenticated;
grant execute on function public.leave_current_couple() to authenticated;
grant execute on function public.reset_harmony() to authenticated;
grant execute on function public.get_question_round_status(uuid) to authenticated;
grant execute on function public.reveal_question_result(uuid) to authenticated;

commit;
