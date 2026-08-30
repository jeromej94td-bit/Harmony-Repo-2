# Happy Couple – verbindlicher UI-Vertrag

## Geltungsbereich

Dieser Vertrag gilt ausschließlich für **Beziehungen → Liebe im Gleichgewicht** und dort für die erste Frage:

> Welches Paar ist GLÜCKLICH?

## Inhalt und Ablauf

- Die Frage ist immer **Frage 1 von 11**.
- Es gibt genau vier lokale Regen-/Regenschirm-Karten in einem **2×2-Raster**.
- Jede gesamte Bildkarte ist direkt auswählbar und speichert die Antwort `1`, `2`, `3` oder `4`.
- Auf jeder Karte steht sichtbar ein runder Nummern-Pin: **1**, **2**, **3** oder **4**.
- Es gibt keine A–E-Antwortbuttons, keine Freitextantwort und keinen separaten Partner-Schritt.
- Nach der Auswahl folgen die zehn normalen Fragen des Packs.

## Visuelles Verhalten

- Oben im Fragebereich steht die Pille **„Frage 1 von 11“**.
- Die Karten haben einen Harmony-Neonrahmen.
- Bei Auswahl erhält die komplette Karte einen verstärkten Rahmen und pinken Glow.
- Die Karten erscheinen nacheinander: `index × 700 ms`.
- Die Karten-Flip-Animation dauert `620 ms`.

## Schutz gegen Regressionen

Die UI-Tests prüfen die vier Nummern-Pins, die vier Klickflächen, die Speicherung einer Auswahl und das Screenshot-Artefakt
`build/harmony-image-choice-preview/happy-couple-question.png`.
Die Routing-Tests stellen sicher, dass nur **„Welches Paar ist GLÜCKLICH?“** den Happy-Couple-Renderer erhält; die zweite
Frage des Packs bleibt eine normale Quizfrage.
