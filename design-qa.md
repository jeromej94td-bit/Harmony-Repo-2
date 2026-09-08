# Design QA: Der Geheime Plan

## Vergleichsgrundlage

- Ausgewählter visueller Entwurf: `C:\Users\Ralfg\.codex\generated_images\01a054b1-3b78-7f61-8a77-a0971c9e4490\exec-f3e06260-5d19-4522-b09f-8bb6526794ba.png`
- Produktionsasset: `app/src/main/res/drawable-nodpi/secret_plan_magic_book.webp`
- Gerenderter Auswahlscreen: `app/build/secret-plan-preview/01-book-choice.png`
- Gerenderter Seitenumschlag bei 900 ms: `app/build/secret-plan-preview/02-page-turn.png`
- Ziel-Viewport: Pixel 8, 411 dp, Android SDK 35

## Sichtprüfung

- Das magische Buch ist die klare Hauptbühne und füllt den Spielbereich ohne Platzhalterflächen.
- Lavendel, Rosé, warmes Gold, Creme und Türkis bleiben hell, weich und im Harmony-Stil.
- Das vorhandene transparente Panda-Paar ist links oberhalb des Buchs platziert, blickt von der Kamera weg und lässt die rechte Seite frei.
- Frage, Kapitelanzeige und alle Antwortoptionen sind im Ausgangszustand deutlich lesbar.
- Die Antworten wirken wie helle Buch-Tabs und nicht wie eine generische Formularliste.
- Beim Auswählen verschwinden die nicht gewählten Optionen sanft. Die gewählte Seite hebt sich, kippt räumlich und bewegt sich zum rotierenden Herzsiegel.
- Der vollständige Header bleibt während der Animation stabil; der Test-Viewport springt nicht mehr zum fokussierten Element.
- Während der laufenden Animation sind alle Antwort-Tabs deaktiviert.
- Eine selbst geschriebene Idee wird nach dem Speichern als eigene ausgewählte Buchseite animiert; die Preset-Tabs verschwinden dabei vollständig.
- Die Ergebnis- und Versiegelungsansichten verwenden dasselbe Buch-, Gold-, Rosé- und Herzmotiv.

## Behobene Abweichungen

- P1: Der erste Screenshot-Test prüfte irrtümlich auf fehlende Click-Semantics statt auf den korrekten deaktivierten Zustand. Der Vertrag prüft jetzt `Disabled`.
- P1: Ein fokussierter, animierter Antwort-Tab konnte den Screenshot-Viewport nach unten verschieben. Die Buch-Tabs sind nun nicht fokussierbar und bleiben per Touch bedienbar.
- P2: Nicht gewählte Antworten waren beim Seitenumschlag noch zu präsent und überlagerten die bewegte Seite. Sie blenden jetzt deutlich schneller aus.
- P2: Das ausgewählte Harmony-Konzept enthielt das Paarmotiv, im Produktionsscreen fehlte es. Das vorhandene Panda-Paar wurde links und rückwärts blickend integriert.
- P1: Der Pfad „Eigene Idee“ hatte zunächst keine sichtbare ausgewählte Seite. Er besitzt jetzt dieselbe Hebe-, Dreh- und Siegelbewegung wie eine Preset-Antwort und einen Compose-Vertragstest.
- P3: Die Produktionsfassung verwendet die normale Harmony-Schrift statt der dekorativen Serifenschrift des Konzeptbilds, damit App-Konsistenz und Lesbarkeit erhalten bleiben.

## Ergebnis

final result: passed
