package bartenderProblem.actors.bartender;

import bartenderProblem.actors.Guest;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
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
				
		// TODO: check for nearby guests
		
		space.moveByVector(this, 1, calculateHeading(),0,0);
		NdPoint point = space.getLocation(this);
		grid.moveTo(this, (int) point.getX(), (int) point.getY());
	}
	
	// returns direction in radiant
	protected abstract double calculateHeading();
	
	// so this in every step for each guest to deliver the order if available
	protected abstract void handleDelivery(Guest guest);

	// do this in every step for each guest to take new orders
	protected abstract void handleOrder(Guest guest);
}
