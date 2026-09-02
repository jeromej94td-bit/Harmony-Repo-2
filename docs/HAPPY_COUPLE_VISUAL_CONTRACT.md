# Happy Couple – verbindlicher UI-Vertrag

## Geltungsbereich

Dieser Vertrag gilt ausschließlich für **Beziehungen → Liebe im Gleichgewicht** und dort für die erste Frage:

> Welches Paar ist GLÜCKLICH?

## Inhalt und Ablauf

- Die Happy-Couple-Frage ist immer **Frage 1 von 11**.
- Es gibt genau vier lokale Regen-/Regenschirm-Karten in einem **2×2-Raster**.
- Jede gesamte Bildkarte ist direkt auswählbar und speichert ausschließlich die Antwort `1`, `2`, `3` oder `4`.
- Auf jeder Karte steht sichtbar ein runder Nummern-Pin: **1**, **2**, **3** oder **4**.
- Es gibt bei dieser Frage keine A–E-Antwortbuttons, keine Freitextantwort und keinen separaten Partner-Schritt.
- Nach der Auswahl folgen die zehn normalen Fragen des Packs.
- Auch ein veralteter/dynamischer Pack darf weder Fragetext noch Antwortwerte der Happy-Couple-Frage auf A–D oder andere Werte umbiegen. Der Renderer fällt auf den kanonischen Fragetext und `1`–`4` zurück.

## Visuelles Verhalten

- Im Happy-Couple-Fragebereich steht sichtbar die Pille **„Frage 1 von 11“**.
- Darunter steht groß **„Welches Paar ist GLÜCKLICH?“**.
- Der Untertext lautet: **„Wähle das Paar, das für dich am glücklichsten wirkt.“**
- Die vier Karten haben einen Harmony-Neonrahmen.
- Bei Auswahl erhält die komplette Karte einen verstärkten Rahmen und pinken Glow; der Nummern-Pin wird ebenfalls hervorgehoben.
- Die Karten erscheinen nacheinander: `index × 700 ms`.
- Die Karten-Flip-Animation dauert `620 ms`.

## Verbindliche Runtime-Dateien

- `app/src/main/java/com/example/data/GeneratedHarmonyHappyCouple.kt`
- `app/src/main/java/com/example/data/GeneratedContentRegistry.kt`
- `app/src/main/java/com/example/ui/screens/HarmonyImageChoicePolicy.kt`
- `app/src/main/java/com/example/ui/screens/HarmonyHappyCoupleQuestion.kt`

## Regression-Schutz

- `HappyCoupleVisualContractTest` verlangt die sichtbare `Frage 1 von 11`-Pille, den kanonischen Fragetext und sichtbare Nummern `1`–`4`, auch wenn ein veralteter Runtime-Override anderen Text bzw. A–D liefert.
- Derselbe Test erzeugt das visuelle Referenzartefakt `build/happy-couple-preview/happy-couple-question.png`.
- `HappyCoupleNumberPickRegressionTest` schützt Klickflächen, Auswahlzustand und die Rückgabe `1`–`4`.
- `HappyCoupleQuizRunnerRoutingRegressionTest` schützt die Route des echten Quiz-Runners.
- Die bestehenden Love-Balance-Runtime-Tests schützen die Position als erste von insgesamt elf Fragen und die Reparatur alter 10er-Custom-Packs.

## Nicht zulässige Regressionen

Folgende Zustände gelten ausdrücklich als Fehler:

- `1/10` statt elf Fragen,
- eine normale A–E-Frage als erste Frage von „Liebe im Gleichgewicht“,
- unsichtbare Nummern-Tags ohne tatsächlich sichtbare `1`–`4`,
- Happy-Couple auf einer späteren Frage des Packs,
- nur teilweise anklickbare Bildkarten,
- dynamische A–D-Werte anstelle von `1`–`4`.
