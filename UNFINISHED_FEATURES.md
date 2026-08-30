# Unfinished Features

Dieser Branch dient als Sammelstelle für Features, Ideen und Zwischenstände, die bewusst eingefroren wurden und später wieder aufgenommen werden sollen.

## Regeln

- Nicht in `main` mergen, solange ein Eintrag hier den Status **PAUSED** oder **UNFINISHED** hat.
- Neue eingefrorene Features hier mit Status, Grund, letztem Stand und Wiederaufnahme-Punkt dokumentieren.
- Wenn bereits Code auf einem separaten Arbeitsbranch existiert, Branch und Commit hier referenzieren, damit nichts verloren geht.
- Bei Wiederaufnahme zuerst gegen den dann aktuellen `main` neu aufsetzen bzw. rebasen und anschließend normal über einen eigenen PR prüfen.

## 1. Baby-/Kid-Generator

**Status:** PAUSED

**Entscheidung:** Der ältere Baby-/Kid-Generator-Zwischenstand wird vorerst nicht weiterentwickelt.

**Vorhandener Zwischenstand:**
- Arbeitsbranch: `fix/generated-moment-image-persistence`
- letzter bekannter Commit: `3a1efd249ecd19a0cf49aa6a821f325987920ac1`
- dort begonnene Arbeit: erzeugte Bilder beim Speichern in „Momente“ dauerhaft übernehmen und den Moment-Datenpfad korrigieren

**Bekannte offene Punkte:**
- KidGenerator-Datenpfad noch nicht vollständig auf den neuen Moment-Speicherpfad umgestellt
- MainActivity-Verdrahtung für Bildpfad und Emoji noch nicht vollständig abgeschlossen
- Zwischenstand liegt hinter dem aktuellen `main` und darf nicht direkt gemergt werden

## 2. „Eure Mischung“

**Status:** PAUSED / AUS PRODUKTIVEM APP-PFAD ENTFERNT

**Entscheidung:** „Eure Mischung“ wird aus der aktiven App genommen, der letzte vollständige Stand aber separat konserviert.

**Vollständiges Archiv:** `unfinished/eure-mischung/`

**Archivierte Quelle:**
- Arbeitsbranch: `fix/eure-mischung-state-restoration`
- Head: `309804ee7f718b3f524a72206c0edbd938dc2458`
- PR #195: State-/Rotationsfix, bewusst nicht in `main` gemergt

**Im Archiv enthalten:**
- vollständiger `EureMischungScreen`
- letzter `EureMischungSessionState`
- State-Restoration-Regressionstest
- Snapshot der zu diesem Stand verwendeten `GeminiImageService`
- Wiederaufnahme-Dokumentation

**Wichtig:** Die produktive `GeminiImageService` wird nicht zusammen mit „Eure Mischung“ gelöscht, weil der separate KidGenerator diese gemeinsame technische Abhängigkeit weiterhin verwendet.

**Wiederaufnahme:**
1. aktuellen `main` prüfen
2. neuen Arbeitsbranch aus aktuellem `main` erstellen
3. Dateien selektiv aus `unfinished/eure-mischung/` übernehmen
4. Home-/MainActivity-Einstieg neu gegen den aktuellen Stand verdrahten
5. Generator- und State-Restoration-Flow vollständig testen
6. erst danach einen neuen Freigabe-PR erstellen

---

Weitere eingefrorene Ideen werden unterhalb dieses Abschnitts ergänzt.
