#!/usr/bin/env python3
"""Regression guard for Harmony's shipped locales and locale-specific cuisine decks."""
from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
UI = ROOT / "app/src/main/java/com/example/ui"
DATA = ROOT / "app/src/main/java/com/example/data"

EXPECTED_CODES = {
    "de", "en", "it", "fr", "ja", "pl", "es-419", "es-ES", "pt-BR", "pt-PT",
    "da", "no", "nl", "sv", "is", "ko", "zh-CN", "zh-TW",
    "hu", "ro", "bg", "uk", "ru", "el", "tr", "ar", "he", "fa", "hi", "bn",
    "ur", "ta", "te", "mr", "gu", "kn", "ml", "th", "vi", "id", "ms", "fil",
    "my", "km", "lo", "sw", "af", "am", "yo", "ig", "ha", "zu", "xh", "so",
    "et", "lv", "lt", "sl", "sr",
}


def require(condition: bool, message: str) -> None:
    if not condition:
        raise SystemExit(message)


def enum_entries(language_text: str) -> dict[str, str]:
    enum_body = language_text.split("val isRtl", 1)[0]
    return dict(re.findall(r'^\s*([A-Z][A-Z0-9_]*)\("([^"]+)"', enum_body, re.MULTILINE))


def extract_block(text: str, start: str, end: str) -> str:
    require(start in text, f"Missing block start: {start}")
    tail = text.split(start, 1)[1]
    require(end in tail, f"Missing block end after: {start}")
    return tail.split(end, 1)[0]


def main() -> None:
    language = (UI / "Language.kt").read_text(encoding="utf-8")
    catalog = (UI / "TranslationCatalog.kt").read_text(encoding="utf-8")
    profile = (UI / "screens/ProfileSheet.kt").read_text(encoding="utf-8")
    language_selector = (UI / "screens/LanguageSelectorCard.kt").read_text(encoding="utf-8")
    cuisine = (DATA / "CuisinePackInstaller.kt").read_text(encoding="utf-8")
    drive = (DATA / "DriveTotAssetInstaller.kt").read_text(encoding="utf-8")

    entries = enum_entries(language)
    actual_codes = set(entries.values())
    require(len(entries) == 59, f"Expected 59 AppLanguage entries, found {len(entries)}")
    require(actual_codes == EXPECTED_CODES, f"Locale inventory drift: {sorted(EXPECTED_CODES ^ actual_codes)}")

    require(
        'fun hasCompletePack(language: AppLanguage): Boolean = true' in catalog,
        "Runtime language availability must not depend on translation freshness",
    )
    require(
        'fun hasFullCustomerCoverage(language: AppLanguage): Boolean' in catalog,
        "Strict translation coverage diagnostic is missing",
    )
    require(
        'LanguageSelectorCard(' in profile,
        "Profile no longer wires the protected language selector",
    )
    require(
        'AppLanguage.entries.filter(TranslationCatalog::hasCompletePack)' in language_selector,
        "Profile language selector no longer uses the protected availability gate",
    )

    for enum_name in entries:
        require(
            f"AppLanguage.{enum_name} ->" in catalog,
            f"TranslationCatalog routing missing for {enum_name}",
        )

    require('ITALIAN_PACK_ID = "tot_italian_cuisine_mixed"' in cuisine, "Italian cuisine pack ID missing")
    require('POLISH_PACK_ID = "tot_polish_cuisine_traditional"' in cuisine, "Polish cuisine pack ID missing")
    require('"it" -> listOf(italianPack)' in cuisine, "Italian language-to-cuisine link missing")
    require('"pl" -> listOf(polishPack)' in cuisine, "Polish language-to-cuisine link missing")
    require('CuisinePackInstaller.install(context)' in drive, "CuisinePackInstaller is no longer installed at startup")

    italian_pairs = extract_block(cuisine, "private val italianPack = QuestionPack(", "private val polishPack")
    polish_pairs = extract_block(cuisine, "private val polishPack = QuestionPack(", "private val italianImages")
    italian_images = extract_block(cuisine, "private val italianImages = listOf(", "private val polishImages")
    polish_images = extract_block(cuisine, "private val polishImages = listOf(", "fun install(context")

    require(italian_pairs.count(' to "') == 30, "Italian cuisine deck must keep 30 food pairs")
    require(polish_pairs.count(' to "') == 14, "Polish cuisine deck must keep 14 food pairs")
    require(italian_images.count(" to R.drawable.") == 30, "Italian cuisine image links must keep 30 pairs")
    require(polish_images.count(" to R.drawable.") == 14, "Polish cuisine image links must keep 14 pairs")

    print("Language inventory guard OK: 59 locales; Italian/Polish cuisine links intact")


if __name__ == "__main__":
    main()
