# Unfinished Features

Dieser Branch dient als Sammelstelle für Features, Ideen und Zwischenstände, die bewusst eingefroren wurden und später wieder aufgenommen werden sollen.

## Regeln

- Nicht in `main` mergen, solange ein Eintrag hier den Status **PAUSED** oder **UNFINISHED** hat.
- Neue eingefrorene Features hier mit Status, Grund, letztem Stand und Wiederaufnahme-Punkt dokumentieren.
- Wenn bereits Code auf einem separaten Arbeitsbranch existiert, Branch und Commit hier referenzieren, damit nichts verloren geht.
- Bei Wiederaufnahme zuerst gegen den dann aktuellen `main` neu aufsetzen bzw. rebasen und anschließend normal über einen eigenen PR prüfen.

## 1. Baby-/Kid-Generator / „Eure Mischung“

**Status:** PAUSED

**Entscheidung:** Der Baby-/Kid-Generator wird vorerst nicht weiterentwickelt und nicht in den produktiven Harmony-Stand gemergt.

**Vorhandener Zwischenstand:**
- Arbeitsbranch: `fix/generated-moment-image-persistence`
- letzter bekannter Commit: `3a1efd249ecd19a0cf49aa6a821f325987920ac1`
- dort begonnene Arbeit: erzeugte Bilder beim Speichern in „Momente“ dauerhaft übernehmen und den Moment-Datenpfad korrigieren

**Bekannte offene Punkte:**
- KidGenerator-Datenpfad noch nicht vollständig auf den neuen Moment-Speicherpfad umgestellt
- MainActivity-Verdrahtung für Bildpfad und Emoji noch nicht vollständig abgeschlossen
- Zwischenstand liegt hinter dem aktuellen `main` und darf nicht direkt gemergt werden

**Wiederaufnahme:**
1. aktuellen `main` prüfen
2. neuen Arbeitsbranch aus aktuellem `main` erstellen
3. nur die noch sinnvollen Teile aus dem alten Zwischenstand übernehmen
4. Generator-Flow vollständig testen
5. erst danach neuen PR erstellen

---

Weitere eingefrorene Ideen werden unterhalb dieses Abschnitts ergänzt.
