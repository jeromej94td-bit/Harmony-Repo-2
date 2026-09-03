# Herbstabend – Übergabe

Branch: `feature/herbstabend`
Repository: `https://github.com/jeromej94td-bit/Harmony-Repo-2.git`

## Bereits enthalten

- `herbstabend`-Pack in `HarmonyPacksData.DEFAULT_PACKS` mit sechs Fragen und je vier Antworten.
- Stabile Bildrunden-Zuordnung in `HarmonyImageChoicePolicy.kt` über Pack-ID und Index.
- Test-first-Verträge für Pack und Routing.
- Asset-Vertragstest für 24 geplante PNG-Karten.
- Vollständiger Design-Spec und Implementierungsplan.
- Sieben freigegebene Bildschirm-Mockups unter `docs/superpowers/references/herbstabend-design/`.

## Noch offen

- 24 eigenständige, textfreie, fotorealistische Produktionskarten erzeugen und unter `app/src/main/res/drawable-nodpi/` ablegen.
- `AutumnEveningQuestion`-Compose-Renderer integrieren und in `HarmonyImageChoiceQuestion` verdrahten.
- Englische Übersetzungen ergänzen.
- Drink- und Fensternest-Roborazzi-Previews ausführen.
- `compileDebugKotlin`, `assembleDebug`, fokussierte Regressionstests und PR-Review durchführen.

Die vier `introspection_*_golden.mp3` unter `app/src/main/res/raw/` sind lokale Build-Artefakte und absichtlich nicht Bestandteil des Features oder Commits.
