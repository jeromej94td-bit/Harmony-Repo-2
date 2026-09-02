-- Restrict user-specific SECURITY DEFINER helpers to signed-in Harmony accounts.
-- Anonymous Auth users still receive the authenticated database role, so the
-- functions themselves continue to reject them through harmony_is_real_user().

revoke execute on function public.harmony_is_real_user(uuid) from public, anon;
grant execute on function public.harmony_is_real_user(uuid) to authenticated, service_role;

revoke execute on function public.update_harmony_profile(text) from public, anon;
grant execute on function public.update_harmony_profile(text) to authenticated, service_role;
