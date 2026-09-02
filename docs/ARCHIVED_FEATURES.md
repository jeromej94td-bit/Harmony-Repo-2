# Archivierte Features – Produktionsregeln

Der vollständige Harmony-Stand vor der Produktions-Isolierung ist auf diesem Archiv-Branch gesichert:

`archive/pre-production-isolation-2026-09-02`

Dort bleiben Harmony Brain, alte UI-Experimente und die dazugehörige Legacy-Verkabelung als Referenz erhalten. Der Code ist damit nicht verloren, gehört aber nicht mehr zur freigegebenen Produktlogik.

## Verbindliche Regel

Den Archiv-Branch niemals vollständig zurück nach `main` mergen. Wenn eine archivierte Idee wie Harmony Brain später wieder gebraucht wird, wird von einem aktuellen `main` ein neuer Feature-Branch erstellt. Aus dem Archiv werden dann nur die ausdrücklich gewünschten Konzepte oder Dateien portiert und an die aktuelle App-Architektur angepasst.

## Produktionsschutz

`app/build.gradle.kts` enthält den Task `verifyProductionSourceIsolation`. Er läuft vor jedem Android-Build und bricht den Build ab, wenn bekannte archivierte Features wieder aktiviert werden oder entfernte Katalogeinträge wie `mischung` in produktiven Datenquellen erneut fest verdrahtet werden.

Der private Paar-Chat in `ChatScreen.kt` bleibt Brain-frei. Alte Kompatibilitätsparameter dürfen nur über den ausdrücklich als Übergangsbrücke markierten Adapter laufen; sie werden dort nicht ausgeführt.

## Warum diese Trennung existiert

Git kann Änderungen aus unterschiedlichen Entwicklungsständen ohne Textkonflikt zusammenführen, obwohl die Dateien fachlich nicht mehr zueinander passen. Deshalb reicht ein deaktiviertes Feature-Flag nicht als Schutz. Archiv und Produktion werden getrennt, und der Build selbst prüft die Trennung.
