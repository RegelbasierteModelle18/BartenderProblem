package bartenderProblem.actors.bartender;

import bartenderProblem.actors.Guest;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class StupidBartender extends Bartender {
	protected double calculateHeading() {
		return 0;
	}
	
	protected void handleDelivery(Guest guest) {
		
	}

	protected void handleOrder(Guest guest) {
		
	}
}
