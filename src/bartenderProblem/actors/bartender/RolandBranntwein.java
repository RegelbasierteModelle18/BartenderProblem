package bartenderProblem.actors.bartender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import bartenderProblem.Util;
import bartenderProblem.actors.Guest;
import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;

/**
 * Roland Branntwein - ein Erbe des StupidBartender
 * 
 * Roland arbeitet strukturiert, aber er kann nicht viel tragen.
 * Er unterteilt die Bar in 4 Quadranten, die er der Reihe nach Abgeht - zwischendurch muss er an der Theke aufladen
 * 
 * @author chris
 *
 */

public class RolandBranntwein extends Bartender{
	int storageLimit;		// how many drinks the bartender can hold at a time
	double lastHeading = 0;
	Context<Object> context;
	private Map<Guest, Guest.Drink> orderList;
	Map<Guest, Guest.Drink> storage;
	private Guest nextGuest;
	
	public RolandBranntwein(int deliveryRange, int orderRange, int storageLimit) {
		super(deliveryRange, orderRange);
		this.storageLimit = storageLimit;
		orderList = new HashMap<Guest, Guest.Drink>();
		storage = new HashMap<Guest, Guest.Drink>();
	}
	
	
	protected double calculateHeading(Context<Object> context) {
		ContinuousSpace<Object> space = Util.getSpace(context);
		NdPoint myPosition = space.getLocation(this);
		Collection<Type> avoidElements = new ArrayList<>();
		avoidElements.add(Type.TABLE);	// avoid tables
		
		NdPoint goalPosition = null;
		
		// welcher ist der nächste Anlaufpunkt?
		// => Höchste Prio: Habe ich schon einen anhaltspunkt?
		if(nextGuest != null) {
			goalPosition = space.getLocation(nextGuest);
		}
		// => Prio 0: bin ich voll? Gehe dann richtung Theke
		else if(orderList.size() >= storageLimit) {
			EnvironmentElement nextBar = Util.getNextEnvironmentElement(Type.BAR, avoidElements, context, (int) myPosition.getX(), (int) myPosition.getY());
			if(nextBar == null) {
				return 0;
			}
			GridPoint barPosition = Util.getGrid(context).getLocation(nextBar);
			goalPosition = new NdPoint(barPosition.getX(), barPosition.getY());
		}
		
		else if(storage.size() > 0) {
			// => Prio 1: der nächste Gast auf dem Storage (Gast mit größtem Distanz/Durst verhältnis)
			double maxOperationPrivation = 0;
			for(Map.Entry<Guest, Guest.Drink> guest : storage.entrySet()) {
				if(Util.getSpace(context).getLocation(guest.getKey()) == null) {
					continue;
				}
				double operationPrivation = calcOperationPrivation(guest.getKey());
				if(operationPrivation > maxOperationPrivation) {
					nextGuest = guest.getKey();
				}
			}
			goalPosition = space.getLocation(nextGuest);
		} 	
		else if(orderList.size() >= 0) {
			
			// => Prio 2: der nächste Gast mit geringstens Distanz/Durst verhältnis, der noch nicht auf der orderList steht
			double maxOperationPrivation = 0;
			for(Map.Entry<Guest, Integer> guest : getGuestIdleTicks().entrySet()) {
				if(Util.getSpace(context).getLocation(guest.getKey()) == null) {
					continue;
				}
				if(orderList.containsKey(guest.getKey())) {
					continue;
				}
				double operationPrivation = calcOperationPrivation(guest.getKey());
				if(operationPrivation > maxOperationPrivation) {
					nextGuest = guest.getKey();
				}
			}
			goalPosition = space.getLocation(nextGuest);
		} 
		else {
			// => Prio 3: der nächste Gast mit geringstem Distanz/Durst verhältnis
			double maxOperationPrivation = 0;
			for(Map.Entry<Guest, Integer> guest : getGuestIdleTicks().entrySet()) {
				double operationPrivation = calcOperationPrivation(guest.getKey());
				if(operationPrivation > maxOperationPrivation) {
					nextGuest = guest.getKey();
				}
			}
			goalPosition = space.getLocation(nextGuest);
		}
		
		if(goalPosition == null) {
			return new Random().nextDouble() * 2 * Math.PI;
		}
		
		List<GridPoint> shortestWay = Util.calculatePath((int) myPosition.getX(), (int) myPosition.getY(), (int) goalPosition.getX(), (int) goalPosition.getY(), avoidElements, context);
		if(shortestWay == null || shortestWay.isEmpty()) {
			return new Random().nextDouble() * 2 * Math.PI;
		}
		
		// follow shortest way
		if ((int) myPosition.getX() > shortestWay.get(0).getX()) {
			return Math.PI;
		} else if ((int) myPosition.getX() < shortestWay.get(0).getX()) {
			return 0;
		} else if ((int) myPosition.getY() > shortestWay.get(0).getY()) {
			return 3 * Math.PI / 2;
		} else if ((int) myPosition.getY() < shortestWay.get(0).getY()) {
			return Math.PI / 2;
		}
		return new Random().nextDouble() * 2 * Math.PI;
		
		
	}
	
	protected void handleDelivery(Guest guest) {
		if(storage.isEmpty()) {
			return;
		}
		if(!orderList.containsKey(guest) || !storage.containsKey(guest)) {
			return;
		}
		guest.takeDelivery(orderList.remove(guest));
		storage.remove(guest);
		System.out.println("He took his drink!");
		nextGuest = null;
	}

	protected void handleOrder(Guest guest) {	
		if(orderList.size() >= storageLimit){
			return;
		}
		if(orderList.containsKey(guest)) {
			return;
		}
		Guest.Drink drink = guest.order();
		if(drink != null) {
			System.out.println("Have a new Order, wohow!!");
			
			orderList.put(guest, drink);
			nextGuest = null;
		} else {
			System.out.println("He don't want to drink. :(");
			nextGuest = null;
		}
	}
	
	private double getDistanceTo(Guest guest) {
		context = ContextUtils.getContext(this);
		NdPoint myPos = Util.getSpace(context).getLocation(this);
		NdPoint guestPos =  Util.getSpace(context).getLocation(guest);
		if(guestPos == null) {
			return 0;
		}
		return Math.sqrt(Math.pow(myPos.getX() - guestPos.getX(), 2) + Math.pow(myPos.getY() - guestPos.getY(), 2));
	}
	
	// Value of efficiency to operate this guest
	private double calcOperationPrivation(Guest guest) {
		if(guest == null) {
			return 0;
		}
		return (double) (getGuestIdleTicks(guest) / getDistanceTo(guest));
	}
	
	@Override
	protected void fillUp() {
		context = ContextUtils.getContext(this);
		int cnt = 0;
		
		for(Map.Entry<Guest, Guest.Drink> order : orderList.entrySet()) {
			storage.put(order.getKey(), order.getValue());
			if(Util.getSpace(context).getLocation(order.getKey()) == null) {
				System.out.println("Grr, this Guest is gone...");
				storage.remove(order.getKey());
			}
			cnt++;
		}
			orderList.clear();
		if(cnt == 0) {
			System.out.println("No orders in my list...");
		} else {
			System.out.println("Filled Up my Inventory, orderList Now Shall be Empty. Is it?!! =>" + orderList.isEmpty());
		}
	}
}
