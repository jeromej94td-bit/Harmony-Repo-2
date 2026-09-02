from pathlib import Path
root=Path(__file__).resolve().parents[1]
models=(root/'app/src/main/java/com/example/data/model/Models.kt').read_text()
extra_path=root/'app/src/main/java/com/example/data/model/HarmonyExpansionPacks.kt'
assert extra_path.exists(), 'new natural quiz packs must be defined separately'
extra=extra_path.read_text()
assert 'DEFAULT_PACKS + HarmonyExpansionPacks.PACKS' in models, 'expanded packs must be included without disturbing protected packs'

unbel=models[models.index('id = "unbeliebt"'):models.index('id = "ehepaar"')]
zust=models[models.index('id = "zustimmen"'):models.index('id = "tagesfragen"')]
liebe=models[models.index('id = "liebegleichgewicht"'):models.index('id = "neueliebe"')]
assert unbel.count('Question(') >= 16, 'Unbeliebte Meinungen needs at least 10 additional questions'
assert zust.count('Question(') >= 15, 'Zustimmen oder Ablehnen needs at least 10 additional questions'
assert 'type = "quiz"' in liebe and 'pairs = listOf(' not in liebe and liebe.count('Question(') >= 10, 'Liebe im Gleichgewicht must be a normal answer game'
assert extra.count('QuestionPack(') >= 6 and extra.count('Question(') >= 45, 'add several substantial natural games'
for forbidden in ('type = "tot"', 'pairs = listOf'):
    assert forbidden not in extra, 'new expansion games must not introduce image/tot content'

# Thin non-image packs must be meaningfully filled, not just accompanied by new games.
def pack_block(pack_id: str) -> str:
    marker = f'id = "{pack_id}"'
    start = models.index(marker)
    next_pos = models.find('id = "', start + len(marker))
    return models[start:] if next_pos < 0 else models[start:next_pos]

minimums = {
    "zuhause": 8,
    "kinder": 10,
    "haustier": 8,
    "reisevor": 9,
    "hauskauf": 8,
    "gelegenheit": 10,
    "aufwaermen1": 10,
    "wergehteher": 12,
    "nienie": 12,
    "tiefe": 10,
    "geldpack": 10,
    "naehe": 10,
    "intimleben": 10,
    "gespraechsanreger": 12,
    "essenreden": 10,
}
for pack_id, minimum in minimums.items():
    actual = pack_block(pack_id).count('Question(')
    assert actual >= minimum, f'{pack_id} has only {actual} questions; expected at least {minimum}'

print('Content expansion contract passed')
