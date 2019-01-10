package bartenderProblem.actors.bartender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
	private HashSet<Guest> unthirstyGuests;
	private int operationRange = 20;
	public Mode mode;
	
	public enum Mode {
		IDLE,
		DELIVERY,
		ORDER,
		REFILL;
	}
	
	public RolandBranntwein(int deliveryRange, int orderRange, int storageLimit) {
		super(deliveryRange, orderRange);
		this.storageLimit = storageLimit;
		orderList = new HashMap<Guest, Guest.Drink>();
		storage = new HashMap<Guest, Guest.Drink>();
		unthirstyGuests = new HashSet<Guest>();
		mode = Mode.IDLE;
	}
	
	
	protected double calculateHeading(Context<Object> context) {
		ContinuousSpace<Object> space = Util.getSpace(context);
		NdPoint myPosition = space.getLocation(this);
		Collection<Type> avoidElements = new ArrayList<>();
		avoidElements.add(Type.TABLE);	// avoid tables
		
		NdPoint goalPosition = null;
		
		// welcher ist der nächste Anlaufpunkt?
		
		//checke Modus:
		if(orderList.size() >= storageLimit) {
			mode = Mode.REFILL;
		}
		if(storage.size() > 0) {
			mode = Mode.DELIVERY;
		}
		if(storage.size() == 0) {
			mode = Mode.ORDER;
		}
		
		// => Höchste Prio: Habe ich schon einen anhaltspunkt?
		if(nextGuest != null) {
			goalPosition = space.getLocation(nextGuest);
			if(getDistanceTo(nextGuest) <= 1.5) {
				nextGuest = null;
			}
		}
		// => Prio 0: bin ich voll? Gehe dann richtung Theke
		else if(mode == Mode.REFILL || orderList.size() >= storageLimit) {
			EnvironmentElement nextBar = Util.getNextEnvironmentElement(Type.BAR, avoidElements, context, (int) myPosition.getX(), (int) myPosition.getY());
			if(nextBar == null) {
				return 0;
			}
			GridPoint barPosition = Util.getGrid(context).getLocation(nextBar);
			goalPosition = new NdPoint(barPosition.getX(), barPosition.getY());
		}
		
		else if(mode == Mode.DELIVERY || storage.size() > 0) {
			
			// => Prio 1: der nächste Gast auf dem Storage (Gast mit größtem Distanz/Durst verhältnis)
			double maxOperationPrivation = 0;
			for(Map.Entry<Guest, Guest.Drink> guest : storage.entrySet()) {
				if(!getGuestIdleTicks().containsKey(guest.getKey())) {
					continue;
				}
				double operationPrivation = calcOperationPrivation(guest.getKey());
				if(operationPrivation > maxOperationPrivation && getDistanceTo(guest.getKey()) < operationRange) {
					nextGuest = guest.getKey();
				}
			}
			goalPosition = space.getLocation(nextGuest);
		} 	
		else if(mode == Mode.ORDER) {
			
			// => Prio 2: der nächste Gast mit geringstens Distanz/Durst verhältnis, der noch nicht auf der orderList und noch nicht auf der unthirstyGuests steht
			double maxOperationPrivation = 0;
			for(Map.Entry<Guest, Integer> guest : getGuestIdleTicks().entrySet()) {
				if(Util.getSpace(context).getLocation(guest.getKey()) == null) {
					continue;
				}
				if(orderList.containsKey(guest.getKey()) || unthirstyGuests.contains(guest.getKey())) {
					continue;
				}
				double operationPrivation = calcOperationPrivation(guest.getKey());
				if(operationPrivation > maxOperationPrivation && getDistanceTo(guest.getKey()) < operationRange) {
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
				if(operationPrivation > maxOperationPrivation && getDistanceTo(guest.getKey()) < operationRange) {
					nextGuest = guest.getKey();
				}
			}
			goalPosition = space.getLocation(nextGuest);
		}
		
		if(goalPosition == null || mode == Mode.IDLE) {
			System.out.println("Roland: I have no target. DEBUG INFO: nextGuest="+nextGuest+", goalPosition="
								+goalPosition+", orderListSize="+orderList.size()+", storageSize="+storage.size()+", guestIdleTicksSize="+getGuestIdleTicks().size());
			unthirstyGuests.clear();
			storage.clear();
			orderList.clear();
			if(getGuestIdleTicks().size() > 0) mode = Mode.ORDER;
			return new Random().nextDouble() * 2 * Math.PI;
			
		}
		
		// Calculate List shortestWay
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
	
	/**
	 * handleDelivery is executed, if guest is in range
	 * removes delivery from storage if delivered
	 * 
	 */
	protected void handleDelivery(Guest guest) {
		// this is critical...
		if(storage.get(guest) == null) {
			//System.out.println("Roland: handleDelivery: storage.get(guest) == null: is True");
		}
		// if storage is empty, it makes no sense to deliver anything
		if(storage.isEmpty()) {
			return;
		}
		// if storage not contains the guest, there is no delivery for this guest
		if(!storage.containsKey(guest)) {
			return;
		}
		
		guest.takeDelivery(storage.get(guest));
		storage.remove(guest);
		
		// update thirstiness in global guestIdleTicks
		getGuestIdleTicks().put(guest, 0);
		
		// if target reached, set nextGuest null
		if(guest == nextGuest) {
			nextGuest = null;
		}
	}

	/**
	 * handles order of next Guest
	 */
	protected void handleOrder(Guest guest) {	
		
		// If orderList is full, i can't handle more drinks
		if(orderList.size() >= storageLimit){
			//System.out.println("Roland: My Order List is full, I'm hopefully on the way to the bar..");
			return;
		}
		
		// Ignore Guests already taken drinks
		if(orderList.containsKey(guest)) {
			return;
		}
		
		// Ignore Guests once don't want a drink in this wave 
		if(unthirstyGuests.contains(guest)) {
			return;
		}
		
		// Ask Guest for a Beer
		System.out.println("Roland: Do you want a beer?");
		Guest.Drink drink = guest.order();
		if(drink != null) {
			System.out.println("Guest: Yes!");
			orderList.put(guest, drink);
		} else {
			System.out.println("Guest: No.");
			unthirstyGuests.add(guest);
		}
		
		// if target reached, set nextGuest null
		if(guest == nextGuest) {
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
		// If storage isn't empty, Roland got to bar accidently. Clear.
		if(!storage.isEmpty()) {
			storage.clear();
		}
		
		context = ContextUtils.getContext(this);
		int cnt = 0;
		
		// fill storage, ignore gone guests
		for(Map.Entry<Guest, Guest.Drink> order : orderList.entrySet()) {
			
			if(getGuestIdleTicks().containsKey(order.getKey())) {
				storage.put(order.getKey(), order.getValue());
			} else {
				System.out.println("Roland: ...this guest is gone.");
			}
			cnt++;
		}
		
		// after filling storage clear orderList and unthirstyGuests
		orderList.clear();
		unthirstyGuests.clear();
		
		if(cnt == 0) {
			System.out.println("Roland: Bar should NOT be my Target! Why i am here?");
		} else {
			System.out.println("Roland: I'm now in DeliveryMode!");
		}
		
		mode = Mode.DELIVERY;
	}
}
