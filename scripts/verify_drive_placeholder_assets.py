#!/usr/bin/env python3
from pathlib import Path
import sys

provider_path = Path("app/src/main/java/com/example/ui/components/TotImageProvider.kt")
provider = provider_path.read_text(encoding="utf-8")

expected_resources = [
    "drive_drink_cappuccino", "drive_drink_matcha_latte", "drive_drink_heisse_schokolade",
    "drive_drink_eistee", "drive_drink_minzlimonade", "drive_drink_fruchtpunsch",
    "drive_drink_bier", "drive_drink_rote_bete_saft", "drive_drink_coca_cola",
    "drive_drink_fanta", "drive_drink_orangensaft", "drive_drink_apfelsaft",
    "drive_drink_kaffee", "drive_drink_tee",
    "drive_animal_hund", "drive_animal_katze", "drive_animal_singvogel",
    "drive_animal_pinguin", "drive_animal_kaninchen", "drive_animal_otter",
    "drive_animal_roter_panda", "drive_animal_fuchs", "drive_animal_meerschweinchen",
    "drive_animal_giraffe", "drive_animal_loewe", "drive_animal_gorilla",
    "drive_animal_meeresschildkroete", "drive_animal_igel", "drive_animal_tiger",
    "drive_animal_wolf", "drive_animal_adler", "drive_animal_delfin",
    "drive_hobby_toepfern", "drive_hobby_klavier", "drive_hobby_malen",
    "drive_hobby_zeichnen", "drive_hobby_badminton", "drive_hobby_fahrrad",
    "drive_hobby_bowling", "drive_hobby_diy", "drive_hobby_gitarre",
    "drive_hobby_tennis", "drive_hobby_brettspiel", "drive_hobby_dart",
]

errors = []
if "loremflickr.com" in provider:
    errors.append("TotImageProvider still contains loremflickr placeholder URLs")

for name in expected_resources:
    if f"R.drawable.{name}" not in provider:
        errors.append(f"missing local drawable mapping: {name}")

# Gourmet-Eis is deliberately excluded from this migration and must keep its own flow.
if "custom_gourmet_eissorten" in provider:
    errors.append("TotImageProvider must not special-case Gourmet-Eis during placeholder migration")

if errors:
    print("Drive placeholder asset contract failed:", file=sys.stderr)
    for error in errors:
        print(f" - {error}", file=sys.stderr)
    raise SystemExit(1)

print(f"Drive placeholder asset contract passed for {len(expected_resources)} local mappings.")
