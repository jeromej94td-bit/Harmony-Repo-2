from pathlib import Path

root = Path(__file__).resolve().parents[1]
html = (root / 'tools/harmony-brain-test.html').read_text()
edge = (root / 'supabase/functions/harmony-brain-test-ui/index.ts').read_text()

assert 'harmony-brain-generate' in html
assert 'x-gemini-key' in html
assert 'type="password"' in html
assert 'Testsuite starten' in html
for mode in ('chat', 'search', 'questions', 'recommendations'):
    assert f'value="{mode}"' in html or f"'{mode}'" in html
assert 'grounded' in html and 'sources' in html
assert 'AIzaSy' not in html, 'a real Gemini key must never be embedded in the test page'
assert 'Deno.serve' in edge and 'text/html' in edge
print('Harmony Brain test page contract passed')
