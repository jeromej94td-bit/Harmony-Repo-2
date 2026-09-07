create or replace function public.get_app_session()
returns table(
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
security definer
set search_path to 'public', 'auth'
as $function$
declare
  v_user_id uuid := auth.uid();
  v_couple_id uuid;
  v_default_name text;
  v_default_avatar text;
begin
  if not public.harmony_is_real_user(v_user_id) then
    raise exception 'not_authenticated_or_anonymous' using errcode = 'insufficient_privilege';
  end if;

  select
    coalesce(
      nullif(u.raw_user_meta_data->>'full_name',''),
      nullif(u.raw_user_meta_data->>'name',''),
      nullif(split_part(coalesce(u.email,''),'@',1),''),
      'Harmony User'
    ),
    coalesce(u.raw_user_meta_data->>'avatar_url', u.raw_user_meta_data->>'picture')
  into v_default_name, v_default_avatar
  from auth.users u
  where u.id = v_user_id;

  insert into public.harmony_profiles(user_id, display_name, avatar_url)
  values (v_user_id, v_default_name, v_default_avatar)
  on conflict on constraint harmony_profiles_pkey do update
  set display_name = case
        when btrim(public.harmony_profiles.display_name) = '' then excluded.display_name
        else public.harmony_profiles.display_name
      end,
      avatar_url = coalesce(public.harmony_profiles.avatar_url, excluded.avatar_url),
      updated_at = now();

  select m.couple_id into v_couple_id
  from public.harmony_couple_members m
  join public.harmony_couples c on c.id = m.couple_id and c.ended_at is null
  where m.user_id = v_user_id
  limit 1;

  return query
  select
    me.user_id,
    u.email::text,
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
$function$;
