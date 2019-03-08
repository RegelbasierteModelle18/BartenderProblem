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
In diesem Modell sind bis auf die Wirttypen `Oswald`und `Gottfried` keine Kollektive vorhanden.

#### Gottfried Metkrug
- einige bringen, andere nehmen auf

#### Oswald Branntwein
Agenten des Wirttyps `Oswald` werden über eine gemeinsame Bestellliste, auf die jede Instanz zugreifen kann und auf der alle bereits bewirteten Gastinstanzen verzeichnet sind, kollektiviert.

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
Da der Fokus dieses Modells auf Prozessstrategien für die Barkeeper liegt, ist ein Agent vom Typ `Gast` sehr Schlicht gehalten und erfüllt ein minimum an Logik um Operationen wie Bestellung und Konsum zu ermöglichen. Die Besonderheit dieses Agententyps ist die zufällige Erscheinung an einem zufälligen Tisch in der Bar, sowie die vorher durch Parameter festgelegte Durstgrenze, die bei Erreichen für das Entfernen der Agenteninstanz des Gastes sorgt.
Im Ausgangszustand befindet sich ein neuer Gast in einem nicht durstigen Zustand, in dem er mit einer festgelegten Wahrscheinlichkeit von 50% trotzdem ein Getränk bestellt. Steigt der Durstwert über die Bestellgrenze, bestellt er immer. Wird dem Agenten ein Getränk geliefert, wird es unabhängig vom Durstwert konsumiert, worauf der Durstwert sinkt. Um das Modell simpel zu halten, wird davon ausgegangen, dass Getränke in einem Zeitschritt rückstandslos konsumiert werden. 

### Albus von Pilsner

### Bartholomeus von Pilsner

### Enolf von Pilsner

### Gottfried Metkrug

### Hubert Metkrug

### Roland Branntwein
Ein Agent vom Typ "Roland" erfüllt die allgemeine Vorgehensweise eines Wirtes, in dem er zunächst die Bestellungsaufnahme (`ORDER`), dann die Getränkezubereitung (`REFILL`) und zuletzt die Bestellungslieferung (`DELIVER`) vornimmt. 
Die Besonderheit dieses Agententyps ist die Anzahl gleichzeitiger Bestellungen, die er in einem Bestellzyklus verarbeiten kann, welche durch einen vorher festgelegten Parameter `storageLimit` begrenzt wird. Nachdem bei einem neuen Bestellzyklus der erste Gast durch das beste Verhältnis `Distanz / Durst` ermittelt wurde, errechnen sich folgende Gäste mit selbigem Verhältnis in einer Reichweiteneinschränkung von 20 Feldern auf dem Grid. Dabei ist die metrische Distanz als Luftlinie und der Durstwert die Zeitschritte, die vergangen sind, seitdem ein Gast bedient wurde, zu sehen. Um zu vermeiden, dass der Wirt durch diese Berechnung bei einem Gast, der keinen Durst (bei Bestellvorgang `abgelehnt`) hat so lange wartet, bis dieser ein Getränk bestellt oder ein Gast mit besserem Verhältnis berechnet wird, wird in diesem Fall der Gastagent einer Blacklist `unthirstyGuests` hinzugefügt, die bei einem neuen Bestellzyklus geleert wird. Gäste auf dieser Blacklist werden bei Bestellvorgang vom Wirt nicht mehr berücksichtigt. Hat die Bestellliste `orderList` das Limit erreicht, oder sind 70 Zeitschritte vergangen, wechselt der Agent in den Zustand `REFILL`, in dem der kürzeste Weg zum nahesten Thekenelement ermittelt und aufgesucht wird. Vor dem hinzufügen der Getränke werden alle Gäste von der Bestellliste entfernt, die bereits gegangen sind, bevor der Agent in den Zustand `DELIVER` wechselt und die Getränke in der Reihenfolge der Bestellungsaufnahme austeilt. Sind keine Gäste in der Bar vorhanden, wechselt der Agent in den Zustand `IDLE`, in dem er pro Zeitschritt in eine zufällige Richtung geht.

### Oswald Branntwein
Ein Agent vom Typ `Oswald` erfüllt alle Vorgehensweisen, die auch eine Agenteninstanz `Roland` erfüllt und stellt eine Erweitung dieses Typen dar.
Die Besonderheit dieses Agententypen ist die `Kollektivität` mehrerer Agenten im Sinne der Zusammenarbeit.
Eine gemeinsame Gästeliste, auf die vor der Auswahl des nächsten Gastes zugegriffen wird, sorgt dafür, dass niemals ein Gast, der bereits bewirtet wird, erneut ausgewählt wird. Ein Gast, der eine Bestellung neu aufgegeben hat, wird dieser Liste `guestManageSet` hinzugefügt. Intern wird diese Variable als HashSet geführt um doppelte Werte automatisch zu vermeiden. Sind weniger Gäste als Agenten dieses Typs existent, so kann eine mehrfache Bedienung eines Gastes dennoch vorkommen.
Ein Beispiel hierfür ist der Ausgangszustand der Bar, in dem meist zunächst weniger Gäste als Wirte anwesend sind.

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
