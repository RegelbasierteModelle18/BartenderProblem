package bartenderProblem.actors.bartender;

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

public abstract class Bartender {
	
	int deliveryRange, orderRange;
	
	public Bartender(int deliveryRange, int orderRange) {
		this.deliveryRange = deliveryRange;
		this.orderRange = orderRange;
	}
	
	@ScheduledMethod(start = 1, interval = 1, shuffle=true)
	public void step() {
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) context.getProjection("Continuous Space");
			
		// handle deliverys
		for (Object o : new ContinuousWithin(context, this, deliveryRange).query()) {
			if (o instanceof Guest) {
				handleDelivery((Guest) o);
			}
		}
		
		// handle orders
		for (Object o : new ContinuousWithin(context, this, orderRange).query()) {
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
