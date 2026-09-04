# Herbstabend – Übergabe

Branch: `feature/herbstabend`
Repository: `https://github.com/jeromej94td-bit/Harmony-Repo-2.git`
Pull Request: `#269`

## Bereits enthalten

- `herbstabend`-Pack in `HarmonyPacksData.DEFAULT_PACKS` mit sechs Fragen und je vier Antworten.
- Stabile Bildrunden-Zuordnung in `HarmonyImageChoicePolicy.kt` über Pack-ID und Index.
- 24 eigenständige Produktionskarten unter `app/src/main/res/drawable-nodpi/` (`autumn_story_*`, `autumn_drink_*`, `autumn_snack_*`, `autumn_nook_*`, `autumn_sound_*`, `autumn_scent_*`).
- Dedizierter `AutumnEveningQuestion`-Compose-Renderer, verdrahtet über `HarmonyImageChoiceQuestion`.
- 2×2-Fotokarten, 22-dp-Radien, Aubergine/Blackberry-Grundfläche, Kupfer/Roségold-Auswahlzustand, gestaffelte Eintrittsanimation, Haptik und Accessibility-Semantik.
- Exakte englische Herbstabend-Texte für Titel, sechs Fragen, 24 Optionen und sechs Untertitel über `TranslationCatalog`.
- Test-first-Verträge für Pack, Routing, Assets, Renderer und Übersetzung.
- Robolectric/Roborazzi-Visualvertrag für Getränk und Fensternest inklusive Auswahlzustand und Preview-Capture.
- Vollständiger Design-Spec und Implementierungsplan.
- Sieben freigegebene Bildschirm-Mockups unter `docs/superpowers/references/herbstabend-design/`.
- Dedizierter GitHub-Actions-Check `Herbstabend Verify` für die fokussierten Herbstabend-Tests und `compileDebugKotlin`.

## Verifikation / noch zu prüfen

- PR `#269` muss die laufenden GitHub-Actions-Checks erfolgreich abschließen.
- Der vorhandene Android-PR-Workflow führt `:app:assembleDebug` gegen `main` aus.
- `Herbstabend Verify` führt Pack-, Routing-, Asset-, Renderer-, Übersetzungs- und Visualtests sowie `:app:compileDebugKotlin` aus.
- Die sieben Referenz-Mockups und 24 Produktions-PNGs sind im Repository vorhanden. Eine pixelgenaue visuelle Qualitätsprüfung der Binärbilder wurde in der Connector-Sitzung nicht vorgetäuscht; sie erfolgt über die erzeugten Roborazzi-Previews bzw. eine Umgebung, die die PNG-Pixel rendern kann.

Die vier `introspection_*_golden.mp3` unter `app/src/main/res/raw/` sind lokale Build-Artefakte und absichtlich nicht Bestandteil des Features oder Commits.
