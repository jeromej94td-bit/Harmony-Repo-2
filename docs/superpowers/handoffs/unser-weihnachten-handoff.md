# Unser Weihnachten – Integration

Branch: `feature/unser-weihnachten`

## Einstieg

`HarmonyPacksData` registriert die Kategorie `weihnachten`. `GamesScreen` zeigt dafür eine hervorgehobene, rahmenlose Kategorie-Karte mit animierter Schneeflocke. `MainActivity` öffnet `ChristmasExperienceScreen` als Fullscreen-Overlay und blendet Harmony Top- und Bottom-Navigation aus.

## Spielumfang

- 4 Teile
- 15 Runden pro Teil
- 60 eindeutige Runden insgesamt
- visuelle Rasterauswahl, Spotlight-Karten, Aufdeckkarten und dreistufige K.-o.-Duelle
- Teil 3 enthält Harry Potter und weltweit bekannte Weihnachtsfilm- und Songklassiker
- Teil 2 konzentriert sich stark auf Geschenke, Wünsche und gemeinsame Erlebnisse

## Spielmodus

Standard ist `Gemeinsam`. Eine kleine Moduswahl auf der Teil-Auswahl schaltet optional auf `Reihum`; dabei rotiert die Anzeige automatisch zwischen Person 1 bis 4.

## Animation und Design

- code-native Kerze, Geschenkbox, Filmklappe mit Note und Stern
- asymmetrische Bezier-Lichtbahnen statt gemeinsamer Kreise
- Overshoot-Pop beim Erscheinen
- dauerhaftes Pulsieren und atmender Glow
- wandernder Lichtpunkt und sparsame Partikel
- gestaffelte 3D-Kartendrehung mit Haptik und eigenem Flip-Sound
- winterlicher Harmony-Hintergrund ohne Menschen oder Weihnachtsdorf
- keine rote/pinke Kartenumrandung für die Weihnachtskategorie

## Audio

`ChristmasAudioController` lebt am Root des kompletten Weihnachtsflows. Dadurch wird `christmas_music.ogg` nur beim Betreten gestartet, läuft bei Teil- und Rundenwechseln ohne Neustart weiter und endet erst beim vollständigen Verlassen. Die Wiedergabelautstärke ist fest auf `0.65f` gesetzt. Beim Wechsel in den App-Hintergrund pausiert die Musik und setzt an derselben Stelle fort.

Assets:

- `app/src/main/res/raw/christmas_music.ogg` – 177,4535 Sekunden, 3.251.243 Byte
- `app/src/main/res/raw/christmas_card_flip.ogg` – 0,22 Sekunden, 5.480 Byte

## Prüfungen

`ChristmasGameDefinitionTest` prüft vier Teile, exakt 15 Runden pro Teil, 60 eindeutige IDs, vier Optionen pro Runde und die vereinbarte Harry-Potter-Runde.
