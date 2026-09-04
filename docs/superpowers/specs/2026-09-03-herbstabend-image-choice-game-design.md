# Unser Herbstabend – Design für ein fotorealistisches Bildauswahlspiel

**Datum:** 2026-09-03  
**Status:** Visuelle Richtung freigegeben; technische Spezifikation zur Prüfung  
**Ziel-Repository:** Harmony-Repo-2  
**Feature-Branch:** `feature/herbstabend`

## Ziel

Harmony erhält ein neues saisonales Paarspiel namens **„Unser Herbstabend“**. In sechs aufeinanderfolgenden Bildfragen stellt der spielende Partner einen persönlichen Herbstabend zusammen. Jede Stufe zeigt vier klar unterscheidbare, fotorealistische Auswahlkarten. Die Karten erscheinen nacheinander mit derselben räumlichen Bewegungsqualität wie die vorhandenen Ei-/Steak-Bildfragen, erhalten aber ein eigenständiges warmes Herbstbild.

Das Spiel funktioniert vollständig offline. Es verwendet weder Supabase noch Gemini und versendet keine Bild- oder Antwortdaten. Speicherung, Fortsetzen, Partner-Synchronisation und spätere Ergebnisanzeige bleiben in der bestehenden Harmony-Infrastruktur.

## Katalog und Inhalt

Das Spiel wird als reguläres `QuestionPack` registriert:

- ID: `herbstabend`
- Titel: `Unser Herbstabend`
- Kategorie: `lieber` (`Was magst du lieber?`)
- Thema: `hobbys`
- Typ: `quiz`
- Symbol: `🍂`
- Tags: `herbst`, `cozy`, `fürpaare`, `bildauswahl`

Die sechs Fragen und ihre Reihenfolge sind verbindlich:

1. **Welche Geschichte zieht dich in den Herbst?**
   - Mystery
   - Thriller
   - Dark Academia
   - Cozy Fantasy
2. **Was wärmt deinen Abend?**
   - Chai Latte
   - Heiße Schokolade
   - Apfel-Zimt-Tee
   - Pumpkin Spice
3. **Welcher Snack gehört dazu?**
   - Zimtschnecke
   - Chocolate Cookie
   - Kürbismuffin
   - Apfelkuchen
4. **Wo wird es richtig gemütlich?**
   - Fensternest
   - Kaminsofa
   - Deckenhöhle
   - Bibliotheksecke
5. **Welcher Klang begleitet euch?**
   - Regen am Fenster
   - Kaminfeuer
   - Herbstwind
   - Völlige Ruhe
6. **Welcher Duft macht es vollkommen?**
   - Vanille & Holz
   - Herbstlaub
   - Kürbisgewürz
   - Bratapfel

## Routing und Komponenten

Die Bilddarstellung wird ausschließlich über die stabile Pack-ID `herbstabend` und den expliziten Fragenindex `0..5` gewählt. Fragetexte werden nicht als Routing-Schlüssel verwendet. Dadurch bleiben importierte, übersetzte oder später redaktionell angepasste Texte sicher, und die Bildoberfläche kann nicht versehentlich bei einem anderen Paket erscheinen.

`HarmonyImageChoiceKind` erhält sechs Herbst-Kinds:

- `AUTUMN_STORY`
- `AUTUMN_DRINK`
- `AUTUMN_SNACK`
- `AUTUMN_NOOK`
- `AUTUMN_SOUND`
- `AUTUMN_SCENT`

Die sechs Kinds werden in `HarmonyImageChoicePolicy` durch eine einzige packgebundene Index-Zuordnung aufgelöst. Ein dedizierter `AutumnEveningQuestion`-Renderer übernimmt nur die Herbstoberfläche. Der bestehende `QuizRunnerScreen` bleibt Eigentümer von Navigation, Fortschritt, Speichern, Fortsetzen, Überspringen, Abschluss und Exit-Bestätigung.

## Visuelles System

Jede Frage zeigt eine ruhige 2×2-Anordnung aus vier großen, touchfreundlichen Fotokarten. Die Bildkarten verwenden:

- echtes, redaktionelles Fotolicht statt Illustration oder Emoji-Art;
- tiefe Aubergine-/Blackberry-Flächen als Harmony-Basis;
- gedämpfte Kupfer- und Roségold-Akzente;
- cremefarbene, deutlich lesbare Schrift;
- konsistente Radien, Zuschnitte und Kartenproportionen;
- zurückhaltenden Aurora-Nebel und feine Körnung;
- einen warmen Auswahlrahmen und ein kleines Auswahlzeichen;
- keine verschachtelten Glaskarten, keine grellen Verläufe und keine zufälligen Dekoelemente.

Die Raumstufe verwendet die freigegebene mittlere Belichtung: ein gedämpfter, warmer Oktober-Nachmittag. Sie darf weder düster noch sonnig-hell wirken. Beim **Fensternest** sind realistische Regentropfen und vertikale Wasserläufe auf der Scheibe zwingend sichtbar.

Die Texte stehen als echte Compose-Texte über beziehungsweise unter den Bildern und werden nicht in Bilddateien eingebrannt. Das schützt Lesbarkeit, Übersetzbarkeit und Barrierefreiheit.

## Bildassets

Die Produktion benötigt 24 eigenständige, fotorealistische Kartenbilder in `app/src/main/res/drawable-nodpi/`:

- `autumn_story_01..04`
- `autumn_drink_01..04`
- `autumn_snack_01..04`
- `autumn_nook_01..04`
- `autumn_sound_01..04`
- `autumn_scent_01..04`

Alle Bilder einer Gruppe verwenden dasselbe Seitenverhältnis und einen stabilen zentralen Motivbereich. Keine Karte wird aus einem Screenshot oder einer Kontaktübersicht ausgeschnitten. Die bestehenden freigegebenen Bildschirmmockups dienen nur als Qualitäts- und Belichtungsreferenz; die App erhält separat erzeugte, textfreie Produktionsbilder.

Die Bilddateien werden vor dem Commit auf sinnvolle Pixelmaße und Dateigröße optimiert, ohne sichtbare Artefakte zu erzeugen. Sie enthalten keine Markenlogos, Wasserzeichen, Personen oder urheberrechtlich erkennbare Film-/Buchfiguren.

## Animation und Interaktion

Beim Eintritt in eine Stufe erscheinen die vier Karten gestaffelt von links nach rechts und von oben nach unten. Die Bewegung kombiniert kurze horizontale Verschiebung, sanfte Y-Rotation, Skalierung und Einblendung. Während der Eintrittsbewegung sind Karten noch nicht antippbar.

Bei Auswahl:

- hebt sich die gewählte Karte minimal an;
- der Kupfer-/Roségold-Rahmen wird sichtbar;
- ein dezentes Häkchen bestätigt die Auswahl;
- eine kurze vorhandene Harmony-Vibration wird ausgelöst;
- die Antwort wird über den bestehenden `onPickAnswer`-Pfad gespeichert.

Es gibt keine automatische Weiterschaltung unmittelbar beim ersten Touch, falls der vorhandene Runner eine bestätigende Weiter-Aktion vorsieht. Navigation und Wiederaufnahme folgen exakt dem bestehenden Quizverhalten.

## Abschluss und Paarverhalten

Nach Frage sechs verwendet das Paket zunächst den bestehenden sicheren Quiz-Abschluss. Die eigenen Antworten werden gespeichert. In einer gekoppelten Sitzung bleiben Partnerdaten durch den vorhandenen Synchronisations- und Reveal-Pfad geschützt und werden nicht vorzeitig in einer neuen Sonderoberfläche offengelegt.

Die freigegebene Ergebnisgrafik definiert die spätere visuelle Richtung, aber diese erste Implementierung erzeugt **kein** künstlich zusammengesetztes Ergebnisfoto, das möglicherweise nicht zu den tatsächlich gewählten Optionen passt. Die tatsächlichen sechs Antworten erscheinen über die bestehende Pack-Ergebnisansicht. Eine spätere individuelle Ergebniszusammenfassung kann separat ergänzt werden, sobald ihre Paar- und Synchronisationslogik eigenständig spezifiziert ist.

## Lokalisierung und Barrierefreiheit

Deutsch ist die verbindliche Ausgangssprache. Die neue sichtbare Kopie erhält mindestens vollständige englische Einträge im vorhandenen Übersetzungssystem. Andere Sprachen dürfen gemäß der bestehenden atomaren Fallback-Regel auf den vollständigen deutschen String zurückfallen; es werden keine teilweise übersetzten Mischtexte erzeugt.

Jede Bildkarte bekommt eine aussagekräftige Semantik aus lokalisiertem Optionsnamen und Auswahlzustand. Touchflächen bleiben mindestens 48 dp groß. Kontrast und Textgröße müssen bei normaler Android-Schriftgröße gut lesbar sein. Die Funktion darf bei großer Schrift nicht abstürzen oder Optionen unzugänglich machen; Scrollen bleibt über den Runner möglich.

## Fehlergrenzen

- Eine unbekannte Herbst-Fragenposition liefert keinen Herbst-Renderer.
- Stimmen Options- und Assetanzahl nicht überein, muss ein fokussierter Test fehlschlagen; es wird kein falsches Bild stillschweigend wiederverwendet.
- Fehlende oder beschädigte Assets werden beim Build erkannt.
- Das neue Routing darf keine bestehende Ei-, Steak-, Reise-, Traumhaus-, Happy-Couple- oder Fullscreen-Mechanik verändern.
- Authentifizierung, Supabase, Audio, Video, Datenbankmigrationen und Dev Studio liegen außerhalb dieses Features.

## Tests und Verifikation

Die Implementierung wird testgetrieben abgesichert:

1. Pack-Vertragstest für ID, Kategorie, Thema, sechs Fragen und exakt vier Optionen je Frage.
2. Routing-Test für `herbstabend` Index `0..5` sowie negative Fälle für andere IDs und ungültige Indizes.
3. Asset-Vertragstest für vier eindeutige Drawables je Herbst-Kind.
4. Animationstest für die stabile Kartenreihenfolge und Verzögerungen.
5. Compose-/Roborazzi-Test mindestens für Getränk und die freigegebene Raumstufe; alle Optionen und Auswahlzustand müssen sichtbar sein.
6. Gezielter Unit-Testlauf für die neuen Tests.
7. `compileDebugKotlin` und `assembleDebug` mit dem vorhandenen JDK 21 und Android SDK.

Der unveränderte Stand von `origin/main` kompiliert, besitzt am 2026-09-03 aber mehrere bereits vorhandene, fachfremde Vertragsfehler in Auth-, Signierungs- und Spezial-Flow-Tests; der vollständige Baseline-Lauf blieb danach in einem Test-Worker stehen. Diese Altfehler werden nicht im Herbstspiel-Branch behoben. Neue gezielte Tests müssen grün sein, und jeder Unterschied zum Baseline-Zustand wird separat berichtet.

## Abnahmekriterien

- „Unser Herbstabend“ ist im bestehenden Spielekatalog auffindbar und startbar.
- Alle sechs Stufen erscheinen in der festgelegten Reihenfolge.
- Jede Stufe zeigt vier logisch passende, hochwertige, fotorealistische Bilder.
- Die Raumstufe besitzt die freigegebene herbstlich gedämpfte Belichtung und sichtbaren Regen am Fensternest.
- Karten erscheinen animiert und reagieren eindeutig auf Auswahl.
- Antworten werden über den bestehenden lokalen und gekoppelten Harmony-Pfad gespeichert.
- Andere Bildspiele, Login, Audio, Video und Supabase bleiben unverändert.
- Die neuen fokussierten Tests, Kotlin-Kompilierung und Debug-Assembly bestehen.
