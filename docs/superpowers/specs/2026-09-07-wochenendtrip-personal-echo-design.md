# Wochenendtrip - Personal Echo Design

## Ziel

`h500_076_wochenendtrip_szenario` wird von einer generischen Szenarioansicht zu einer persoenlichen Reise durch bereits beantwortete Harmony-Auswahlfragen. Eine Runde zeigt genau zwei fruehere Auswahlmotive, die inhaltlich zur aktuellen Wochenendtrip-Situation passen. Erst nachdem beide Personen ihre neue Wahl getroffen haben, wird die Verbindung zur frueheren Antwort kurz aufgedeckt.

Die erste Version verwendet ausschliesslich vorhandene Auswahlantworten. Freitext, Audio, Fotos, Memory-Match-Inhalte und sensible offene Antworten sind ausgeschlossen.

## Verbindliche acht Motive

Die erste Version besitzt einen kleinen, voll kuratierten Katalog. Jedes Motiv entspricht einer bereits existierenden Antwort aus `entweder_oder_panda` und bekommt ein eigenes lokales Bildmotiv.

| Schluessel | Gespeicherte Antworten | Bildmotiv | Relevante Situationen |
| --- | --- | --- | --- |
| `couch_blanket` | `Couch & Decke`, `Couch & Decke 🛋️` | warmes Filmzimmer mit Decken, Projektor und Snacks | Regen, Erschoepfung, Ruhe |
| `cinema` | `Kino`, `Kino 🎬` | leuchtendes Programmkino im Regen | Regen, Abendprogramm, gemeinsames Erlebnis |
| `city` | `Stadt`, `Stadt 🏙️` | farbige regennasse Altstadt bei Daemmerung | Zielwahl, Aktivitaet, Entdecken |
| `country` | `Land`, `Land 🌾` | heller Naturweg mit Wald, See und Landhaus | Zielwahl, Ruhe, Erholung |
| `roadtrip` | `Roadtrip`, `Roadtrip 🚗` | kurvige Panoramastrasse mit kleinem Reiseauto | Spontanitaet, Umplanung, Rueckfahrt |
| `train` | `Zug`, `Zug 🚆` | gemuetliches Zugabteil mit weiter Aussicht | Rueckfahrt, Energie, praktische Planung |
| `camping` | `Camping`, `Camping ⛺` | Zelt, Lichter und nasse Waldlichtung | Budget, Abenteuer, Improvisation |
| `hotel` | `5-Sterne-Hotel`, `5-Sterne-Hotel 🏨` | helles Boutique-Hotel mit warmem Fensterlicht | Komfort, Budget, Erholung |

Emoji werden nur beim Normalisieren alter Werte akzeptiert. In der neuen Oberflaeche erscheinen keine Emoji-Platzhalter.

## Auswahl der zwei Motive

Beim Start des Packs baut `WeekendEchoSelector` aus der lokalen `BrainAnswerHistoryEntity`-Historie einen Sitzungspool:

1. Nur echte Antworten aus Auswahlspielen werden gelesen.
2. Eine Antwort muss exakt auf eines der acht normalisierten Katalogmotive passen.
3. Das aktuelle Wochenendtrip-Pack selbst wird als Quelle ausgeschlossen.
4. Doppelte Motive werden zusammengefuehrt; die juengste gueltige Quelle bleibt erhalten.
5. Aus allen Treffern werden hoechstens acht unterschiedliche Motive in den Sitzungspool aufgenommen.
6. Pro Kapitel bewertet eine feste lokale Tabelle die Passung zwischen Frage und Motiv.
7. Die zwei bestpassenden noch nicht direkt nacheinander gezeigten Motive bilden das neue Duell.
8. Bei Punktegleichstand entscheidet eine stabile, aus Pack-ID und Frageindex abgeleitete Reihenfolge. Ein Neustart veraendert die aktive Runde daher nicht zufaellig.

Die Auswahl laeuft vollstaendig lokal und deterministisch. Es gibt keine Laufzeit-Bildgenerierung und keinen Netzwerkaufruf.

## Fallback

Sind fuer ein Kapitel weniger als zwei passende Katalogmotive aus echten frueheren Antworten vorhanden, wird fuer dieses Kapitel die bestehende `ScenarioBoard`-Frage mit ihren vorhandenen Antwortoptionen verwendet. Harmony erfindet keine persoenliche Antwort und behauptet keine Erinnerung, die nicht gespeichert ist.

## Privater Ablauf

Jede personalisierte Runde besitzt fuenf Phasen:

1. **Person A waehlt:** Beide bebilderten Welten sind sichtbar. Nur die zwei rotierenden Siegel sind anklickbar.
2. **Verdecken:** Das gewaehlte Siegel schliesst sich. Die Antwort wird nicht eingeblendet.
3. **Uebergabe:** Person B erhaelt dieselben zwei Siegel, ohne die erste Wahl zu sehen.
4. **Person B waehlt:** Auch diese Wahl wird zunaechst verdeckt gespeichert.
5. **Aufdecken:** Beide Siegel brechen gleichzeitig auf. Nur jetzt darf die kurze historische Verbindung erscheinen. Danach wird durch Tippen auf ein geoeffnetes Siegel zum naechsten Kapitel gewechselt.

Der Hintergrund, die Pandas, Bilder und Textflaechen sind niemals klickbar. Zur Bedienung werden ausschliesslich die beiden Siegel als ausreichend grosse Touch-Ziele freigegeben.

## Visuelles Ziel

Verbindliche Referenz ist:

`C:\Users\Ralfg\.codex\generated_images\01a054b1-3b78-7f61-8a77-a0971c9e4490\exec-7f8e0950-3e59-48c5-b33c-f9f08f883813.png`

Die Oberflaeche behaelt:

- eine geteilte, bildschirmfuellende Welt mit einer cyan-violett-pinken Aurora-Trennlinie,
- mittlere Helligkeit mit sichtbaren Details ohne weissen Leuchtwasch,
- Harmony-Violett, Rosa, Reise-Cyan, Teal und warmes Gold,
- zwei von hinten sichtbare Harmony-Pandas auf der linken Reisehaelfte,
- keine Pandas oder Menschen auf der rechten Haelfte,
- eine kurze Frage und genau zwei grosse rotierende Leuchtsiegel,
- keine erklaerenden Karten, keinen unteren Weiter-Button und keine zusaetzlichen Antwortbuttons.

Die acht Bildmotive werden vorab als lokale, optimierte WebP-Assets erstellt. Pro Duell werden zwei Motive zu einer Split-Szene kombiniert. Die Pandas sind eine separate Compose-Ebene und werden nicht dauerhaft in jedes Hintergrundbild eingebrannt.

## Bewegung

- Die Aurora-Trennlinie pulsiert langsam und verschiebt ihre Farbenergie dezent.
- Jedes noch waehbare Siegel dreht seinen aeusseren Lichtring kontinuierlich. Das Siegel selbst bleibt lesbar.
- Beim Antippen stoppt der Ring kurz, das Siegel zieht sich zusammen und wird verdeckt.
- Beim gemeinsamen Reveal drehen beide Ringe schneller, die Wachskanten brechen auf und ein Lichtfaden verbindet die Siegel.
- Ein passender historischer Antwortbegriff darf fuer etwa 1,5 Sekunden zwischen den Siegeln erscheinen. Es gibt keine Saetze wie `Damals gewaehlt` oder `Darum fuehrt eure Reise ...`.
- Danach bleiben die geoeffneten Siegel als einzige Weiter-Touchziele aktiv.
- Bei systemweit reduzierter Bewegung werden Rotation, Partikel und Zoom durch einfache Farb- und Alphawechsel ersetzt.

## Komponenten und Datenfluss

- `WeekendEchoCatalog`: acht Katalogeintraege, akzeptierte gespeicherte Schreibweisen, Bildschluessel und Kapitel-Tags.
- `WeekendEchoSelector`: filtert und bewertet historische Auswahlantworten und liefert null oder genau zwei Kandidaten.
- `WeekendEchoRoundState`: speichert Kandidaten, beide privaten Wahlen und Reveal-Phase stabil ueber Rekreation.
- `WeekendEchoBoard`: rendert ausschliesslich das neue Spiel fuer die feste Pack-ID `h500_076_wochenendtrip_szenario`.
- `RotatingEchoSeal`: besitzt die einzige anklickbare Flaeche und alle Auswahl-, Sperr- und Reveal-Zustaende.
- `WeekendSplitScene`: kombiniert zwei lokale Motive, Aurora-Trennlinie und die linke Panda-Ebene.

Die bestehende Antwortpersistenz bleibt die Quelle der Wahrheit. Das neue Board erhaelt einen vorberechneten, rein lesenden historischen Kontext. Es schreibt erst nach abgeschlossenem Reveal die neue aktuelle Antwort ueber den vorhandenen `onPick`-Pfad. Authentifizierung, Supabase, andere Packs und globale Navigation werden nicht veraendert.

## Fehler- und Datenschutzregeln

- Unbekannte oder nicht mehr katalogisierte Antwortwerte werden ignoriert.
- Antworten ohne eindeutig aufgeloeste Personenzuordnung werden nicht als personenspezifisches Echo bezeichnet.
- Vor dem Reveal werden weder alte noch aktuelle Antworten im sichtbaren UI ausgegeben.
- Ein fehlendes oder defektes Bild verwendet einen kuratierten Harmony-Farbverlauf fuer genau dieses Motiv, nie ein Emoji oder leeres Rechteck.
- Nach Prozessneustart wird eine begonnene private Runde wieder verdeckt hergestellt; keine Antwort blitzt beim Laden auf.

## Verifikation

Automatische Tests muessen mindestens absichern:

- exakte Normalisierung der acht erlaubten Motive,
- Ausschluss von Freitext und unbekannten Antworten,
- Auswahl von genau zwei tatsaechlich vorhandenen historischen Antworten,
- deterministische Reihenfolge je Pack und Frage,
- Fallback bei weniger als zwei gueltigen Kandidaten,
- keine sichtbare Antwort in den Phasen Person A, Uebergabe und Person B,
- Reveal erst nach beiden Wahlen,
- nur beide Siegel besitzen Click-Aktionen,
- feste Aktivierung ausschliesslich ueber die Pack-ID statt Fragetext,
- Wiederherstellung nach Activity-Recreation,
- reduzierte Bewegung ohne Endlosschleifen,
- bestehende Scenario-, Auth- und Signing-Vertraege bleiben unveraendert.

Vor einer Veroeffentlichung werden Unit-Tests, Compose-UI-Tests, `assembleDebug` und ein visueller Vergleich gegen die Referenz ausgefuehrt.
