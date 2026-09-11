-- Pilot: whole-pack partner reveal for "Aufwärmen: Einander kennenlernen".
-- Existing per-question reveal behavior stays unchanged for every non-configured pack.

create table if not exists public.harmony_partner_pack_configs (
  pack_id text primary key,
  display_title text not null,
  question_count integer not null check (question_count > 0),
  enabled boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

alter table public.harmony_partner_pack_configs enable row level security;
revoke all on table public.harmony_partner_pack_configs from anon, authenticated;

insert into public.harmony_partner_pack_configs(pack_id, display_title, question_count, enabled)
values ('aufwaermen1', 'Einander kennenlernen', 10, true)
on conflict (pack_id) do update
set display_title = excluded.display_title,
    question_count = excluded.question_count,
    enabled = excluded.enabled,
    updated_at = now();

create table if not exists public.harmony_pack_completions (
  couple_id uuid not null references public.harmony_couples(id) on delete cascade,
  pack_id text not null references public.harmony_partner_pack_configs(pack_id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  completed_at timestamptz not null default now(),
  primary key (couple_id, pack_id, user_id)
);

create index if not exists harmony_pack_completions_user_idx
  on public.harmony_pack_completions(user_id, pack_id, completed_at desc);

alter table public.harmony_pack_completions enable row level security;
revoke all on table public.harmony_pack_completions from anon, authenticated;

create table if not exists public.harmony_partner_notifications (
  id uuid primary key default gen_random_uuid(),
  couple_id uuid not null references public.harmony_couples(id) on delete cascade,
  recipient_user_id uuid not null references auth.users(id) on delete cascade,
  actor_user_id uuid not null references auth.users(id) on delete cascade,
  kind text not null,
  pack_id text not null references public.harmony_partner_pack_configs(pack_id) on delete cascade,
  body text not null,
  created_at timestamptz not null default now(),
  read_at timestamptz,
  constraint harmony_partner_notifications_kind_check
    check (kind in ('partner_pack_completed')),
  constraint harmony_partner_notifications_once_key
    unique (couple_id, recipient_user_id, actor_user_id, kind, pack_id)
);

create index if not exists harmony_partner_notifications_recipient_idx
  on public.harmony_partner_notifications(recipient_user_id, read_at, created_at desc);

alter table public.harmony_partner_notifications enable row level security;
revoke all on table public.harmony_partner_notifications from anon, authenticated;

-- Preserve the existing answer RPC contract, but configured whole-pack pilots become
-- immutable after completion and cannot accept out-of-range question indices.
create or replace function public.submit_question_answer(
  p_pack_id text,
  p_question_index integer,
  p_answer_text text
)
returns table(
  round_id uuid,
  my_answered boolean,
  partner_answered boolean,
  ready_to_reveal boolean
)
language plpgsql
security definer
set search_path to 'public', 'auth'
as $function$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
  v_round_id uuid;
  v_answer_count integer;
  v_partner_pack_question_count integer;
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

  if nullif(btrim(coalesce(p_pack_id, '')), '') is null
     or p_question_index < 0
     or nullif(btrim(coalesce(p_answer_text, '')), '') is null then
    raise exception 'invalid_answer_payload' using errcode = 'invalid_parameter_value';
  end if;

  select m.couple_id into v_couple_id
  from public.harmony_couple_members m
  join public.harmony_couples c on c.id = m.couple_id
  where m.user_id = v_user_id
    and c.ended_at is null
  limit 1;

  if v_couple_id is null then
    raise exception 'not_paired' using errcode = 'check_violation';
  end if;

  select cfg.question_count into v_partner_pack_question_count
  from public.harmony_partner_pack_configs cfg
  where cfg.pack_id = btrim(p_pack_id)
    and cfg.enabled;

  if v_partner_pack_question_count is not null then
    if p_question_index >= v_partner_pack_question_count then
      raise exception 'invalid_answer_payload' using errcode = 'invalid_parameter_value';
    end if;

    if exists (
      select 1
      from public.harmony_pack_completions pc
      where pc.couple_id = v_couple_id
        and pc.pack_id = btrim(p_pack_id)
        and pc.user_id = v_user_id
    ) then
      raise exception 'partner_pack_already_completed' using errcode = 'check_violation';
    end if;
  end if;

  insert into public.harmony_question_rounds(couple_id, pack_id, question_index)
  values (v_couple_id, btrim(p_pack_id), p_question_index)
  on conflict (couple_id, pack_id, question_index)
  do update set pack_id = excluded.pack_id
  returning id into v_round_id;

  insert into public.harmony_question_answers(round_id, user_id, answer_text, answered_at)
  values (v_round_id, v_user_id, btrim(p_answer_text), now())
  on conflict on constraint harmony_question_answers_pkey
  do update set answer_text = excluded.answer_text, answered_at = now();

  select count(distinct a.user_id) into v_answer_count
  from public.harmony_question_answers a
  join public.harmony_couple_members m
    on m.couple_id = v_couple_id and m.user_id = a.user_id
  where a.round_id = v_round_id;

  return query
  select
    v_round_id,
    true,
    exists (
      select 1
      from public.harmony_question_answers a
      where a.round_id = v_round_id
        and a.user_id <> v_user_id
    ),
    case
      when v_partner_pack_question_count is not null then false
      else v_answer_count = 2
    end;
end;
$function$;

create or replace function public.complete_partner_pack(p_pack_id text)
returns table(
  my_completed boolean,
  partner_completed boolean,
  ready_to_reveal boolean,
  notification_created boolean
)
language plpgsql
security definer
set search_path to 'public', 'auth'
as $function$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
  v_partner_user_id uuid;
  v_actor_name text;
  v_display_title text;
  v_question_count integer;
  v_answered_count integer;
  v_inserted_count integer := 0;
  v_partner_completed boolean := false;
begin
  if not public.harmony_is_real_user(v_user_id) then
    raise exception 'not_authenticated_or_anonymous' using errcode = 'insufficient_privilege';
  end if;

  select cfg.display_title, cfg.question_count
  into v_display_title, v_question_count
  from public.harmony_partner_pack_configs cfg
  where cfg.pack_id = btrim(coalesce(p_pack_id, ''))
    and cfg.enabled;

  if v_question_count is null then
    raise exception 'partner_pack_not_enabled' using errcode = 'invalid_parameter_value';
  end if;

  select m.couple_id into v_couple_id
  from public.harmony_couple_members m
  join public.harmony_couples c on c.id = m.couple_id and c.ended_at is null
  where m.user_id = v_user_id
  limit 1;

  if v_couple_id is null then
    raise exception 'not_paired' using errcode = 'check_violation';
  end if;

  select m.user_id into v_partner_user_id
  from public.harmony_couple_members m
  where m.couple_id = v_couple_id
    and m.user_id <> v_user_id
  limit 1;

  if v_partner_user_id is null then
    raise exception 'partner_missing' using errcode = 'check_violation';
  end if;

  select count(distinct r.question_index) into v_answered_count
  from public.harmony_question_rounds r
  join public.harmony_question_answers a
    on a.round_id = r.id and a.user_id = v_user_id
  where r.couple_id = v_couple_id
    and r.pack_id = btrim(p_pack_id)
    and r.question_index >= 0
    and r.question_index < v_question_count;

  if v_answered_count <> v_question_count then
    raise exception 'partner_pack_incomplete' using errcode = 'check_violation';
  end if;

  insert into public.harmony_pack_completions(couple_id, pack_id, user_id, completed_at)
  values (v_couple_id, btrim(p_pack_id), v_user_id, now())
  on conflict (couple_id, pack_id, user_id) do nothing;
  get diagnostics v_inserted_count = row_count;

  select p.display_name into v_actor_name
  from public.harmony_profiles p
  where p.user_id = v_user_id;
  v_actor_name := coalesce(nullif(btrim(v_actor_name), ''), 'Dein Partner');

  if v_inserted_count = 1 then
    insert into public.harmony_partner_notifications(
      couple_id,
      recipient_user_id,
      actor_user_id,
      kind,
      pack_id,
      body
    )
    values (
      v_couple_id,
      v_partner_user_id,
      v_user_id,
      'partner_pack_completed',
      btrim(p_pack_id),
      v_display_title || ' wurde von ' || v_actor_name || ' ausgefüllt. Antworte jetzt, um die Antworten zu sehen.'
    )
    on conflict on constraint harmony_partner_notifications_once_key do nothing;
  end if;

  select exists (
    select 1
    from public.harmony_pack_completions pc
    where pc.couple_id = v_couple_id
      and pc.pack_id = btrim(p_pack_id)
      and pc.user_id = v_partner_user_id
  ) into v_partner_completed;

  return query
  select true, v_partner_completed, v_partner_completed, (v_inserted_count = 1);
end;
$function$;

create or replace function public.get_partner_pack_results(p_pack_id text)
returns table(
  question_index integer,
  my_answer_text text,
  partner_answered boolean,
  ready_to_reveal boolean,
  partner_user_id uuid,
  partner_display_name text,
  partner_avatar_url text,
  partner_answer_text text,
  my_completed boolean,
  partner_completed boolean
)
language plpgsql
stable
security definer
set search_path to 'public', 'auth'
as $function$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
  v_partner_user_id uuid;
  v_question_count integer;
  v_my_completed boolean := false;
  v_partner_completed boolean := false;
  v_ready_to_reveal boolean := false;
begin
  if not public.harmony_is_real_user(v_user_id) then
    raise exception 'not_authenticated_or_anonymous' using errcode = 'insufficient_privilege';
  end if;

  select cfg.question_count into v_question_count
  from public.harmony_partner_pack_configs cfg
  where cfg.pack_id = btrim(coalesce(p_pack_id, ''))
    and cfg.enabled;

  if v_question_count is null then
    raise exception 'partner_pack_not_enabled' using errcode = 'invalid_parameter_value';
  end if;

  select m.couple_id into v_couple_id
  from public.harmony_couple_members m
  join public.harmony_couples c on c.id = m.couple_id and c.ended_at is null
  where m.user_id = v_user_id
  limit 1;

  if v_couple_id is null then
    raise exception 'not_paired' using errcode = 'check_violation';
  end if;

  select m.user_id into v_partner_user_id
  from public.harmony_couple_members m
  where m.couple_id = v_couple_id
    and m.user_id <> v_user_id
  limit 1;

  if v_partner_user_id is null then
    raise exception 'partner_missing' using errcode = 'check_violation';
  end if;

  select exists (
    select 1 from public.harmony_pack_completions pc
    where pc.couple_id = v_couple_id
      and pc.pack_id = btrim(p_pack_id)
      and pc.user_id = v_user_id
  ) into v_my_completed;

  select exists (
    select 1 from public.harmony_pack_completions pc
    where pc.couple_id = v_couple_id
      and pc.pack_id = btrim(p_pack_id)
      and pc.user_id = v_partner_user_id
  ) into v_partner_completed;

  v_ready_to_reveal := v_my_completed and v_partner_completed;

  return query
  select
    indexes.question_index,
    mine.answer_text,
    case when v_ready_to_reveal then (theirs.answer_text is not null) else false end,
    v_ready_to_reveal,
    partner.user_id,
    partner.display_name,
    partner.avatar_url,
    case when v_ready_to_reveal then theirs.answer_text else null end,
    v_my_completed,
    v_partner_completed
  from generate_series(0, v_question_count - 1) as indexes(question_index)
  left join public.harmony_question_rounds r
    on r.couple_id = v_couple_id
   and r.pack_id = btrim(p_pack_id)
   and r.question_index = indexes.question_index
  left join public.harmony_question_answers mine
    on mine.round_id = r.id and mine.user_id = v_user_id
  left join public.harmony_question_answers theirs
    on theirs.round_id = r.id and theirs.user_id = v_partner_user_id
  left join public.harmony_profiles partner
    on partner.user_id = v_partner_user_id
  order by indexes.question_index;
end;
$function$;

create or replace function public.get_partner_notifications()
returns table(
  notification_id uuid,
  pack_id text,
  body text,
  actor_user_id uuid,
  actor_display_name text,
  actor_avatar_url text,
  created_at timestamptz
)
language sql
stable
security definer
set search_path to 'public', 'auth'
as $function$
  select
    n.id,
    n.pack_id,
    n.body,
    n.actor_user_id,
    p.display_name,
    p.avatar_url,
    n.created_at
  from public.harmony_partner_notifications n
  left join public.harmony_profiles p on p.user_id = n.actor_user_id
  where n.recipient_user_id = auth.uid()
    and public.harmony_is_real_user(auth.uid())
    and n.read_at is null
  order by n.created_at asc
  limit 20;
$function$;

create or replace function public.mark_partner_notification_read(p_notification_id uuid)
returns boolean
language plpgsql
security definer
set search_path to 'public', 'auth'
as $function$
declare
  v_updated integer := 0;
begin
  if not public.harmony_is_real_user(auth.uid()) then
    raise exception 'not_authenticated_or_anonymous' using errcode = 'insufficient_privilege';
  end if;

  update public.harmony_partner_notifications n
  set read_at = coalesce(n.read_at, now())
  where n.id = p_notification_id
    and n.recipient_user_id = auth.uid();
  get diagnostics v_updated = row_count;
  return v_updated = 1;
end;
$function$;

revoke all on function public.complete_partner_pack(text) from public, anon;
revoke all on function public.get_partner_pack_results(text) from public, anon;
revoke all on function public.get_partner_notifications() from public, anon;
revoke all on function public.mark_partner_notification_read(uuid) from public, anon;

grant execute on function public.complete_partner_pack(text) to authenticated;
grant execute on function public.get_partner_pack_results(text) to authenticated;
grant execute on function public.get_partner_notifications() to authenticated;
grant execute on function public.mark_partner_notification_read(uuid) to authenticated;
