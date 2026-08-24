#!/usr/bin/env python3
"""Bring all pre-41 Harmony production locales up to the current customer catalog.

The first ten mature locales keep their reviewed/base catalogs untouched and receive only
missing keys in LocalizationUpdates.kt. The six newer locales are complete generated maps,
so missing keys are merged directly into their catalog map. Existing translations always win.
"""
from __future__ import annotations

import argparse
import re
import shutil
from pathlib import Path

import audit_localization as audit
from generate_production_locale_catalogs import kotlin_escape, request_batch

ROOT = Path(__file__).resolve().parents[1]
UI = ROOT / "app/src/main/java/com/example/ui"
UPDATES_PATH = UI / "LocalizationUpdates.kt"

UPDATE_TARGETS = {
    "it": ("ItalianContent.kt", "EXACT_ITALIAN_CONTENT", "LOCALIZATION_UPDATES_ITALIAN", "it"),
    "fr": ("FrenchContent.kt", "EXACT_FRENCH_CONTENT", "LOCALIZATION_UPDATES_FRENCH", "fr"),
    "ja": ("JapaneseContent.kt", "EXACT_JAPANESE_CONTENT", "LOCALIZATION_UPDATES_JAPANESE", "ja"),
    "pl": ("PolishContent.kt", "EXACT_POLISH_CONTENT", "LOCALIZATION_UPDATES_POLISH", "pl"),
    "es-419": ("SpanishContent.kt", "EXACT_SPANISH_LATIN_AMERICA_CONTENT", "LOCALIZATION_UPDATES_SPANISH_LATIN_AMERICA", "es"),
    "es-ES": ("SpanishContent.kt", "EXACT_SPANISH_SPAIN_CONTENT", "LOCALIZATION_UPDATES_SPANISH_SPAIN", "es"),
    "pt-BR": ("PortugueseBrazilContent.kt", "EXACT_PORTUGUESE_BRAZIL_CONTENT", "LOCALIZATION_UPDATES_PORTUGUESE_BRAZIL", "pt"),
    "pt-PT": ("PortuguesePortugalContent.kt", "EXACT_PORTUGUESE_PORTUGAL_CONTENT", "LOCALIZATION_UPDATES_PORTUGUESE_PORTUGAL", "pt"),
    "da": ("DanishContent.kt", "EXACT_DANISH_CONTENT", "LOCALIZATION_UPDATES_DANISH", "da"),
    "no": ("NorwegianContent.kt", "EXACT_NORWEGIAN_CONTENT", "LOCALIZATION_UPDATES_NORWEGIAN", "no"),
}

DIRECT_TARGETS = {
    "nl": ("DutchContent.kt", "EXACT_DUTCH_CONTENT", "nl"),
    "sv": ("SwedishContent.kt", "EXACT_SWEDISH_CONTENT", "sv"),
    "is": ("IcelandicContent.kt", "EXACT_ICELANDIC_CONTENT", "is"),
    "ko": ("KoreanContent.kt", "EXACT_KOREAN_CONTENT", "ko"),
    "zh-CN": ("ChineseSimplifiedContent.kt", "EXACT_CHINESE_SIMPLIFIED_CONTENT", "zh-CN"),
    "zh-TW": ("ChineseTraditionalContent.kt", "EXACT_CHINESE_TRADITIONAL_CONTENT", "zh-TW"),
}


def canonical_customer_keys() -> set[str]:
    canonical_all = audit.extract_map(UI / "EnglishContent.kt", "EXACT_ENGLISH_CONTENT")
    if not canonical_all:
        raise RuntimeError("Could not parse canonical English catalog")
    return {
        key
        for key in canonical_all
        if key not in audit.DEV_ONLY_KEYS
        and key not in audit.INTERNAL_ONLY_KEYS
        and "Entwickler" not in key
    }


def translate_missing(keys: set[str], target: str, code: str) -> dict[str, str]:
    todo = sorted(keys)
    translated: dict[str, str] = {}
    if not todo:
        print(f"{code}: already current", flush=True)
        return translated
    print(f"Translating {len(todo)} newly missing strings for {code}", flush=True)
    for offset in range(0, len(todo), 10):
        chunk = todo[offset:offset + 10]
        translated.update(request_batch(chunk, target))
        print(f"  {code}: {min(offset + len(chunk), len(todo))}/{len(todo)}", flush=True)
    return translated


def replace_map(text: str, map_name: str, values: dict[str, str]) -> str:
    marker = re.search(rf'\b{re.escape(map_name)}\b[^=]*=\s*mapOf\s*\(', text)
    if not marker:
        raise RuntimeError(f"Could not find map {map_name}")

    body_start = marker.end()
    pos = body_start
    depth = 1
    in_string = False
    escaped = False
    while pos < len(text) and depth:
        ch = text[pos]
        if in_string:
            if escaped:
                escaped = False
            elif ch == "\\":
                escaped = True
            elif ch == '"':
                in_string = False
        else:
            if ch == '"':
                in_string = True
            elif ch == '(':
                depth += 1
            elif ch == ')':
                depth -= 1
        pos += 1
    if depth != 0:
        raise RuntimeError(f"Unterminated map {map_name}")

    rows = [f'    "{kotlin_escape(key)}" to "{kotlin_escape(values[key])}",' for key in sorted(values)]
    body = "\n" + "\n".join(rows) + "\n"
    return text[:body_start] + body + text[pos - 1:]


def refresh_update_locales(canonical: set[str]) -> None:
    updates_text = UPDATES_PATH.read_text(encoding="utf-8")
    changed = False
    for code, (filename, base_name, update_name, target) in UPDATE_TARGETS.items():
        base = audit.extract_map(UI / filename, base_name)
        current_updates = audit.parse_map_of(updates_text, update_name)
        if not current_updates:
            raise RuntimeError(f"Could not parse existing update map {update_name}")
        effective_keys = set(base) | set(current_updates)
        missing = canonical - effective_keys
        additions = translate_missing(missing, target, code)
        if additions:
            merged = dict(current_updates)
            merged.update(additions)
            updates_text = replace_map(updates_text, update_name, merged)
            changed = True
    if changed:
        UPDATES_PATH.write_text(updates_text, encoding="utf-8")
        print("Updated LocalizationUpdates.kt", flush=True)


def refresh_direct_locales(canonical: set[str]) -> None:
    for code, (filename, map_name, target) in DIRECT_TARGETS.items():
        path = UI / filename
        text = path.read_text(encoding="utf-8")
        existing = audit.extract_map(path, map_name)
        missing = canonical - set(existing)
        additions = translate_missing(missing, target, code)
        if not additions:
            continue
        merged = dict(existing)
        merged.update(additions)
        path.write_text(replace_map(text, map_name, merged), encoding="utf-8")
        print(f"Updated {filename}: {len(merged)} entries", flush=True)


def verify_complete(canonical: set[str]) -> None:
    updates_text = UPDATES_PATH.read_text(encoding="utf-8")
    errors: list[str] = []
    for code, (filename, base_name, update_name, _target) in UPDATE_TARGETS.items():
        effective = audit.extract_map(UI / filename, base_name)
        effective.update(audit.parse_map_of(updates_text, update_name))
        missing = canonical - set(effective)
        if missing:
            errors.append(f"{code}: {len(missing)} keys still missing")
    for code, (filename, map_name, _target) in DIRECT_TARGETS.items():
        values = audit.extract_map(UI / filename, map_name)
        missing = canonical - set(values)
        if missing:
            errors.append(f"{code}: {len(missing)} keys still missing")
    if errors:
        raise RuntimeError("; ".join(errors))
    print("Existing locale refresh complete: all 16 pre-41 production locales cover current customer keys", flush=True)


def copy_artifacts(artifact_dir: Path) -> None:
    artifact_dir.mkdir(parents=True, exist_ok=True)
    shutil.copy2(UPDATES_PATH, artifact_dir / UPDATES_PATH.name)
    for filename, _map_name, _target in DIRECT_TARGETS.values():
        shutil.copy2(UI / filename, artifact_dir / filename)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--artifact-dir", default=None)
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    canonical = canonical_customer_keys()
    refresh_update_locales(canonical)
    refresh_direct_locales(canonical)
    verify_complete(canonical)
    if args.artifact_dir:
        copy_artifacts(Path(args.artifact_dir))


if __name__ == "__main__":
    main()
