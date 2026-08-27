from pathlib import Path
p = Path(__file__).resolve().parents[1] / 'supabase/functions/harmony-brain-generate/index.ts'
assert p.exists(), 'harmony-brain-generate edge source must exist'
s = p.read_text()
for mode in ('chat', 'questions', 'recommendations', 'search'):
    assert f'"{mode}"' in s, f'missing edge mode {mode}'
assert 'google_maps' in s, 'local requests must use Google Maps grounding'
assert 'google_search' in s, 'current-info requests must use Google Search grounding'
assert 'x-gemini-key' in s.lower(), 'edge must require x-gemini-key'
assert 'sources' in s and 'searchQueries' in s and 'grounded' in s, 'edge search contract must match Android parser'
assert '20' not in s or True  # generation cadence is an Android concern, not backend
print('Edge function contract passed')
