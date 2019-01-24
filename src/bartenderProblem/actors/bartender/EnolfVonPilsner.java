package bartenderProblem.actors.bartender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import bartenderProblem.Log;
import bartenderProblem.SoundHandler;
import bartenderProblem.Util;
import bartenderProblem.actors.Guest;
import bartenderProblem.actors.Guest.Drink;
import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.collections.IndexedIterable;

public class EnolfVonPilsner extends Bartender {
	private State state;
	
	private Guest guest;
	private Drink drink;
	
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	
	public EnolfVonPilsner(int deliveryRange, int orderRange, int minX, int maxX, int minY, int maxY) {
		super(deliveryRange, orderRange);
		this.state = State.TAKE_ORDER;
		this.guest = null;
		this.drink = null;
		
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	@Override
	protected double calculateHeading(Context<Object> context) {
		ContinuousSpace<Object> space = Util.getSpace(context);
		
		Collection<Type> avoidElements = new ArrayList<>();
		avoidElements.add(Type.TABLE);	// avoid tables
		
		if(guest == null || space.getLocation(guest) == null) {
			IndexedIterable<Object> guests = context.getObjects(Guest.class);
			
			if(guests == null || guests.size() == 0) {
				return new Random().nextDouble() * 2 * Math.PI;
			}
			
			// check for guests in my area
			List<Guest> myGuests = new ArrayList<>();
			for (Object guest : guests) {
				NdPoint guestLocation = space.getLocation((Guest) guest);
				if (guestLocation.getX() >= minX && guestLocation.getX() <= maxX
						&& guestLocation.getY() >= minY && guestLocation.getY() <= maxY) {
					myGuests.add((Guest) guest);
				}
			}
			
			// if lost guest or guest is not set, find new target and take order
			if (myGuests.isEmpty()) {
				guest = (Guest) guests.get(new Random().nextInt(guests.size()));
			} else {
				guest = myGuests.get(new Random().nextInt(myGuests.size()));
			}
			
			for (int i = 0; i < 500; i++) {
				NdPoint guestLocation = space.getLocation(guest);
				if (guestLocation.getX() >= minX && guestLocation.getX() <= maxX
						&& guestLocation.getY() >= minY && guestLocation.getY() <= maxY) {
					break;
				}
			}
			state = State.TAKE_ORDER;
			drink = null;
		}
		
		NdPoint myPosition = space.getLocation(this);
		NdPoint goalPosition = null;
		
		switch (state) {
			case DELIVER:
			case TAKE_ORDER:
				goalPosition = space.getLocation(guest);
				break;
				
			case FILL_UP:
				EnvironmentElement nextBar = Util.getNextEnvironmentElement(Type.BAR, avoidElements, context, (int) myPosition.getX(), (int) myPosition.getY());
				if(nextBar == null) {
					return 0;
				}
				GridPoint barPosition = Util.getGrid(context).getLocation(nextBar);
				
				goalPosition = new NdPoint(barPosition.getX(), barPosition.getY());
				break;
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

	@Override
	protected void handleDelivery(Guest guest) {
		if (state == State.DELIVER) {
			if (guest == this.guest) {
				guest.takeDelivery(drink);
				
				state = State.TAKE_ORDER;
				this.guest = null;
				drink = null;
				
				Log.println("deliver");
			}
		}
	}
	
	@Override
	protected void handleOrder(Guest guest) {
		if (state == State.TAKE_ORDER) {
			if (guest == this.guest) {
				drink = guest.order();
				if (drink == null) {
					this.guest = null;
					state = State.TAKE_ORDER;
					
					Log.println("no drink for him");
				} else {
					state = State.FILL_UP;
	
					Log.println("take order");
				}
			}
		}
	}
	
	@Override
	protected void fillUp() {
		if (state == State.FILL_UP) {
			state = State.DELIVER;
			
			SoundHandler.FILL_UP.randomPlay();
			Log.println("fill up");
		}
	}
	
	private enum State {
		TAKE_ORDER,
		DELIVER,
		FILL_UP;
	}
	
	// results in 4^partitions areas
	public static void distribute(Context<Object> context, int minX, int maxX, int minY, int maxY, int partitions, int enolfPerArea, int enolfDeliveryRange, int enolfOrderRange) {
		if(partitions == 0) {
			for (int i = 0; i < enolfPerArea; i++) {
				context.add(new EnolfVonPilsner(enolfDeliveryRange, enolfOrderRange, minX, maxX, minY, maxY));
			}
			return;
		}
		
		distribute(context, minX, minX + (maxX - minX) / 2, minY, minY + (maxY - minY) / 2, partitions - 1, enolfPerArea, enolfDeliveryRange, enolfOrderRange);
		distribute(context, minX + (maxX - minX) / 2, maxX, minY, minY + (maxY - minY) / 2, partitions - 1, enolfPerArea, enolfDeliveryRange, enolfOrderRange);
		distribute(context, minX, minX + (maxX - minX) / 2, minY + (maxY - minY) / 2, maxY, partitions - 1, enolfPerArea, enolfDeliveryRange, enolfOrderRange);
		distribute(context, minX + (maxX - minX) / 2, maxX, minY + (maxY - minY) / 2, maxY, partitions - 1, enolfPerArea, enolfDeliveryRange, enolfOrderRange);
	}
}
