package bartenderProblem.actors.bartender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import bartenderProblem.Util;
import bartenderProblem.actors.Guest;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.collections.IndexedIterable;

public class StupidBartender extends Bartender {
	private Guest guest;
	
	public StupidBartender(int deliveryRange, int orderRange) {
		super(deliveryRange, orderRange);
		this.guest = null;
	}
	
	protected double calculateHeading(Context<Object> context) {
		ContinuousSpace<Object> space = Util.getSpace(context);
		NdPoint myPosition = space.getLocation(this);
		
		if(guest == null || space.getLocation(guest) == null) {
			IndexedIterable<Object> guests = context.getObjects(Guest.class);
			
			if(guests == null || guests.size() == 0) {
				return new Random().nextDouble() * 2 * Math.PI;
			}
			
			// if lost guest or guest is not set, find new target and take order
			guest = (Guest) guests.get(new Random().nextInt(guests.size()));
		}
		
		Collection<Type> avoidElements = new ArrayList<>();
		avoidElements.add(Type.TABLE);	// avoid tables
		
		// get element that he searched for
		/*EnvironmentElement nextBar = Util.getNextEnvironmentElement(Type.ENTRY, avoidElements, context, (int) myPosition.getX(), (int) myPosition.getY());
		if(nextBar == null) {
			return 0;
		}*/
		
		NdPoint guestPosition = space.getLocation(guest);

		// calculate the shortest way to aimed position
		List<GridPoint> shortestWay = Util.calculatePath((int) myPosition.getX(), (int) myPosition.getY(), (int) guestPosition.getX(), (int) guestPosition.getY(), avoidElements, context);
		if(shortestWay == null || shortestWay.isEmpty()) {
			return 0;
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
		return Math.PI;
	}
	
	protected void handleDelivery(Guest guest) {
		
	}

	protected void handleOrder(Guest guest) {
		
	}
	
	@Override
	protected void fillUp() {
		// TODO Auto-generated method stub
		
	}
}
