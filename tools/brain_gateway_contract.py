from pathlib import Path

root = Path(__file__).resolve().parents[1]
gateway = (root / 'app/src/main/java/com/example/data/brain/gateway/SupabaseHarmonyBrainGateway.kt').read_text()
viewmodel = (root / 'app/src/main/java/com/example/ui/HarmonyViewModel.kt').read_text()
provider = (root / 'app/src/main/java/com/example/data/SupabaseClientProvider.kt').read_text()

assert 'yepluyipizbbrgoffqdq' in gateway, 'primary Harmony Brain gateway must target connected Supabase project'
assert 'sb_publishable_lat183ycL-tC_3NDwzCHOw_GKmcNWqM' in gateway, 'gateway must use connected publishable key'
assert 'BuildConfig.GEMINI_API_KEY' in gateway, 'gateway must send configured Gemini key to backend'
assert 'x-gemini-key' in gateway.lower(), 'gateway must send x-gemini-key header'
assert 'SupabaseBrainAuthSession' not in gateway, 'primary gateway must not create anonymous sessions'
assert 'HarmonyBrainIntentPolicy.needsLiveSearch' in viewmodel, 'view model must use shared live-search intent policy'
assert 'fun needsBrainWebSearch' not in viewmodel, 'legacy narrow web-search predicate must be removed'
assert 'Trattoria Bella Vista' not in viewmodel and 'Café Am Schlosspark' not in viewmodel, 'offline fallback must not invent businesses'
assert 'yepluyipizbbrgoffqdq' in provider, 'shared Supabase provider must target connected project'
print('Brain gateway contract passed')
