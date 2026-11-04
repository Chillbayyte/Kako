# Chillbyyte Minikarte – Quellcodeprojekt (Fabric 26.3)

Dies ist **kein fertiger installierbarer JAR-Download**. Die Java-Quellen müssen noch mit Gradle/Java 25 gebaut und im Spiel getestet werden.

## Gewünschte Funktionen (erste Testversion)

- Eine **zweite, kleine Karte** – unabhängig von **Xaero’s Minimap**.
- Einfache Geländeübersicht aus **nur bereits geladenen Blöcken** (21×21 Farbfelder, keine Vollbildkarte).
- Dein Standort als roter Punkt in der Kartenmitte.
- Rote **X-Markierungen**, wenn du gestorben bist; Speicherung lokal pro Server und Dimension.
- Todespunkte werden weiterhin gespeichert, wenn die Karte ausgeblendet ist.
- **O** öffnet das eigene Einstellungsmenü; **V** blendet die Karte ein/aus.
- Schalter: Karte, Todespunkte, Koordinaten, Position und Zoom. Letzte Todespunkte löschen.
- Kartenecke standardmäßig **unten rechts**, damit Xaero oben links bleiben kann.
- Keine Änderungen an Xaero oder Nitrado; keine Spielerpositionsdaten an Dritte.

## Installation – nachdem die Mod-JAR erfolgreich gebaut wurde

1. Minecraft mit **Fabric 26.3**, **Fabric API** und **Java 25** starten.
2. Die gebaute JAR in **`%appdata%\.minecraft\mods`** auf **deinem PC** kopieren.
3. Xaero's Minimap unverändert im selben Ordner lassen.
4. Minecraft starten und die Tasten **O** und **V** ausprobieren. Bei Kollisionen Tasten unter Optionen → Steuerung ändern.
5. **Nicht** in den Nitrado-Ordner `mods` hochladen! Die Mod ist nur für den Minecraft-Client. Andere Spieler installieren sie freiwillig separat.

## Projekt bauen (für Entwickler)

- JDK 25 installieren und als JAVA_HOME konfigurieren.
- Gradle 9.6 oder kompatible Version installieren.
- Projekt entpacken und im Hauptordner `gradle build` ausführen.
- Ergebnis sollte unter `build/libs/chillbyyte-minikarte-0.1.0.jar` liegen.
- Bei Fehlern die Gradle-Fehlermeldung prüfen und ggf. API-Namen anpassen.

Alternativ die Dateien in ein privates GitHub-Repository hochladen. Die beiliegende GitHub-Actions-Datei baut mit Java 25 und Gradle 9.6; JAR im Workflow-Artefakt herunterladen. Die GitHub-Actions-Konfiguration ist ungetestet.

## Testplan

1. Client mit Xaero + dieser Mod starten (keine Abstürze).
2. Prüfen, ob beide Karten getrennt angezeigt werden.
3. **O** → „Minikarte AUS“ → Xaero bleibt sichtbar.
4. **V** → Karte erscheint wieder.
5. In sicherer Testwelt sterben; „X“ auf der Karte nahe der Todesposition.
6. Spiel neu starten und Todesmarkierung prüfen.
7. Dimension wechseln und prüfen, dass Marker nicht in anderen Dimensionen erscheinen.

## Einschränkungen

- Dies ist eine **erste Prototyp-Version**, keine vollwertige Weltkarte wie Xaero.
- Die Geländeübersicht zeigt **nur geladene Chunks**, teils stark vereinfacht und in Höhlen/Nether ungenau.
- Für den tatsächlichen Produktionsbetrieb ist ein Build- und Spieltest erforderlich; das Paket wurde hier nicht kompiliert, weil Java 25 / Gradle-Abhängigkeiten in der Arbeitsumgebung nicht verfügbar sind.
- Sollte ein Crash auftreten, die Test-JAR wieder aus dem Client-Mods-Ordner nehmen. Deine Welt und der Server werden nicht geändert.
