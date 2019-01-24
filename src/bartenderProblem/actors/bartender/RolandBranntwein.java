/**
 * @file RolandBranntwein.java
 * @date 2019-01-08
 * @author christomeyer
 */

package bartenderProblem.actors.bartender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import bartenderProblem.Log;
import bartenderProblem.Util;
import bartenderProblem.actors.Guest;
import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;


public class RolandBranntwein extends Bartender{
	int storageLimit;							// how many drinks the bartender can hold at a time
	Context<Object> context;					// Simphony Context
	private Map<Guest, Guest.Drink> orderList;	// orderList containing Guest with ordered Drink 
	Map<Guest, Guest.Drink> storage;			// storage containing holding Drinks
	private Guest nextGuest;					// save next target ( = increasing performance)
	private HashSet<Guest> unthirstyGuests;		// to not handle Guests who are not thursty
	private double operationRange = 20;			// operationRange for next Target
	public Mode mode;							// Barkeeper Mode
	
	public enum Mode {
		IDLE,									// No Target
		DELIVERY,								// Target is nearest Guest in Map "storage"
		ORDER,									// Target is next Guest with best efficiency
		REFILL;									// Target is the bar
	}
	
	/**
	 * Roland Branntwein - ein Erbe des legend�ren Bartender
	 * 
	 * Roland arbeitet mit leichter Intelligenz. Er hat eine begrenzte Trage- und Aufnahmemenge.
	 * Zwischendurch muss er an der Theke also erstmal die Bestellungen abarbeiten, bevor er weitermachen kann.
	 * Zu einem Gast geht er mit dem k�rzesten Weg.
	 * Der erste Gast, den er bedient, errechnet sich aus dem besten Distanz/Durst-Verh�ltnis.
	 * Ab dem zweiten Gast bezieht er bei dieser berechnung nur G�ste in Reichweite von 20 in betracht.
	 * Sind 70 Zeitschritte vergangen, in denen er Bestellungen aufgenommen hat oder ist die orderList voll, geht er zur Theke und bearbeitet die Bestellungen.
	 * 
	 * Manchmal idelt Roland einfach ein bisschen rum, obwohl er eigentlich besser zu tun h�tte!
	 */
	
	/**
	 * c'tor
	 * @param deliveryRange Range of throwing Drinks to Guests
	 * @param orderRange Range of hearing the voice of a Guest
	 * @param storageLimit how many Drink Roland can hold
	 */
	public RolandBranntwein(int deliveryRange, int orderRange, int storageLimit) {
		super(deliveryRange, orderRange);
		this.storageLimit = storageLimit;
		orderList = new HashMap<Guest, Guest.Drink>();
		storage = new HashMap<Guest, Guest.Drink>();
		unthirstyGuests = new HashSet<Guest>();
		mode = Mode.IDLE;
	}
	
	/**
	 * Calculate the next walking direction of Roland
	 */
	protected double calculateHeading(Context<Object> context) {
		ContinuousSpace<Object> space = Util.getSpace(context);
		NdPoint myPosition = space.getLocation(this);
		Collection<Type> avoidElements = new ArrayList<>();
		avoidElements.add(Type.TABLE);	// avoid tables
		updateOperationRange();
		NdPoint goalPosition = null;
		
		
		//check Mode
		if(orderList.size() >= storageLimit) {
			mode = Mode.REFILL;
		}
		else if(!storage.isEmpty()) {
			mode = Mode.DELIVERY;
		}
		else {
			mode = Mode.ORDER;
		}
		
		// highest priority: Is there already a next target precalculated?
		if(nextGuest != null) {
			goalPosition = space.getLocation(nextGuest);
			if(getDistanceTo(nextGuest) <= 1.5) {
				nextGuest = null;
			}
		} else {
			double maxOperationPrivation = 0;
			switch(mode) {
			case IDLE:
				if(getGuestIdleTicks().isEmpty()) {
					return new Random().nextDouble() * 2 * Math.PI;
				}
				
				// clear all Lists to avoid being stuck in some idle-things
				unthirstyGuests.clear();
				storage.clear();
				orderList.clear();	
				
			case ORDER:
				// don't stay in order mode too long
				if(getSteps() > 70 || orderList.size() > storageLimit) {
					mode = Mode.REFILL;
					resetSteps();
				}
	
				for(Map.Entry<Guest, Integer> guest : getGuestIdleTicks().entrySet()) {
					if(Util.getSpace(context).getLocation(guest.getKey()) == null) {
						continue;
					}
					if(orderList.containsKey(guest.getKey()) || unthirstyGuests.contains(guest.getKey())) {
						continue;
					}
					double operationPrivation = calcOperationPrivation(guest.getKey());
					if(operationPrivation > maxOperationPrivation && getDistanceTo(guest.getKey()) < operationRange) {
						maxOperationPrivation = operationPrivation;
						nextGuest = guest.getKey();
					}
				}
				goalPosition = space.getLocation(nextGuest);
				
				break;
				
			case REFILL:
				// is Roland's storage full? => go to bar!
				EnvironmentElement nextBar = Util.getNextEnvironmentElement(Type.BAR, avoidElements, context, (int) myPosition.getX(), (int) myPosition.getY());
				if(nextBar == null) {
					return 0;
				}
				GridPoint barPosition = Util.getGrid(context).getLocation(nextBar);
				goalPosition = new NdPoint(barPosition.getX(), barPosition.getY());
				
				break;
				
			case DELIVERY:
				
				for(Map.Entry<Guest, Guest.Drink> guest : storage.entrySet()) {
					if(!getGuestIdleTicks().containsKey(guest.getKey())) {
						continue;
					}
					double operationPrivation = calcOperationPrivation(guest.getKey());
					if(operationPrivation > maxOperationPrivation && getDistanceTo(guest.getKey()) < operationRange) {
						maxOperationPrivation = operationPrivation;
						nextGuest = guest.getKey();
					}
				}
				goalPosition = space.getLocation(nextGuest);
				
				break;
				
			}
		}
		// if we got here, all modes and priorities gone wrong (this shall NOT happen if any guests are available!)
		if(goalPosition == null) {
			Log.println("Roland is in IDLE. DEBUG INFO: nextGuest="+nextGuest+", goalPosition="
								+goalPosition+", orderListSize="+orderList.size()+", storageSize="+storage.size()+", guestIdleTicksSize="+getGuestIdleTicks().size());
			
			
			if(getGuestIdleTicks().size() > 0) mode = Mode.ORDER;
			
			// random direction in this case
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
			//Log.println("Roland: handleDelivery: storage.get(guest) == null: is True");
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
			//Log.println("Roland: My Order List is full, I'm hopefully on the way to the bar..");
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
		//Log.println("Roland: Do you want a beer?");
		Guest.Drink drink = guest.order();
		if(drink != null) {
			Log.println("Guest: Yes!");
			orderList.put(guest, drink);
		} else {
			Log.println("Guest: I don't want a Drink!");
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
	
	private void updateOperationRange() {
		this.operationRange = getSteps() / 5;
		if(operationRange <= 20){
			operationRange = 20;
		}
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
				//Log.println("Roland: ...this guest is gone.");
			}
			cnt++;
		}
		
		// after filling storage clear orderList and unthirstyGuests
		orderList.clear();
		unthirstyGuests.clear();
		
		if(cnt == 0) {
			//Log.println("Roland: Bar should NOT be my Target! Why i am here?");
		} else {
		}
		
		mode = Mode.DELIVERY;
		resetSteps();
	}
}
