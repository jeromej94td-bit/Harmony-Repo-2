begin;

create table if not exists public.harmony_pack_attempts (
  id uuid primary key default gen_random_uuid(),
  couple_id uuid not null references public.harmony_couples(id) on delete cascade,
  pack_id text not null,
  sequence integer not null check (sequence > 0),
  started_at timestamptz not null default now(),
  started_by uuid references auth.users(id) on delete set null,
  finished_at timestamptz,
  unique (couple_id, pack_id, sequence)
);

alter table public.harmony_pack_attempts enable row level security;
revoke all on table public.harmony_pack_attempts from anon, authenticated;

-- Preserve every existing shared run as attempt 1 before changing round uniqueness.
insert into public.harmony_pack_attempts(couple_id, pack_id, sequence, started_at)
select r.couple_id, r.pack_id, 1, min(r.created_at)
from public.harmony_question_rounds r
group by r.couple_id, r.pack_id
on conflict (couple_id, pack_id, sequence) do nothing;

create unique index if not exists harmony_pack_attempts_one_current_idx
  on public.harmony_pack_attempts(couple_id, pack_id)
  where finished_at is null;

alter table public.harmony_question_rounds
  add column if not exists attempt_id uuid;

update public.harmony_question_rounds r
set attempt_id = a.id
from public.harmony_pack_attempts a
where r.attempt_id is null
  and a.couple_id = r.couple_id
  and a.pack_id = r.pack_id
  and a.sequence = 1;

do $$
begin
  if exists (select 1 from public.harmony_question_rounds where attempt_id is null) then
    raise exception 'attempt_backfill_incomplete';
  end if;
end;
$$;

alter table public.harmony_question_rounds
  alter column attempt_id set not null;

alter table public.harmony_question_rounds
  drop constraint if exists harmony_question_rounds_couple_id_pack_id_question_index_key;

do $$
begin
  if not exists (
    select 1 from pg_constraint
    where conrelid = 'public.harmony_question_rounds'::regclass
      and conname = 'harmony_question_rounds_attempt_id_fkey'
  ) then
    alter table public.harmony_question_rounds
      add constraint harmony_question_rounds_attempt_id_fkey
      foreign key (attempt_id)
      references public.harmony_pack_attempts(id)
      on delete cascade;
  end if;
end;
$$;

do $$
begin
  if not exists (
    select 1 from pg_constraint
    where conrelid = 'public.harmony_question_rounds'::regclass
      and conname = 'harmony_question_rounds_attempt_question_key'
  ) then
    alter table public.harmony_question_rounds
      add constraint harmony_question_rounds_attempt_question_key
      unique (attempt_id, question_index);
  end if;
end;
$$;

create index if not exists harmony_question_rounds_attempt_lookup_idx
  on public.harmony_question_rounds(couple_id, pack_id, attempt_id, question_index);

create or replace function public.start_pack_attempt(p_pack_id text)
returns table(attempt_id uuid, attempt_sequence integer, created_new boolean)
language plpgsql
security definer
set search_path to 'public', 'auth'
as $function$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
  v_pack_id text := btrim(coalesce(p_pack_id, ''));
  v_current_id uuid;
  v_current_sequence integer;
  v_next_sequence integer;
  v_has_current_work boolean := false;
begin
  if not public.harmony_is_real_user(v_user_id) then
    raise exception 'not_authenticated_or_anonymous' using errcode = 'insufficient_privilege';
  end if;
  if nullif(v_pack_id, '') is null then
    raise exception 'invalid_pack_id' using errcode = 'invalid_parameter_value';
  end if;

  select m.couple_id into v_couple_id
  from public.harmony_couple_members m
  join public.harmony_couples c on c.id = m.couple_id and c.ended_at is null
  where m.user_id = v_user_id
  limit 1;
  if v_couple_id is null then
    raise exception 'not_paired' using errcode = 'check_violation';
  end if;

  -- Serialize replay starts for the couple so both devices can safely join one attempt.
  perform 1 from public.harmony_couples c where c.id = v_couple_id for update;

  select a.id, a.sequence
    into v_current_id, v_current_sequence
  from public.harmony_pack_attempts a
  where a.couple_id = v_couple_id
    and a.pack_id = v_pack_id
    and a.finished_at is null
  order by a.sequence desc
  limit 1;

  if v_current_id is not null then
    select
      exists (
        select 1
        from public.harmony_question_rounds r
        join public.harmony_question_answers qa on qa.round_id = r.id
        where r.attempt_id = v_current_id
          and qa.user_id = v_user_id
      )
      or exists (
        select 1
        from public.harmony_pack_completions pc
        where pc.couple_id = v_couple_id
          and pc.pack_id = v_pack_id
          and pc.user_id = v_user_id
      )
    into v_has_current_work;

    -- The second device joining a replay has no work in the new attempt yet.
    -- Return that attempt instead of accidentally creating another one.
    if not v_has_current_work then
      return query select v_current_id, v_current_sequence, false;
      return;
    end if;

    update public.harmony_pack_attempts
    set finished_at = coalesce(finished_at, now())
    where id = v_current_id;
  end if;

  select coalesce(max(a.sequence), 0) + 1
    into v_next_sequence
  from public.harmony_pack_attempts a
  where a.couple_id = v_couple_id
    and a.pack_id = v_pack_id;

  insert into public.harmony_pack_attempts(couple_id, pack_id, sequence, started_by)
  values (v_couple_id, v_pack_id, v_next_sequence, v_user_id)
  returning id into v_current_id;

  -- Completion is current-run state. Clearing it makes the replay independent of the old run.
  delete from public.harmony_pack_completions pc
  where pc.couple_id = v_couple_id
    and pc.pack_id = v_pack_id;

  return query select v_current_id, v_next_sequence, true;
end;
$function$;

revoke all on function public.start_pack_attempt(text) from public;
grant execute on function public.start_pack_attempt(text) to authenticated;

create or replace function public.submit_question_answer(
  p_pack_id text,
  p_question_index integer,
  p_answer_text text
)
returns table(round_id uuid, my_answered boolean, partner_answered boolean, ready_to_reveal boolean)
language plpgsql
security definer
set search_path to 'public', 'auth'
as $function$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
  v_pack_id text := btrim(coalesce(p_pack_id, ''));
  v_round_id uuid;
  v_attempt_id uuid;
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
  if nullif(v_pack_id, '') is null
     or p_question_index < 0
     or nullif(btrim(coalesce(p_answer_text, '')), '') is null then
    raise exception 'invalid_answer_payload' using errcode = 'invalid_parameter_value';
  end if;

  select m.couple_id into v_couple_id
  from public.harmony_couple_members m
  join public.harmony_couples c on c.id = m.couple_id
  where m.user_id = v_user_id and c.ended_at is null
  limit 1;
  if v_couple_id is null then
    raise exception 'not_paired' using errcode = 'check_violation';
  end if;

  select cfg.question_count into v_partner_pack_question_count
  from public.harmony_partner_pack_configs cfg
  where cfg.pack_id = v_pack_id and cfg.enabled;
  if v_partner_pack_question_count is not null then
    if p_question_index >= v_partner_pack_question_count then
      raise exception 'invalid_answer_payload' using errcode = 'invalid_parameter_value';
    end if;
    if exists (
      select 1 from public.harmony_pack_completions pc
      where pc.couple_id = v_couple_id
        and pc.pack_id = v_pack_id
        and pc.user_id = v_user_id
    ) then
      raise exception 'partner_pack_already_completed' using errcode = 'check_violation';
    end if;
  end if;

  select a.id into v_attempt_id
  from public.harmony_pack_attempts a
  where a.couple_id = v_couple_id
    and a.pack_id = v_pack_id
    and a.finished_at is null
  order by a.sequence desc
  limit 1;

  if v_attempt_id is null then
    select s.attempt_id into v_attempt_id
    from public.start_pack_attempt(v_pack_id) s
    limit 1;
  end if;

  insert into public.harmony_question_rounds(attempt_id, couple_id, pack_id, question_index)
  values (v_attempt_id, v_couple_id, v_pack_id, p_question_index)
  on conflict (attempt_id, question_index)
  do update set pack_id = excluded.pack_id, couple_id = excluded.couple_id
  returning id into v_round_id;

  insert into public.harmony_question_answers(round_id, user_id, answer_text, answered_at)
  values (v_round_id, v_user_id, p_answer_text, now())
  on conflict on constraint harmony_question_answers_pkey
  do update set answer_text = excluded.answer_text, answered_at = now();

  select count(distinct qa.user_id) into v_answer_count
  from public.harmony_question_answers qa
  join public.harmony_couple_members m
    on m.couple_id = v_couple_id and m.user_id = qa.user_id
  where qa.round_id = v_round_id;

  return query
  select
    v_round_id,
    true,
    exists (
      select 1 from public.harmony_question_answers qa
      where qa.round_id = v_round_id and qa.user_id <> v_user_id
    ),
    case when v_partner_pack_question_count is not null then false else v_answer_count = 2 end;
end;
$function$;

create or replace function public.get_pack_question_results(p_pack_id text)
returns table(
  question_index integer,
  my_answer_text text,
  partner_answered boolean,
  ready_to_reveal boolean,
  partner_user_id uuid,
  partner_display_name text,
  partner_avatar_url text,
  partner_answer_text text
)
language plpgsql
stable
security definer
set search_path to 'public', 'auth'
as $function$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
  v_attempt_id uuid;
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

  select m.couple_id into v_couple_id
  from public.harmony_couple_members m
  join public.harmony_couples c on c.id = m.couple_id
  where m.user_id = v_user_id and c.ended_at is null
  limit 1;
  if v_couple_id is null then
    raise exception 'not_paired' using errcode = 'check_violation';
  end if;

  select a.id into v_attempt_id
  from public.harmony_pack_attempts a
  where a.couple_id = v_couple_id
    and a.pack_id = btrim(p_pack_id)
    and a.finished_at is null
  order by a.sequence desc
  limit 1;
  if v_attempt_id is null then
    return;
  end if;

  return query
  select
    r.question_index,
    mine.answer_text,
    (theirs.answer_text is not null) as partner_answered,
    (mine.answer_text is not null and theirs.answer_text is not null) as ready_to_reveal,
    partner.user_id,
    partner.display_name,
    partner.avatar_url,
    case
      when mine.answer_text is not null and theirs.answer_text is not null then theirs.answer_text
      else null
    end as partner_answer_text
  from public.harmony_question_rounds r
  left join public.harmony_question_answers mine
    on mine.round_id = r.id and mine.user_id = v_user_id
  left join public.harmony_couple_members pm
    on pm.couple_id = v_couple_id and pm.user_id <> v_user_id
  left join public.harmony_profiles partner on partner.user_id = pm.user_id
  left join public.harmony_question_answers theirs
    on theirs.round_id = r.id and theirs.user_id = pm.user_id
  where r.attempt_id = v_attempt_id
  order by r.question_index;
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
  v_attempt_id uuid;
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
  where cfg.pack_id = btrim(coalesce(p_pack_id, '')) and cfg.enabled;
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
  where m.couple_id = v_couple_id and m.user_id <> v_user_id
  limit 1;
  if v_partner_user_id is null then
    raise exception 'partner_missing' using errcode = 'check_violation';
  end if;

  select a.id into v_attempt_id
  from public.harmony_pack_attempts a
  where a.couple_id = v_couple_id
    and a.pack_id = btrim(p_pack_id)
    and a.finished_at is null
  order by a.sequence desc
  limit 1;
  if v_attempt_id is null then
    raise exception 'partner_pack_incomplete' using errcode = 'check_violation';
  end if;

  select count(distinct r.question_index) into v_answered_count
  from public.harmony_question_rounds r
  join public.harmony_question_answers qa
    on qa.round_id = r.id and qa.user_id = v_user_id
  where r.attempt_id = v_attempt_id
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
      couple_id, recipient_user_id, actor_user_id, kind, pack_id, body
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
    select 1 from public.harmony_pack_completions pc
    where pc.couple_id = v_couple_id
      and pc.pack_id = btrim(p_pack_id)
      and pc.user_id = v_partner_user_id
  ) into v_partner_completed;

  return query select true, v_partner_completed, v_partner_completed, (v_inserted_count = 1);
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
  v_attempt_id uuid;
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
  where cfg.pack_id = btrim(coalesce(p_pack_id, '')) and cfg.enabled;
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
  where m.couple_id = v_couple_id and m.user_id <> v_user_id
  limit 1;
  if v_partner_user_id is null then
    raise exception 'partner_missing' using errcode = 'check_violation';
  end if;

  select a.id into v_attempt_id
  from public.harmony_pack_attempts a
  where a.couple_id = v_couple_id
    and a.pack_id = btrim(p_pack_id)
    and a.finished_at is null
  order by a.sequence desc
  limit 1;

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
    on r.attempt_id = v_attempt_id
    and r.question_index = indexes.question_index
  left join public.harmony_question_answers mine
    on mine.round_id = r.id and mine.user_id = v_user_id
  left join public.harmony_question_answers theirs
    on theirs.round_id = r.id and theirs.user_id = v_partner_user_id
  left join public.harmony_profiles partner on partner.user_id = v_partner_user_id
  order by indexes.question_index;
end;
$function$;

create or replace function public.reset_harmony()
returns boolean
language plpgsql
security definer
set search_path to 'public', 'auth'
as $function$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
  v_member_ids uuid[];
begin
  if not public.harmony_is_real_user(v_user_id) then
    raise exception 'not_authenticated_or_anonymous' using errcode = 'insufficient_privilege';
  end if;

  select m.couple_id into v_couple_id
  from public.harmony_couple_members m
  where m.user_id = v_user_id
  limit 1;

  if v_couple_id is not null then
    select array_agg(m.user_id) into v_member_ids
    from public.harmony_couple_members m
    where m.couple_id = v_couple_id;

    delete from public.harmony_question_answers
    where round_id in (
      select id from public.harmony_question_rounds where couple_id = v_couple_id
    );
    delete from public.harmony_question_rounds where couple_id = v_couple_id;
    delete from public.harmony_pack_attempts where couple_id = v_couple_id;

    update public.harmony_couple_invites
    set expires_at = least(expires_at, now())
    where used_at is null
      and expires_at > now()
      and created_by = any(v_member_ids);
    delete from public.harmony_couple_members where couple_id = v_couple_id;
    update public.harmony_couples
    set ended_at = coalesce(ended_at, now())
    where id = v_couple_id;
  else
    update public.harmony_couple_invites
    set expires_at = least(expires_at, now())
    where created_by = v_user_id
      and used_at is null
      and expires_at > now();
  end if;

  return true;
end;
$function$;

commit;
