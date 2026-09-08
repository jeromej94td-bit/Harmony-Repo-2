# Der Geheime Plan Visual Upgrade - Arbeits-Prompt

> Diesen Prompt an einen anderen Agenten oder ein günstigeres Modell geben, wenn die visuelle Umsetzung von "Der geheime Plan" weitergebaut oder geprüft werden soll.

## Ziel

Baue das Harmony-Spiel **Der Geheime Plan** grafisch deutlich hochwertiger aus. Es darf nicht wie ein Platzhalter wirken. Die Auswahlantworten sollen in einer magischen Buchszene dargestellt werden, mit farbenfrohem Harmony-Stil, weichen Leuchteffekten und einer sichtbaren, sanften Antwort-Animation.

## Wichtigste Vorgabe

Nicht nur UI-Karten oder Textlisten bauen. Dieses Spiel braucht eine echte visuelle Szene:

- ein helles, warmes magisches Buch als zentrale Bühne
- Antwortoptionen als Buchseiten, Tabs oder kleine Papierstreifen
- ein leuchtendes Herz-/Siegel-Element
- sanfte Animation beim Auswählen
- keine dunkle, monotone violette Fläche als Hauptwirkung
- keine generischen Platzhaltergrafiken
- keine überladenen Texte

## Ausgewähltes Design

Verwende das Konzept **magisches Buch / geheimer Plan**:

- Die Antwort liegt als Seite im Buch.
- Beim Antippen hebt sich die Seite leicht an.
- Danach klappt oder dreht sie sich sanft.
- Am Ende wird sie in ein leuchtendes Herzsiegel oder Kapitel-Siegel verwandelt.
- Danach geht das Spiel zur nächsten Frage oder zum Ergebnis weiter.

Die Optik soll heller und farbenfroher sein als die erste dunkle Version:

- Grundstimmung: Harmony, romantisch, weich, hochwertig
- Farben: Lavendel, Rosé, warmes Gold, Creme, sanftes Türkis als Akzent
- Licht: weiches Leuchten, Pulsieren, Aurora-Glow
- Kein harter Neon-Look, kein greller Vollbild-Flash

## Konkrete Animation

Die Antwortauswahl darf nicht sofort hart weiter springen. Die Animation soll ungefähr 2 Sekunden dauern.

Empfohlene Timeline:

- 0-300 ms: Antwort hebt sich an und bekommt mehr Glow
- 300-1400 ms: Seite klappt/dreht sich wie eine Buchseite
- 1400-1850 ms: Seite verwandelt sich in ein leuchtendes Herz-/Kapitel-Siegel
- 1850-2000 ms: sanftes Auspendeln, dann erst nächste Frage

Während dieser Zeit dürfen keine weiteren Antworten angenommen werden.

## Inhaltliches Spielprinzip

Das Spiel zeigt Auswahlantworten. Es soll später auch Antworten einbauen können, die Nutzer vorher selbst geschrieben haben. Für den ersten Ausbau reichen Auswahlantworten.

Beispiele für passende Fragen:

- "Wofür nehmt ihr euch spontan einen freien Tag?"
- "Wo würdest du deinen Partner am liebsten überraschen?"
- "Welche kleine Idee würde euren Abend sofort schöner machen?"
- "Was sollte in eurem geheimen Plan auf keinen Fall fehlen?"

Beispiele für Antwortoptionen:

- "Kleiner Roadtrip"
- "Zeit nur für uns"
- "Etwas Neues erleben"
- "Ein Herzensprojekt starten"
- "Eigene Idee ..."

Wichtig: Die Antwortoptionen müssen in der Grafik/Komposition gut sitzen. Nicht einfach eine normale Formularliste darunter kleben.

## Textregeln

Wenig Text. Keine langen Erklärtexte im Spielscreen.

Vermeiden:

- "damals gewählt"
- lange Hilfetexte
- erklärende Sätze wie "weiter zum nächsten Kapitel"
- überladene Untertitel

Erlaubt:

- kurzer Spieltitel
- eine kurze Frage
- Antwortoptionen
- dezente Kapitelanzeige

## Technische Ziel-Dateien

Arbeite im Harmony-Repo:

`C:\Users\Ralfg\StudioProjects\Harmony-Repo-2`

Aktueller Arbeits-Worktree für diese Umsetzung:

`C:\Users\Ralfg\StudioProjects\Harmony-Repo-2\.worktrees\secret-plan-book-motion`

Voraussichtlich relevante Dateien:

- `app/src/main/java/com/example/ui/screens/SecretPlanBoard.kt`
- `app/src/main/java/com/example/ui/screens/SecretPlanMotionTimeline.kt`
- `app/src/test/java/com/example/ui/screens/SecretPlanMotionTimelineTest.kt`
- `app/src/main/res/drawable-nodpi/secret_plan_magic_book.webp`
- `design-qa.md`

Bereits vorhandenes Bildasset, wenn es existiert:

`app/src/main/res/drawable-nodpi/secret_plan_magic_book.webp`

## Umsetzungshinweise für Android Compose

- Nutze das echte Buchbild als Hauptszene.
- Lege Antwortflächen über das Buch, aber so, dass sie wie Teil der Buchseiten wirken.
- Nutze `Animatable` oder Compose Transitions für Auswahlbewegung.
- Nutze `graphicsLayer` für:
  - `translationY`
  - `rotationZ`
  - `rotationX` oder `rotationY`, falls stabil
  - `scaleX` / `scaleY`
  - `alpha`
- Sperre Klicks während `pendingChoice != null`.
- Der Fortschritt zur nächsten Frage darf erst nach Ende der Animation erfolgen.
- Die finale Ergebnisansicht soll ebenfalls hochwertig aussehen und nicht auf eine normale Textkarte zurückfallen.

## Tests

Mindestens diese Prüfung muss bleiben oder ergänzt werden:

`SecretPlanMotionTimelineTest`

Sie soll absichern:

- bei 0 ms ist Stage `LIFT`
- bei 300 ms ist Stage `PAGE_TURN`
- bei 1400 ms ist Stage `SEAL`
- bei 1850 ms ist Stage `SETTLE`
- vor 2000 ms werden Eingaben gesperrt
- ab 2000 ms ist Stage `COMPLETE` und Eingaben sind wieder erlaubt
- negative Zeiten werden auf 0 ms geklemmt

Zusätzlich eine visuelle Prüfung erzeugen:

- Roborazzi- oder Screenshot-Test für den Auswahlscreen
- Screenshot im Animationszustand bei etwa 900 ms
- Bilder danach wirklich ansehen, nicht nur Build grün melden

## Abnahmekriterien

Die Arbeit ist erst fertig, wenn alle Punkte erfüllt sind:

- Das Spiel sieht sichtbar nach einer gestalteten Szene aus, nicht nach Platzhalter-UI.
- Das Buchbild ist sichtbar und korrekt platziert.
- Antwortoptionen sind lesbar.
- Die ausgewählte Antwort animiert sichtbar über ca. 2 Sekunden.
- Während der Animation sind weitere Klicks gesperrt.
- Die Ergebnisansicht passt zum gleichen visuellen Stil.
- Mindestens der Motion-Test läuft grün.
- Ein Screenshot wurde erzeugt und visuell geprüft.
- `design-qa.md` existiert und sagt `final result: passed` oder erklärt klar, was blockiert.

## Verbotene Abkürzungen

Nicht akzeptieren:

- "Build erfolgreich" ohne Screenshotprüfung
- graue Karten als Ersatz für die Buchszene
- reines Textlayout ohne Bildasset
- generische Icons statt echter visueller Komposition
- plötzlich dunkler Vollbild-Look ohne Harmony-Farbigkeit
- harte Sofortnavigation nach Antwortklick
- Commit von `local.properties`, Keystores, APKs oder generierten Raw-Audio-Dateien

## GitHub-Regel

Wenn die Umsetzung fertig und geprüft ist:

- nur beabsichtigte Dateien committen
- keine lokalen Secrets oder Build-Ausgaben committen
- Branch pushen
- PR gegen `main` erstellen
- im PR beschreiben:
  - welches Spiel verbessert wurde
  - welche Animation eingebaut wurde
  - welche Tests gelaufen sind
  - welche Screenshot-/Design-QA durchgeführt wurde

## Kurzprompt für anderen Agenten

Setze im Harmony-Repo das Spiel "Der Geheime Plan" als hochwertige magische Buchszene um. Verwende ein echtes helles Buch-/Herz-Asset, farbenfrohen Harmony-Stil mit Lavendel, Rosé, Gold und sanftem Glow. Antwortoptionen liegen als Buchseiten/Tabs im Buch. Beim Antippen hebt sich die Antwort an, klappt wie eine Seite und verwandelt sich nach ca. 2 Sekunden in ein leuchtendes Herz-/Kapitel-Siegel. Während der Animation sind weitere Klicks gesperrt. Kein Platzhalterlook, keine reine Textliste, keine dunkle Monofläche. Ergänze Motion-Tests und eine echte Screenshot-/Design-QA. Erst als fertig melden, wenn der Screenshot visuell geprüft wurde und `design-qa.md` das Ergebnis dokumentiert.
