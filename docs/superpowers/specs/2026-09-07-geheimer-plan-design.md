# Der geheime Plan – Design

## Ziel

"Der geheime Plan" wird ein eigenständiges Drei-Kapitel-Spiel für zwei Personen. Beide treffen zu jedem Kapitel eine private Wahl. Erst nachdem die dritte doppelte Wahl abgeschlossen ist, öffnet sich eine gemeinsame Plan-Karte. Sie zeigt Überschneidungen als konkrete Ideen für jetzt und unterschiedliche Wünsche als gleichwertige Alternativen.

## Spielerlebnis

Das Spiel stellt genau diese drei Fragen in dieser Reihenfolge:

1. "Wofür nehmt ihr euch spontan einen freien Tag?" – Optionen: "Kleiner Roadtrip", "Zeit nur für uns", "Etwas Neues erleben", "Ein Herzensprojekt starten" und "Eigene Idee …".
2. "Welche Überraschung würde euch wirklich freuen?" – Optionen: "Ein geplanter Abend", "Ein spontaner Ausflug", "Eine persönliche Geste", "Ein gemeinsames Upgrade" und "Eigene Idee …".
3. "Welche gemeinsame Idee sollte endlich passieren?" – Optionen: "Unser nächster Kurztrip", "Ein neues Ritual", "Ein Projekt zu zweit", "Ein mutiger erster Schritt" und "Eigene Idee …".

In jedem Kapitel wählen Person A und Person B nacheinander über zwei leuchtende, nicht beschriftete Siegel. Die jeweils gewählte Antwort bleibt verdeckt; der Bildschirm zeigt nur eine neutrale Bestätigung und die Übergabe an die andere Person. Die Karte "Eigene Idee …" öffnet ein kurzes, lokales Texteingabefeld mit Speichern-Button. Leerer oder nur aus Leerzeichen bestehender Text kann nicht gespeichert werden. Nach Kapitel eins und zwei wird ohne Reveal in das nächste Kapitel gewechselt. Im dritten Kapitel erscheint nach der zweiten Wahl eine aufklappende Plan-Karte.

Die Plan-Karte hat zwei Bereiche:

- **Jetzt planen:** Antworten, die beide gewählt haben, mit einem sanften Stern-Impuls.
- **Auch schön:** Antwortpaare, die unterschiedlich sind, nebeneinander ohne Gewinner oder Punktzahl.

Der Abschluss-Button "Plan speichern" speichert die dritte Doppelwahl und beendet das Quiz auf dem normalen Weg. Bereits gespeicherte Kapitel werden bei einem erneuten Öffnen aus den Antwortdaten wiederhergestellt; die Plan-Karte wird für das dritte Kapitel direkt wieder gezeigt.

## Architektur

Das Spiel erhält einen neuen, festen Pack `geheimer_plan` mit drei Fragen in `HarmonyExpansionPacks`. Seine Sondermechanik wird ausschließlich über diese stabile Pack-ID in `FullscreenQuestionMechanicBoard` geroutet. Andere Geheimwahl-Packs bleiben unverändert auf `SecretChoiceRevealBoard`.

`SecretPlanAnswerCodec` speichert die zwei privaten Antworten eines Kapitels in einem versionierten String. Jede Antwort trägt ihren Typ (`preset` oder `custom`) und den sichtbaren Text; Freitext wird vor dem Speichern getrimmt und auf 80 Zeichen begrenzt. `SecretPlanProgress` filtert ausschließlich Antworten aus `geheimer_plan`, dekodiert sie pro Frageindex und baut für Kapitel drei die drei Plan-Zeilen. Die Logik ist reine Kotlin-Logik und wird ohne Datenbank, Netzwerk oder KI getestet.

`SecretPlanBoard` steuert nur den sichtbaren Ablauf der aktuellen Frage. Es erhält `questionIndex`, `historicalAnswers`, `selectedAnswer`, Profil und `onPick`. Für Kapitel null und eins ruft es `onPick` erst nach beiden verdeckten Wahlen auf. Für Kapitel zwei zeigt es den Reveal, bevor es mit dem kodierten Ergebnis einmal `onPick` aufruft.

## Visuelles System und Interaktion

Der Bildschirm nutzt das bestehende Aurora-Glass-System: dunkles Violett, Plum, warmes Pink und dezente Gold-Akzente. Drei schwebende Plan-Karten stehen für die Kapitel. Nur die zwei Antwortsiegel sowie die expliziten Übergabe-, Weiter- und Speichern-Buttons sind klickbar. Dekoration, Kartenhintergrund und Panda-Silhouetten bleiben nicht klickbar.

Der dritte Durchgang animiert eine Karte von geschlossen zu offen. Die Animation ist lokal, kurz und respektiert den gespeicherten Zustand: Nach einem erneuten Öffnen wird keine private Zwischenphase nachgestellt.

## Daten, Privatsphäre und Fehlerfälle

- Keine KI-Anfrage, kein Netzwerk und keine neue Berechtigung.
- Keine Änderung an Auth, Profilbild-Synchronisierung, Google-Login, E-Mail-Login, Supabase oder Android-Signatur.
- Nur exakt die Pack-ID `geheimer_plan` aktiviert die neue UI.
- Fehlende, ungültige oder doppelte gespeicherte Antworten werden nicht erraten: Die Anzeige setzt an der aktuellen normalen Geheimwahl fort. Eine Abschlusskarte wird nur mit drei gültigen Doppelwahlen angezeigt.
- Freitext bleibt lokal im kodierten Antwortwert und wird erst im finalen Reveal lesbar. Ein Freitext wird nur dann als gemeinsame Idee markiert, wenn beide bereinigten Texte gleich sind; Groß-/Kleinschreibung und mehrfacher Leerraum ändern die Gleichheit nicht.
- Ein bereits vorhandener kodierter Wert wird als abgeschlossen behandelt; `onPick` darf beim erneuten Tippen nicht noch einmal gesendet werden.

## Testbare Akzeptanzkriterien

1. Der neue Pack ist sichtbar, enthält genau drei Kapitel, vier feste Optionen und die Karte "Eigene Idee …" je Kapitel.
2. Der Codec dekodiert ausschließlich gültige `secret-plan-v1:`-Werte inklusive eigener Antworten, verwirft fremde oder unvollständige Werte und begrenzt eigene Antworten auf 80 Zeichen.
3. Jede Kapitelwahl bleibt bis zum letzten Reveal ohne lesbare Antwortlabels; nur die vorgesehenen Controls haben eine Klickaktion.
4. Nach zwei Wahlen in Kapitel eins oder zwei wird genau ein kodierter Wert geliefert und es erscheint keine Plan-Karte.
5. Im dritten Kapitel entstehen aus drei gespeicherten Paaren plus der aktuellen Wahl die Bereiche "Jetzt planen" und "Auch schön"; bei Gleichstand wird keine Antwort als besser dargestellt.
6. Der feste Pack routet zur neuen Mechanik; ein anderer `SECRET_CHOICE`-Pack routet weiterhin zu `SecretChoiceRevealBoard`.
7. Unit- und Compose-Tests für Codec, Fortschritt, Board und Routing sowie `assembleDebug` sind grün.
