# ODD Protokoll - Bartender Problem Model
Malte kl. Piening, Mark Hiltenkamp, Christoph Meyer

Angewandte Systemwissenschaften: Regelbasierte Modelle (WS18/19)

Die folgende Modellbeschreibung folgt dem ODD-Schema für ein individuelles, agentenbasiertes Modell. (Grim et al. 2006, 2010)


## 1. Modellzewck
- Optimale Policy der Wirte für Maximierung der Gästeanzahl

* * *
## 2. Entitäten, Zustandsvariablen und Skalen
### Agenten
alle auflisten
- ein Gasttyp
- 7 verschiedene Strategien für bedienung der Gäste durch Wirte - somit auch 7 verschiedene Wirttypen
- alle Wirte haben der Übersicht halber Namen erhalten
- alle Namen auflisten

### Räumliche Einheiten
- Grid (Umgebung)
- Continuous Space (Agenten)

### Umgebung
- Auf Grid
- stellt Lokal mit Tischen und Theke dar
- jede Zelle entweder tisch, Boden oder Theke

### Kollektive
- keiner außer Oswald und Gottfried haben kollektive bla bla bla...

#### Gottfried Metkrug
- einige bringen, andere nehmen auf

#### Oswald Branntwein
- Gemeinsame Bestelliste über alle Wirte


### Räumliche und zeitliche Skalierung
- asynchrone updates
- keine Einheit für größe
- sticky borders (wie Wände im Lokal)
- 1 Sekunde pro Tick

* * *
## 3. Prozessübersicht und Terminierung
- allgemeines Agentendiagramm einfügen
- Vorgehen von jedem Agenten kurz beschreiben

### Gast
- dumm
- trinkt gerne viel

### Albus von Pilsner

### Bartholomeus von Pilsner

### Enolf von Pilsner

### Gottfried Metkrug

### Hubert Metkrug

### Oswald Branntwein

### Roland Branntwein

* * *
## 4. Entwufsmuster
***Questions:*** There are eleven design concepts. Most of these were discussed extensively by Railsback (2001) and Grimm and Railsback (2005; Chapter. 5), and are summarized here via the following questions:

### Grundprinzipien
- an sich kein vergleichbares Modell in der Vorlesung kennengelernt

### Emergenz 
- stark emergent
- aus Strategie lässt sich nicht Güte des Systems schließen

### Adaptivität
- von Agent abhängig

#### Gast
- initial kein durst
- wenn dürsteschwelle erreicht, dann durst
- bei kein durst, bei 50% der Nachfragen durch Wirt bestellen
- bei durst immer bestellen

#### Albus von Pilsner

#### Bartholomeus von Pilsner

#### Enolf von Pilsner

#### Gottfried Metkrug

#### Hubert Metkrug

#### Oswald Branntwein

#### Roland Branntwein


### Ziele 
- Gast hat Ziel, nicht zu durstig zu werden, aber auch nicht zu sitt zu werden
- alle Wirte wollen Gäste beliefern, damit Gäste bleiben

### Lernen 
- die lernen nicht, sind nämlich n bissle dumm

### Vorhersage
- Gäste planen nicht für die Zukunft und treffen keine Vorhersagen
- Wirte entscheiden sich vor Belieferung eines Gastes für einen Gast, bei dem die die bestellung aufnehmen und ihn anschließend beliefern. (sagen "der Gast wird beliefert werden")

### Wahrnehmung 

#### Gast
- gast nimmt Umgebung nicht wahr und agiert nur, wenn er zu viel durst hat, oder ein Wirt nach Getränk fragt

#### Wirt
- sehen die ganze umgebung / bar
- sehen alle Gäste
- interagieren nur mit Gästen in unmittelbarer Umgebung


### Interaktion 
- Wirte fragen Gäste in unmittelbarer Umgebung nach Wünschen / Bestellung
- Gäste Antworten vom Zustand abhängig mit Wunschgetränk

- Wirte, die Getränke mit sich tragen, beingen Gästen in unmittelbarer Umgebung diese Getränke
- Gast trinkt Getränk auf Ex

### Stochastik 
- Erstellung der Tische in Umgebung (nicht theke) ist zufällig (hat aber feste Entstehungswahrscheinlichkeit eines Tisches)
- Ankommen von Gästen in der Bar ist zufällig
- einige Wirte wählen zu bedienenden Gast zufällig
- Bestellungsaufnahmen von undurstigen Gästen ist zufällig

### Beobachtung 
- batch runs in Excel Tabellen
- Auswertung als Excel Tabelle
- Dabei besonders mittlere Gastanzahl, Standardabweichung der Gäste in Abhängigkeit von Wirtanzahl nach bestimmter Zeit (nach Erreichen des Maximums) betrachtet

* * *
## 5. Initialisierung
- Erstellungsdichte der Tische vor beginn der Simulation
- Anzahl der Wirte je nach Wirttyp
- Erscheinungswahrscheinlichkeit für Gäste
- Umgebungsgröße

* * *
## 6. Eingabedaten
- keine Eingabedaten externer Quellen

* * *
## 7. Submodelle
- verwendet keine submodelle

## 8. Ergebnisse
- Batch runs einfügen