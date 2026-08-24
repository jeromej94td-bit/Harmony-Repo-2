#!/usr/bin/env python3
"""Print current customer-facing localization gaps without calling a translation API."""
from __future__ import annotations

from pathlib import Path

import audit_localization as audit
from production_locale_registry import BY_CODE

ROOT = Path(__file__).resolve().parents[1]
UI = ROOT / "app/src/main/java/com/example/ui"


def main() -> None:
    canonical_all = audit.extract_map(UI / "EnglishContent.kt", "EXACT_ENGLISH_CONTENT")
    canonical = {
        key: value
        for key, value in canonical_all.items()
        if key not in audit.DEV_ONLY_KEYS
        and key not in audit.INTERNAL_ONLY_KEYS
        and "Entwickler" not in key
    }

    specs: dict[str, tuple[str, str]] = {
        code: (filename, map_name)
        for code, (filename, map_name, _updates) in audit.LOCALES.items()
        if code != "en"
    }
    for code, item in BY_CODE.items():
        specs[code] = (item["filename"], item["exact"])

    union_missing: set[str] = set()
    for code in sorted(specs):
        filename, map_name = specs[code]
        values = audit.extract_map(UI / filename, map_name)
        missing = set(canonical) - set(values)
        union_missing.update(missing)
        print(f"{code}: {len(missing)} base-catalog keys missing")

    print(f"\nUNION_MISSING={len(union_missing)}")
    for key in sorted(union_missing):
        print(f"KEY: {key}")
        print(f"EN : {canonical[key]}")


if __name__ == "__main__":
    main()
