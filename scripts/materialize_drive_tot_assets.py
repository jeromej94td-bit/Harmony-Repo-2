#!/usr/bin/env python3
"""Materialize the approved Drive TOT artwork as bundled Android WebP drawables.

This is intentionally scoped to the 44 drink/animal/hobby placeholders. Gourmet-Eis
and every other TotImageProvider mapping stay untouched.
"""

from __future__ import annotations

import io
import time
import urllib.error
import urllib.request
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
PROVIDER = ROOT / "app/src/main/java/com/example/ui/components/TotImageProvider.kt"
DRAWABLE_DIR = ROOT / "app/src/main/res/drawable-nodpi"

# option text -> (drawable name, Google Drive file id)
ASSETS: dict[str, tuple[str, str]] = {
    # Drinks
    "Cappuccino": ("drive_drink_cappuccino", "1XQ_M6Z6ODn8Lasl0Xg0DZEbnzh8KnsFY"),
    "Matcha-Latte": ("drive_drink_matcha_latte", "1TLTtmsUNmIMozvv0lrNO_FOyavygMBGU"),
    "Heiße Schokolade": ("drive_drink_heisse_schokolade", "1OzsEvYX-aD4jrMAgfVHgdCsIQmiQlDAI"),
    "Eistee": ("drive_drink_eistee", "1sM4FiVipdoM6WJNBE3ce7GmjTviMBUpO"),
    "Minzlimonade": ("drive_drink_minzlimonade", "1ms8oS-s0QzkCWsPaVAU0Y67sUCZxSLlU"),
    "Fruchtpunsch": ("drive_drink_fruchtpunsch", "121ScPgsWoh9suAKa7QAwSg0PkNulV3I1"),
    "Bier": ("drive_drink_bier", "1xkqPC3gFTdRuzV8nptxWOL3VyiB_MKHu"),
    "Rote-Bete-Saft": ("drive_drink_rote_bete_saft", "1xSMxlt-CLVAXtE8W6mfcKb__MgaqtNcL"),
    "Coca-Cola": ("drive_drink_coca_cola", "1wuxqt5zBCfXdtAi6HCta4cPkDYAQOU7C"),
    "Fanta": ("drive_drink_fanta", "1fVx7PM5zuEaufOIo_xPTY1HTv9V_Cqs6"),
    "Orangensaft": ("drive_drink_orangensaft", "1liF5355cFusaSWlO4HM8Eyx2nFkdQlgY"),
    "Apfelsaft": ("drive_drink_apfelsaft", "1gOrobAZco4RRuaLb44A4rp-3OO92sJ2k"),
    "Kaffee": ("drive_drink_kaffee", "1eRhL9p1g4jO8Hs80-poNp1AiXOGoNS29"),
    "Tee": ("drive_drink_tee", "1xkhDHfk-ZE7vA4fxnTbxkvScx4ugAtkw"),
    # Animals
    "Hund": ("drive_animal_hund", "1ehGg9wP8vEY0SE6ZJdDXy-Blqxr1u59V"),
    "Katze": ("drive_animal_katze", "13m6Z9GYx0RNmk60ZPfqRBIFJW5d7x9Uj"),
    "Singvogel": ("drive_animal_singvogel", "1hTMcbdgdK4vpd1_YlgatC_ehT9pVkvcQ"),
    "Pinguin": ("drive_animal_pinguin", "1kHWDxqYOSMbLP3IF_jNp5KznnY9Yngvc"),
    "Kaninchen": ("drive_animal_kaninchen", "1wtMukevVk1IrE2940Ward4hNSJXZyN96"),
    "Otter": ("drive_animal_otter", "1R9IQIGnEHJ9mBuoChZHBPeXH12R8t3HG"),
    "Roter Panda": ("drive_animal_roter_panda", "1lJkh4iFsR1g6GQR83ipnGf4wpiFZu84T"),
    "Fuchs": ("drive_animal_fuchs", "1NgvcGd7wuyz0vqUdDsXLlwBlNXfzjOIi"),
    "Meerschweinchen": ("drive_animal_meerschweinchen", "1FyTzJaj6LPobnv7vtL21HIGBEBoSwGj9"),
    "Giraffe": ("drive_animal_giraffe", "1dY8DU3iyMTSMjtQV6T_Xq9nICPfMQ7gE"),
    "Löwe": ("drive_animal_loewe", "1qaOzfr8zPAgsLrjBapAPu_5n21rskoav"),
    "Gorilla": ("drive_animal_gorilla", "1xv0v9JGH8_MonCZ8VHQQObH8oVLmohZH"),
    "Meeresschildkröte": ("drive_animal_meeresschildkroete", "1hrKykSgV1M0GXFvCai1RFFvh7hvOqKEB"),
    "Igel": ("drive_animal_igel", "1L38KN3GMpRpyK-NTflQclF3kfquzZD8N"),
    "Tiger": ("drive_animal_tiger", "1p-H9uvKVLiqTcPYylglu0gY7CXzDwJ10"),
    "Wolf": ("drive_animal_wolf", "12GeJ-n2mclR95sBUMN5mibAB-aKRgl0o"),
    "Adler": ("drive_animal_adler", "1fCfXDv3AHX7H4Ok0lzkLJEMljJNC9gFI"),
    "Delfin": ("drive_animal_delfin", "13Su4uahMCtcR_go8ISw0PPEVTxPEqN7P"),
    # Hobbies
    "Töpfern": ("drive_hobby_toepfern", "1GkNLDGgQHx-oDEEuugHFl7-J-PhO7jYt"),
    "Klavier spielen": ("drive_hobby_klavier", "1Yxiv1nGmlAXZ2IWHNj1kxi9vNzyiSIBR"),
    "Malen": ("drive_hobby_malen", "1X2X1v7t0Q8bvp7qxtF-vATVxValiy6YU"),
    "Zeichnen": ("drive_hobby_zeichnen", "1urmdCtNzoUVzbWw9ecMDkxfRtjTcKk9b"),
    "Badminton": ("drive_hobby_badminton", "1Ohof3i_WG4JYuujAJwdV8ivu_5Hyxw9j"),
    "Mountainbike": ("drive_hobby_fahrrad", "1sddcn-jHLXxg_KPjkoSJJk5w7Ikwi2dN"),
    "Bowling": ("drive_hobby_bowling", "11cNw27dxWqVIIxYqA95eNz1mKF56FBi8"),
    "Holzwerken": ("drive_hobby_diy", "1oCnLtOPndgcmkxMNJKC--DE0_GTW8nSw"),
    "Gitarre spielen": ("drive_hobby_gitarre", "16t4i_g9CGLPxVksvvxlpXFnoEAvaBl6U"),
    "Tennis": ("drive_hobby_tennis", "1o7vQBg0CY0yRr6jUVFpK3sG5oHjz20VQ"),
    "Brettspiele": ("drive_hobby_brettspiel", "1KU-03kv3yoOGitNtHnVymhgBAFvkn2Ud"),
    "Darts": ("drive_hobby_dart", "1F5oq0qWYnpZ-YeRdvwEVIzZarJe1A5lY"),
}


def download_drive_file(file_id: str) -> bytes:
    urls = (
        f"https://drive.usercontent.google.com/download?id={file_id}&export=download&confirm=t",
        f"https://drive.google.com/uc?export=download&id={file_id}&confirm=t",
    )
    last_error: Exception | None = None
    for attempt in range(3):
        for url in urls:
            try:
                request = urllib.request.Request(
                    url,
                    headers={"User-Agent": "Mozilla/5.0 Harmony-Asset-Materializer/1.0"},
                )
                with urllib.request.urlopen(request, timeout=45) as response:
                    payload = response.read()
                if len(payload) < 1024:
                    raise RuntimeError(f"Drive response too small ({len(payload)} bytes)")
                return payload
            except (urllib.error.URLError, urllib.error.HTTPError, TimeoutError, RuntimeError) as error:
                last_error = error
        time.sleep(2 * (attempt + 1))
    raise RuntimeError(f"Could not download Drive file {file_id}: {last_error}")


def materialize_one(option: str, drawable: str, file_id: str) -> tuple[str, Path]:
    output = DRAWABLE_DIR / f"{drawable}.webp"
    if output.exists() and output.stat().st_size > 1024:
        return option, output

    payload = download_drive_file(file_id)
    try:
        with Image.open(io.BytesIO(payload)) as source:
            source.load()
            image = source.convert("RGBA" if "A" in source.getbands() else "RGB")
    except Exception as error:  # Pillow gives the most useful validation here.
        prefix = payload[:80]
        raise RuntimeError(
            f"Drive file {file_id} for {option!r} is not a readable image; first bytes={prefix!r}"
        ) from error

    image.thumbnail((448, 448), Image.Resampling.LANCZOS)
    output.parent.mkdir(parents=True, exist_ok=True)
    image.save(output, format="WEBP", quality=72, method=6)
    if output.stat().st_size <= 1024:
        raise RuntimeError(f"Generated drawable {output.name} is unexpectedly small")
    return option, output


def patch_provider() -> int:
    original = PROVIDER.read_text(encoding="utf-8")
    lines = original.splitlines()
    replaced: set[str] = set()
    patched: list[str] = []

    for line in lines:
        stripped = line.strip()
        did_replace = False
        for option, (drawable, _) in ASSETS.items():
            prefix = f'"{option}" to "https://loremflickr.com/'
            if stripped.startswith(prefix):
                patched.append(f'        "{option}" to R.drawable.{drawable},')
                replaced.add(option)
                did_replace = True
                break
        if not did_replace:
            patched.append(line)

    result = "\n".join(patched) + ("\n" if original.endswith("\n") else "")
    if "loremflickr.com" in result:
        raise RuntimeError("Provider still contains loremflickr placeholders after targeted migration")

    missing = [option for option, (drawable, _) in ASSETS.items() if f"R.drawable.{drawable}" not in result]
    if missing:
        raise RuntimeError(f"Provider is missing local drawable mappings for: {missing}")

    if replaced and len(replaced) != len(ASSETS):
        not_replaced = sorted(set(ASSETS) - replaced)
        raise RuntimeError(f"Only replaced {len(replaced)}/{len(ASSETS)} placeholders; missing: {not_replaced}")

    if result != original:
        PROVIDER.write_text(result, encoding="utf-8")
    return len(replaced)


def main() -> None:
    DRAWABLE_DIR.mkdir(parents=True, exist_ok=True)

    provider_text = PROVIDER.read_text(encoding="utf-8")
    already_done = (
        "loremflickr.com" not in provider_text
        and all((DRAWABLE_DIR / f"{drawable}.webp").exists() for drawable, _ in ASSETS.values())
    )
    if already_done:
        print(f"All {len(ASSETS)} Drive-backed TOT assets are already materialized.")
        return

    failures: list[str] = []
    with ThreadPoolExecutor(max_workers=6) as executor:
        futures = {
            executor.submit(materialize_one, option, drawable, file_id): option
            for option, (drawable, file_id) in ASSETS.items()
        }
        for future in as_completed(futures):
            option = futures[future]
            try:
                _, output = future.result()
                print(f"materialized {option}: {output.name} ({output.stat().st_size} bytes)")
            except Exception as error:
                failures.append(f"{option}: {error}")

    if failures:
        raise RuntimeError("Drive asset materialization failed:\n - " + "\n - ".join(failures))

    replaced = patch_provider()
    print(f"Patched TotImageProvider: {replaced} loremflickr mappings -> bundled R.drawable mappings.")
    print("Gourmet-Eis was not modified.")


if __name__ == "__main__":
    main()
