from pathlib import Path
import re, subprocess

root = Path(__file__).resolve().parents[1]
rel = 'app/src/main/java/com/example/data/model/Models.kt'
current = (root / rel).read_text()
baseline = subprocess.check_output(['git','show',f'master:{rel}'], cwd=root, text=True)

def blocks(text: str):
    starts=[m.start() for m in re.finditer(r'\n\s*QuestionPack\(', text)]
    result={}
    for i,start in enumerate(starts):
        end=starts[i+1] if i+1<len(starts) else len(text)
        block=text[start:end]
        mid=re.search(r'id = "([^"]+)"',block)
        if mid: result[mid.group(1)]=block
    return result

def pair_payload(block: str):
    m=re.search(r'pairs = listOf\((.*?)\n\s*\)\s*\n\s*\)', block, re.S)
    return re.sub(r'\s+',' ',m.group(1)).strip() if m else None

base=blocks(baseline); cur=blocks(current)
protected=[]
for pack_id,block in base.items():
    if 'type = "tot"' in block and pack_id != 'liebegleichgewicht':
        protected.append(pack_id)
        assert pack_id in cur, f'protected pack {pack_id} missing'
        assert 'type = "tot"' in cur[pack_id], f'protected pack {pack_id} type changed'
        assert pair_payload(cur[pack_id]) == pair_payload(block), f'protected image pairs changed in {pack_id}'
assert protected, 'no protected image packs discovered'
print(f'Protected content contract passed: {len(protected)} image Das-oder-Das packs unchanged')
