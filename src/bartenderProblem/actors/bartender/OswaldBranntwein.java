/**
 * @file OswaldBranntwein.java
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

import bartenderProblem.Util;
import bartenderProblem.actors.Guest;
import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;


public class OswaldBranntwein extends Bartender{
	private static HashSet<Guest> guestManageSet = new HashSet<Guest>(); 
	int storageLimit;							// how many drinks the bartender can hold at a time
	Context<Object> context;					// Simphony Context
	private Map<Guest, Guest.Drink> orderList;	// orderList containing Guest with ordered Drink 
	Map<Guest, Guest.Drink> storage;			// storage containing holding Drinks
	private Guest nextGuest;					// save next target ( = increasing performance)
	private HashSet<Guest> unthirstyGuests;		// to not handle Guests who are not thursty
	private double operationRange = 20;			// operationRange for next Target
	public Mode mode;							// Barkeeper Mode
	public Mode lastMode = mode.IDLE;
	private int headCalcCount = 0;					// precalculation counter, to not get stuck if guests go to null
	
	public enum Mode {
		IDLE,									// No Target
		DELIVERY,								// Target is nearest Guest in Map "storage"
		ORDER,									// Target is next Guest with best efficiency
		REFILL;									// Target is the bar
	}
	
	/**
	 * Oswald Branntwein - ein Erbe des legendären Bartender. Bruder des ominösen Roland Branntwein.
	 * Eigentlich ist er Koch, kann aber besser Barkeepern als Roland.
	 * 
	 * Hallo Beschreibung!
	 */
	
	/**
	 * c'tor
	 * @param deliveryRange Range of throwing Drinks to Guests
	 * @param orderRange Range of hearing the voice of a Guest
	 * @param storageLimit how many Drink Oswald can hold
	 */
	public OswaldBranntwein(int deliveryRange, int orderRange, int storageLimit) {
		super(deliveryRange, orderRange);
		this.storageLimit = storageLimit;
		orderList = new HashMap<Guest, Guest.Drink>();
		storage = new HashMap<Guest, Guest.Drink>();
		unthirstyGuests = new HashSet<Guest>();
		mode = Mode.IDLE;
	}
	
	/**
	 * Calculate the next walking direction of Oswald
	 */
	protected double calculateHeading(Context<Object> context) {
		ContinuousSpace<Object> space = Util.getSpace(context);
		NdPoint myPosition = space.getLocation(this);
		Collection<Type> avoidElements = new ArrayList<>();
		HashSet<Guest> tempDeleter = new HashSet<Guest>();
		avoidElements.add(Type.TABLE);	// avoid tables
		updateOperationRange();
		/*if(lastMode != mode) {
			System.out.println("Current Mode: "+mode);
			lastMode = mode;
		}*/
		NdPoint goalPosition = null;
		
		// highest priority: Is there already a next target precalculated?
		if(nextGuest != null) {
			headCalcCount++;
			goalPosition = space.getLocation(nextGuest);
			if(getDistanceTo(nextGuest) <= 1.5 || headCalcCount > 10) {
				headCalcCount = 0;
				nextGuest = null;
			}
		} else {
			double maxOperationPrivation = 0;
			switch(mode) {
			case IDLE:
				if(getGuestIdleTicks().isEmpty()) {
					return new Random().nextDouble() * 2 * Math.PI;
				} else {
					mode = Mode.ORDER;
					for(Map.Entry<Guest, Integer> guest : getGuestIdleTicks().entrySet()) {
						if(!guestManageSet.contains(guest.getKey())) {
							goalPosition = space.getLocation(guest.getKey());
							nextGuest = guest.getKey();
							break;
						}
					}
				}
				break;
				
			case ORDER:
				// don't stay in order mode too long
				if(((getSteps() > 50) && !orderList.isEmpty()) || (orderList.size() >= storageLimit)) {
					//System.out.println("Oswald is going to the Bar.");
					mode = Mode.REFILL;
					resetSteps();
				} else {
	
					maxOperationPrivation = 0;
					for(Map.Entry<Guest, Integer> guest : getGuestIdleTicks().entrySet()) {
						if(Util.getSpace(context).getLocation(guest.getKey()) == null) {
							continue;
						}
						if(orderList.containsKey(guest.getKey()) || unthirstyGuests.contains(guest.getKey()) || guestManageSet.contains(guest.getKey())) {
							continue;
						}
						double operationPrivation = calcOperationPrivation(guest.getKey());
						if(operationPrivation > maxOperationPrivation && getDistanceTo(guest.getKey()) < operationRange) {
							maxOperationPrivation = operationPrivation;
							nextGuest = guest.getKey();
						}
					}
					if(nextGuest == null) {
						mode = Mode.REFILL;
					}
					goalPosition = space.getLocation(nextGuest);
					//System.out.println("ORDER: Position is:"+goalPosition);
				}
				break;
				
			case REFILL:
				// is Oswald's storage full? => go to bar!
				EnvironmentElement nextBar = Util.getNextEnvironmentElement(Type.BAR, avoidElements, context, (int) myPosition.getX(), (int) myPosition.getY());
				if(nextBar == null) {
					return 0;
				}
				GridPoint barPosition = Util.getGrid(context).getLocation(nextBar);
				goalPosition = new NdPoint(barPosition.getX(), barPosition.getY());
				
				break;
				
			case DELIVERY:
				maxOperationPrivation = 0;
				for(Map.Entry<Guest, Guest.Drink> guest : storage.entrySet()) {
					if(!getGuestIdleTicks().containsKey(guest.getKey())) {
						tempDeleter.add(guest.getKey());
						continue;
					}
					maxOperationPrivation = 0;
					double operationPrivation = calcOperationPrivation(guest.getKey());
					if(operationPrivation > maxOperationPrivation) {
						maxOperationPrivation = operationPrivation;
						nextGuest = guest.getKey();
					}
				}
				for(Guest guest : tempDeleter) {
					storage.remove(guest);
				}
				tempDeleter.clear();
				if(storage.isEmpty()) {
					mode = Mode.ORDER;
					resetSteps();
				}
				goalPosition = space.getLocation(nextGuest);
				
				break;
			}
		}
		// if we got here, all modes and priorities gone wrong (this shall NOT happen if any guests are available!)
		if(goalPosition == null) {
			for(Map.Entry<Guest, Integer> guest : getGuestIdleTicks().entrySet()) {
				if(!guestManageSet.contains(guest.getKey())) {
					goalPosition = space.getLocation(guest.getKey());
					nextGuest = guest.getKey();
					break;
				}
			}
			if(goalPosition == null) {
				System.out.println("Oswald is in IDLE. DEBUG INFO: nextGuest="+nextGuest+", goalPosition="
						+goalPosition+", orderListSize="+orderList.size()+", storageSize="+storage.size()
						+", guestIdleTicksSize="+getGuestIdleTicks().size()+", Mode="+mode);
				mode = Mode.IDLE;
				// clear all Lists to avoid being stuck in some idle-things
				unthirstyGuests.clear();
				storage.clear();
				orderList.clear();
				// random direction in this case
				return new Random().nextDouble() * 2 * Math.PI;
			}		
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
		if(!(mode == Mode.DELIVERY)) {
			return;
		}
		
		// if storage not contains the guest, there is no delivery for this guest
		if(!storage.containsKey(guest)) {
			return;
		}
		// if storage is empty, it makes no sense to deliver anything
		if(storage.isEmpty()) {
			resetSteps();
			mode = Mode.ORDER;
			
			return;
		}
			
		guest.takeDelivery(storage.get(guest));
		storage.remove(guest);
		if(storage.isEmpty()) {
			resetSteps();
			mode = Mode.ORDER;
		}
		//System.out.println("Oswald: My Storage: "+storage.size()+"/"+storageLimit+"!");
		guestManageSet.remove(guest);
		
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
		if(!(mode == Mode.ORDER)) {
			return;
		}
		// If orderList is full, i can't handle more drinks
		if(orderList.size() >= storageLimit){
			mode = Mode.REFILL;
			//System.out.println("Oswald: (in handleOrder) Now going into REFILL!");
			return;
		}
		
		// Ignore Guests already taken drinks or bartender
		if(orderList.containsKey(guest) || guestManageSet.contains(guest)) {
			return;
		}
		
		// Ignore Guests once don't want a drink in this wave 
		if(unthirstyGuests.contains(guest)) {
			return;
		}
		
		// Ask Guest for a Beer
		//System.out.println("Oswald: Do you want a beer?");
		Guest.Drink drink = guest.order();
		if(drink != null) {
			orderList.put(guest, drink);
			if(orderList.size() >= storageLimit) {
				mode = Mode.REFILL;
			}
			//System.out.println("Oswald: Ok, orderList: "+orderList.size()+"/"+storageLimit+"!");
			guestManageSet.add(guest);
		} else {
			//System.out.println("Guest: I don't want a Drink!");
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
		operationRange = Math.abs(50 - (double)(getSteps()));
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
		// If storage isn't empty, Oswald got to bar accidently. Clear.
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
				//System.out.println("Oswald: ...this guest is gone.");
			}
			cnt++;
		}
		
		// after filling storage clear orderList and unthirstyGuests
		orderList.clear();
		unthirstyGuests.clear();
		
		if(cnt == 0) {
			//System.out.println("Oswald: Bar should NOT be my Target! Why i am here?");
		} else {
			//System.out.println("Oswald: Going into DELIVERY MODE");
		}
		
		mode = Mode.DELIVERY;
		resetSteps();
	}
}
