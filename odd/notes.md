# Notizen fürs ODD Protokoll - Bartender Problem Model
## Grundbedingungen für das Modell "Bartender Modell"

Das Modell beschreibt eine Situation in einer bekannten Bar, in dem Gäste ein und ausgehen.

- das Environment ist eine (Grid-)Bar, die über Bereiche verfügt
    + Sitzbereich für Gäste
    + Zapfbereich für Barkeeper
    + in der Bar gibt es zunächst nur Bier zu trinken

- ein Barkeeper (auf einem Continuous-Space) kann Gäste mit Getränken versorgen
    + es befindet sich immer mindestens ein Barkeeper in der Bar
    + jeder Barkeeper kann Getränke "herstellen" (es handelt sich hierbei zunächst um EINE Getränkesorte)
    + Herstellung findet in einem Markierten Bereich (hinter der Theke?) statt
    + ein Barkeeper kann eine bestimmte Menge an Getränken tragen
    + der Barkeeper muss zu einem Gast gehen, um eine Bestellung aufzunehmen
    + der Barkeeper muss zu einem Gast gehen, um ihm ein Getränk zu geben
    + der Barkeeper präferiert beim Austeilen diejenigen, die Durstiger sind, um zu vermeiden, dass sie gehen

- ein Barkeeper braucht Auslastung
    + wird ein Barkeeper nicht benötigt fällt sinkt seine Arbeitslust
    + fällt die Benötigung eines Barkeepers unter eine Grenze, geht er nach Hause **(außer er ist der letzte!)**
    + sind Barkeeper überlastet (es gibt zu viele Bestellungen) wird ein neuer gerufen

- ein Gast ist ein Agent in einem Continuous Space, der folgende Eigenschaften hat:
    + jeder Gast kann in einem Vordefinierten Bereich SITZEN
    + dauert eine Bestellung länger als eine bestimmte Dauer, geht er
    + sein Durst nimmt mit der Zeit zu, steigt der Durst über einen bestimmten Wert, geht er nach Hause
    + ist der Gast sitt, bleibt er
- Gäste kommen random, aber durch einen Wahrscheinlichkeitsfaktor in die Bar, gehen aber nach einer Zeit, wenn sie keinen Platz finden

## Ziel des Projekts (muss noch umgeschrieben und ausformuliert werden)
Die perfekte Zusammensetzung aus Barkeepern und Gästen, sodass sowohl Barkeeper als auch Gäste zufrieden sind.

## Parameter
- Startanzahl Barkeeper
- Gasterscheinungswahrscheinlichkeit
- Gast-Durstgrenze
- Barkeeper-Langeweilegrenze
- Gast-Wartegrenze

## Verhalten der Agenten

### Bartender

1.  Strategie 1:
    Die Bestellungsliste der Bartenders wird in einer Queue realisiert
    Barkeeper geht immer einmal alle Gäste ab und ruft bei Gast ein order() auf, nimmt somit die Bestellung auf, oder eben nicht


2.  Strategie 2:

### Gast
Der Gast verhält sich wie folgt:

    1.  spawnen
    2.  Platz suchen
    3.  zyklus solange durst nicht maxthirst:
        - Bartender ablehnen wenn noch nicht threshold
        - bestellen wenn threshold
        - warten

## Denkbare Erweiterungen

Folgende Erweiterungen für das Projekt sind denkbar:
- Environment:
	+ ein Toilettenbereich für Gäste, der von den Gästen genutzt werden kann,
      dabei kann allerdings der Platz weg sein, wenn sie wiederkommen
	+ über jedem Gast wird (wie bei Sims) sein Durstwert angezeigt
    + in der Bar kann man auch Essen bestellen, dafür gibt es einen Koch-Bereich
		
- Barkeeper
	+ es gibt verschiede Arten von Barkeepern
        - Alkohol-Barkeeper können nur alkoholische Getränke zapfen
        - Alkfree-Barkeeper können nur alkoholfreie Getränke zapfen
	+ das Zapfen verschiedener Getränke dauert verschieden Lange
		
- Gast
    + ein Gast präferiert ein bestimmtes Getränk, welches er dann immer bestellt
    + bekommt ein Gast ein Getränk, dass er nicht mag, sinkt der Durststill-Faktor
	+ ein Gast muss nach einer bestimmten Anzahl Getränke auf die Toilette und sich danach einen freien Platz suchen
	+ ein Gast geht nach Konsum einer bestimmten Anzahl Getränke nach Hause
	+ ein Gast hat einen Promillewert, welcher sich bei Einnahme alkoholischer Getränke steigert, ist er vollständig betrunken, geht er nach Hause
	+ ein Gast mit bestimmtem Promillewert hat einen Störradius (der mit Promillewert steigt), der sensible Gäste unabhängig vom Durstzustand verärgert    und verscheuchen kann
	+ ein Gast hat einen Stoffwechselwert, der den Durst schneller oder langsamer ansteigen lässt
	+ es gibt männliche und weibliche Gäste:
        - männliche Gäste trinken lieber Bier, Cola-Korn, Cola, Vodka-Energy, Vodka-Osaft
        - weibliche Gäste trinken lieber Sekt, Wein, Wasser und Orangensaft

    + nach hinzufügen des Kochs
        - dauert eine Bestellung länger als eine bestimmte Dauer, geht er
        - sein Hunger nimmt mit der Zeit zu, steigt der Hunger über einen bestimmten Wert, geht er nach Hause
        - sein Hunger nimmt **deutlich** langsamer zu, als sein Durst 
        - ist der Gast satt, bleibt er

- Koch
    + es befindet sich immer mindestens ein Koch in der Bar
    + ein Koch kann Gäste auf einem Coninuous-Space mit Essen versorgen
    + Herstellung von Essen findet in einem eigenen Bereich statt
    + der Koch muss zu den Gästen gehen um eine Bestellung aufzunehmen
    + der Koch muss zu den Gästen gehen um die Bestellung auszuliefern
    + es gibt Initial ein Gericht, kann aber im gleichen Prinzip wie bei den Barkeepern erweitert werden
    + hat ein Koch nicht genügend auslastung, geht er nach Hause (außer er ist der letzte)

## Erweiterte Parameter

- Getränkesorten auf Getränkekarte
- Bargröße
- Gast Hungergrenze
- Koch langeweilegrenze