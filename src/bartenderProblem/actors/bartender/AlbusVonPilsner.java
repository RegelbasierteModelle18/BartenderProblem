package bartenderProblem.actors.bartender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
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
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.collections.IndexedIterable;

public class AlbusVonPilsner extends Bartender {
	private State state;
	
	private LinkedList<Guest> guestsToVisit;
	private LinkedList<Guest> guests;
	private LinkedList<Drink> drinks;
	
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	
	public AlbusVonPilsner(int deliveryRange, int orderRange, int minX, int maxX, int minY, int maxY) {
		super(deliveryRange, orderRange);
		this.state = State.TAKE_ORDER;
		this.guestsToVisit = new LinkedList<>();
		this.guests = new LinkedList<>();
		this.drinks = new LinkedList<>();
		
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	private boolean selectGuestsToVisit(Context<Object> context) {
		ContinuousSpace<Object> space = Util.getSpace(context);
		
		IndexedIterable<Object> guests = context.getObjects(Guest.class);
		if(guests == null || guests.size() == 0) {
			return false;
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

		this.guestsToVisit.clear();
		this.drinks.clear();
		this.guests.clear();
		this.state = State.TAKE_ORDER;
		if (myGuests.isEmpty()) {
			this.guestsToVisit.add((Guest) guests.get(new Random().nextInt(guests.size())));
			
			// check for other guests that took seat on the same table
			for (Object o : new ContinuousWithin(context, this.guestsToVisit.getFirst(), 5).query()) {
				if (o instanceof Guest) {
					this.guestsToVisit.add((Guest) o);
				}
			}
		} else {
			this.guestsToVisit.add(myGuests.get(new Random().nextInt(myGuests.size())));
			
			// check for other guests that took seat on the same table
			for (Object o : new ContinuousWithin(context, this.guestsToVisit.getFirst(), 5).query()) {
				if (o instanceof Guest) {
					if (myGuests.contains(o)) {
						this.guestsToVisit.add((Guest) o);
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
	protected double calculateHeading(Context<Object> context) {
		ContinuousSpace<Object> space = Util.getSpace(context);
		
		Collection<Type> avoidElements = new ArrayList<>();
		avoidElements.add(Type.TABLE);	// avoid tables
		
		NdPoint myPosition = space.getLocation(this);
		NdPoint goalPosition = null;
		
		switch (state) {
			case DELIVER:
				if(guests.isEmpty()) {
					if (!selectGuestsToVisit(context)) {
						return new Random().nextDouble() * 2 * Math.PI;
					}
					return calculateHeading(context);
				}
				
				goalPosition = space.getLocation(guests.getFirst());
				if (goalPosition == null) {
					guests.remove(0);
					return calculateHeading(context);
				}
				break;
				
			case TAKE_ORDER:
				if(guestsToVisit.isEmpty()) {
					if (guests.isEmpty()) {
						if (!selectGuestsToVisit(context)) {
							return new Random().nextDouble() * 2 * Math.PI;
						}
					} else {
						this.state = State.FILL_UP;
						return calculateHeading(context);
					}
				}
				
				goalPosition = space.getLocation(guestsToVisit.getFirst());
				if (goalPosition == null) {
					guestsToVisit.remove(0);
					return calculateHeading(context);
				}
				break;
				
			case FILL_UP:
				if(guests.isEmpty()) {
					if (!selectGuestsToVisit(context)) {
						return new Random().nextDouble() * 2 * Math.PI;
					}
					return calculateHeading(context);
				}
				
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
			if (guests.contains(guest)) {
				int index = guests.indexOf(guest);
				guest.takeDelivery(drinks.get(index));
				guests.remove(index);
				drinks.remove(index);
				
				Log.println("deliver");
			}
		}
	}
	
	@Override
	protected void handleOrder(Guest guest) {
		if (state == State.TAKE_ORDER) {
			if (guestsToVisit.contains(guest)) {
				guestsToVisit.remove(guestsToVisit.indexOf(guest));
				
				Drink drink = guest.order();
				if (drink == null) {
					Log.println("no drink for him");
				} else {
					guests.push(guest);
					drinks.push(drink);
					
					if (guestsToVisit.isEmpty()) {
						state = State.FILL_UP;
					}
	
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
	public static void distribute(Context<Object> context, int minX, int maxX, int minY, int maxY, int albusCount, int albusDeliveryRange, int albusOrderRange) {
		if(albusCount / 4 > 0) {
			int remainder = albusCount % 4;
			distribute(context, minX, minX + (maxX - minX) / 2, minY, minY + (maxY - minY) / 2, albusCount / 4 + (remainder-- > 0 ? 1 : 0), albusDeliveryRange, albusOrderRange);
			distribute(context, minX + (maxX - minX) / 2, maxX, minY, minY + (maxY - minY) / 2, albusCount / 4 + (remainder-- > 0 ? 1 : 0), albusDeliveryRange, albusOrderRange);
			distribute(context, minX, minX + (maxX - minX) / 2, minY + (maxY - minY) / 2, maxY, albusCount / 4 + (remainder-- > 0 ? 1 : 0), albusDeliveryRange, albusOrderRange);
			distribute(context, minX + (maxX - minX) / 2, maxX, minY + (maxY - minY) / 2, maxY, albusCount / 4, albusDeliveryRange, albusOrderRange);
		} else {
			switch (albusCount) {
				case 1:
					context.add(new AlbusVonPilsner(albusDeliveryRange, albusOrderRange, minX, maxX, minY, maxY));
					break;
				case 2:
					context.add(new AlbusVonPilsner(albusDeliveryRange, albusOrderRange, minX, minX + (maxX - minX) / 2, minY, maxY));
					context.add(new AlbusVonPilsner(albusDeliveryRange, albusOrderRange, minX + (maxX - minX) / 2, maxX, minY, maxY));
					break;
				case 3:
					context.add(new AlbusVonPilsner(albusDeliveryRange, albusOrderRange, minX, minX + (maxX - minX) / 2, minY, minY + (maxY - minY) / 2));
					context.add(new AlbusVonPilsner(albusDeliveryRange, albusOrderRange, minX + (maxX - minX) / 2, maxX, minY, minY + (maxY - minY) / 2));
					context.add(new AlbusVonPilsner(albusDeliveryRange, albusOrderRange, minX, maxX, minY + (maxY - minY) / 2, maxY));
					break;
			}
		}
		
		
		
		
	}
}
