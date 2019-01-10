package bartenderProblem.actors.bartender;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import bartenderProblem.Util;
import bartenderProblem.actors.Guest;
import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;

public abstract class Bartender {
	
	int deliveryRange, orderRange;
	/**
	 * Explanation guestIdleTicks
	 * Map, which contains every Guest available in the context with his Waiting/Existing-Time in Ticks
	 * Usage: 1. Simple detection of which guests are gone (from your orderList, or whatever)
	 * 		  2. Management for ALL BARTENDERS on guessing thirstiness of every guest
	 * 			=> on delivery set Value of Guest to 0.
	 */
	private Map<Guest, Integer> guestIdleTicks;
	
	public Bartender(int deliveryRange, int orderRange) {
		this.deliveryRange = deliveryRange;
		this.orderRange = orderRange;
		guestIdleTicks = new HashMap<Guest, Integer>();
	}
	
	@ScheduledMethod(start = 1, interval = 1, shuffle=true)
	public void step() {
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) context.getProjection("Continuous Space");
		HashSet<Guest> goneGuests = new HashSet<Guest>();
			
		// update GuestIdleTicks Map
		for(Object guest : context.getObjects(Guest.class)) {
			if(guestIdleTicks.containsKey((Guest) guest)) {
				guestIdleTicks.put((Guest)guest, guestIdleTicks.get((Guest)guest) + 1);
			} else {
				guestIdleTicks.put((Guest) guest, 1);
			}
		}
		// delete gone-guests from guestIdleTicks-Map
		for(Map.Entry<Guest, Integer> guest : guestIdleTicks.entrySet()) {
			if(Util.getSpace(context).getLocation(guest.getKey()) == null) {
				goneGuests.add(guest.getKey());
			}
		}
		for(Guest guest : goneGuests) {
			guestIdleTicks.remove(guest);
		}
		
		// handle deliverys
		for (Object o : new ContinuousWithin(context, this, deliveryRange + 0.1).query()) {
			if (o instanceof Guest) {
				handleDelivery((Guest) o);
			}
		}
		
		// handle orders
		for (Object o : new ContinuousWithin(context, this, orderRange + 0.1).query()) {
			if (o instanceof Guest) {
				handleOrder((Guest) o);
			}
		}
		
		// check for bartender standing on bar
		NdPoint currentLocation = space.getLocation(this);
		for (Object o : grid.getObjectsAt((int) currentLocation.getX(), (int) currentLocation.getY())) {
			if (o instanceof EnvironmentElement) {
				if (((EnvironmentElement) o).getType() == Type.BAR) {
					fillUp();
					break;
				}
			}
		}
		
		// move
		double heading = calculateHeading(context);
		space.moveByVector(this, 1, heading, 0, 0);
		NdPoint point = space.getLocation(this);
		
		// check if bartender walks against table
		boolean movedOnTable = false;
		for (Object o : grid.getObjectsAt((int) point.getX(), (int) point.getY())) {
			if (o instanceof EnvironmentElement) {
				if (((EnvironmentElement) o).getType() == Type.TABLE) {
					movedOnTable = true;
					break;
				}
			}
		}
		
		// if dumbass moved against table, pull back
		if (movedOnTable) {
			space.moveByVector(this, 1, (heading + Math.PI) % (2 * Math.PI), 0, 0);
		} else {
			grid.moveTo(this, (int) point.getX(), (int) point.getY());
		}
	}
	
	public int getGuestIdleTicks(Guest guest) {
		if(guestIdleTicks.containsKey(guest)) {
			return guestIdleTicks.get(guest);
		}
		return 0;
	}
	
	public Map<Guest, Integer> getGuestIdleTicks() {
		return guestIdleTicks;
	}
	
	public int count() {
		return 1;
	}
	
	// returns direction in radiant
	protected abstract double calculateHeading(Context<Object> context);
	
	// so this in every step for each guest to deliver the order if available
	protected abstract void handleDelivery(Guest guest);

	// do this in every step for each guest to take new orders
	protected abstract void handleOrder(Guest guest);
	
	// do this in every step the bartender is at the bar
	protected abstract void fillUp();
}
