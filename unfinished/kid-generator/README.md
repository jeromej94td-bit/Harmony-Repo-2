# KidGenerator — geparkter Zwischenstand

**Status:** PAUSED / aus dem produktiven Release-Pfad vorgesehen

Dieser Ordner konserviert den aktuellen vollständigen KidGenerator-Stand aus `main` nach dem Merge von PR #200. Das Feature war in der App über die Kategorie `mischung` / „Eure Mischung“ erreichbar, ist aber als unfertiger eigener Supabase-/KI-Generator nicht releasebereit.

## Archivquelle
- main zum Archivierungszeitpunkt: `1b652e816c62cbb583abfddd35a848100c6fe04d`
- alter Park-PR: #115 (`Release: KidGenerator in unfinished-ideas parken`) — veraltet und konfliktbehaftet, nicht direkt mergen

## Archivierte Dateien
- `KidGeneratorScreen.kt`
- `KidGeneratorViewModel.kt`
- `KidGeneratorRepository.kt`
- `SupabaseKidGeneratorGateway.kt`
- `KidGeneratorModels.kt`
- `KidGeneratorFullscreenChromeContractTest.kt`

## Wiederaufnahme
1. neuen Branch aus dem dann aktuellen `main` erstellen
2. Dateien aus diesem Archiv selektiv zurückportieren
3. Supabase-/Auth-/Edge-Function-Vertrag gegen den dann aktuellen Backend-Stand prüfen
4. Kategorie/Einstieg neu verdrahten; den alten `mischung`-Sonderpfad nicht blind zurückkopieren
5. Fotoauswahl, Rotation, Generierung, Fehlerzustände, Galerie, Momente und Teilen vollständig testen
6. erst danach einen neuen Release-PR erstellen
