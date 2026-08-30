# Eure Mischung — geparkter Zwischenstand

**Status:** PAUSED / aus dem produktiven App-Pfad entfernt

Dieser Ordner konserviert den letzten vollständigen Entwicklungsstand von **„Eure Mischung“**, bevor das Feature aus dem Release-Pfad genommen wurde.

## Quelle
- Arbeitsbranch: `fix/eure-mischung-state-restoration`
- archivierter Head: `309804ee7f718b3f524a72206c0edbd938dc2458`
- PR: #195 — `Fix: Eure Mischung bei Rotation vollständig wiederherstellen`
- PR wurde bewusst **nicht** in `main` gemergt und nach dem Archivieren geschlossen.

## Archivierte Dateien
- `EureMischungScreen.kt` — kompletter UI-/Generierungsflow inklusive letztem State-Restoration-Stand
- `EureMischungSessionState.kt` — Saveable-State und Wiederherstellung bereits erzeugter lokaler Bilder
- `EureMischungStateRestorationContractTest.kt` — zugehöriger Regressionstest
- `GeminiImageService.kt` — Snapshot der gemeinsam verwendeten Bildgenerierungs-Abhängigkeit zum Zeitpunkt des Parkens

## Wichtige Abhängigkeit
`GeminiImageService.kt` bleibt auch im produktiven App-Code erhalten, weil der separate `KidGeneratorScreen` Teile dieses Services weiter verwendet. Der Snapshot hier dient nur dazu, den damaligen Eure-Mischung-Stand vollständig nachvollziehen zu können.

## Frühere Integration
Das Feature war über `HomeScreen` und `MainActivity` erreichbar. Beim Wiederaufnehmen müssen diese Einstiegspunkte gegen den dann aktuellen Appstand neu eingebaut werden; alte MainActivity-/HomeScreen-Snapshots sollen nicht blind zurückkopiert werden.

## Wiederaufnahme
1. neuen Branch aus dem dann aktuellen `main` erstellen
2. `EureMischungScreen.kt` und `EureMischungSessionState.kt` aus diesem Archiv selektiv übernehmen
3. aktuelle `GeminiImageService`-API mit dem archivierten Snapshot vergleichen
4. Einstieg in Home/MainActivity neu und konfliktfrei verdrahten
5. Rotation/Activity-Recreation, Fotoauswahl, Generierung, Ergebnis, Galerie/Momente/Teilen vollständig testen
6. erst danach über einen neuen PR wieder für `main` freigeben
