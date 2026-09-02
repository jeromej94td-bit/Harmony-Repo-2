begin;

insert into storage.buckets (
  id,
  name,
  public,
  file_size_limit,
  allowed_mime_types
)
values (
  'harmony-avatars',
  'harmony-avatars',
  false,
  5242880,
  array['image/jpeg', 'image/png', 'image/webp']::text[]
)
on conflict (id) do update
set public = false,
    file_size_limit = excluded.file_size_limit,
    allowed_mime_types = excluded.allowed_mime_types;

-- Custom Harmony avatars live at exactly <auth.uid()>/avatar. They are never
-- public. The owner and their currently connected partner may read the object.
drop policy if exists harmony_avatar_select_owner_or_partner on storage.objects;
create policy harmony_avatar_select_owner_or_partner
on storage.objects
for select
to authenticated
using (
  bucket_id = 'harmony-avatars'
  and public.harmony_is_real_user()
  and (
    (storage.foldername(name))[1] = auth.uid()::text
    or exists (
      select 1
      from public.harmony_couple_members mine
      join public.harmony_couple_members partner
        on partner.couple_id = mine.couple_id
      where mine.user_id = auth.uid()
        and partner.user_id::text = (storage.foldername(name))[1]
    )
  )
);

drop policy if exists harmony_avatar_insert_own on storage.objects;
create policy harmony_avatar_insert_own
on storage.objects
for insert
to authenticated
with check (
  bucket_id = 'harmony-avatars'
  and public.harmony_is_real_user()
  and (storage.foldername(name))[1] = auth.uid()::text
  and (storage.foldername(name))[2] is null
  and name = auth.uid()::text || '/avatar'
);

drop policy if exists harmony_avatar_update_own on storage.objects;
create policy harmony_avatar_update_own
on storage.objects
for update
to authenticated
using (
  bucket_id = 'harmony-avatars'
  and public.harmony_is_real_user()
  and name = auth.uid()::text || '/avatar'
)
with check (
  bucket_id = 'harmony-avatars'
  and public.harmony_is_real_user()
  and name = auth.uid()::text || '/avatar'
);

drop policy if exists harmony_avatar_delete_own on storage.objects;
create policy harmony_avatar_delete_own
on storage.objects
for delete
to authenticated
using (
  bucket_id = 'harmony-avatars'
  and public.harmony_is_real_user()
  and name = auth.uid()::text || '/avatar'
);

create or replace function public.update_harmony_avatar(p_avatar_ref text)
returns table (user_id uuid, display_name text, avatar_url text)
language plpgsql
security definer
set search_path = public, auth
as $$
declare
  v_user_id uuid := auth.uid();
  v_expected text;
  v_ref text := nullif(btrim(coalesce(p_avatar_ref, '')), '');
begin
  if not public.harmony_is_real_user(v_user_id) then
    raise exception 'not_authenticated_or_anonymous' using errcode = 'insufficient_privilege';
  end if;

  v_expected := 'harmony-avatar:' || v_user_id::text || '/avatar';
  if v_ref is not null and v_ref <> v_expected then
    raise exception 'invalid_avatar_ref' using errcode = 'invalid_parameter_value';
  end if;

  insert into public.harmony_profiles(user_id, display_name, avatar_url)
  select
    v_user_id,
    coalesce(nullif(btrim(u.raw_user_meta_data->>'full_name'), ''), split_part(coalesce(u.email, 'Harmony User'), '@', 1)),
    v_ref
  from auth.users u
  where u.id = v_user_id
  on conflict(user_id) do update
  set avatar_url = excluded.avatar_url,
      updated_at = now();

  return query
  select p.user_id, p.display_name, p.avatar_url
  from public.harmony_profiles p
  where p.user_id = v_user_id;
end;
$$;

-- Production clients update profiles through validated RPCs. This prevents a
-- client from bypassing avatar validation with a direct table update.
revoke insert, update, delete on table public.harmony_profiles from authenticated;
grant select on table public.harmony_profiles to authenticated;

revoke all on function public.update_harmony_avatar(text) from public, anon;
grant execute on function public.update_harmony_avatar(text) to authenticated, service_role;

commit;
