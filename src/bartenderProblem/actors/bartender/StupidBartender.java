package bartenderProblem.actors.bartender;

import bartenderProblem.actors.Guest;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class StupidBartender extends Bartender {
	protected double calculateHeading() {
		return 0;
	}
	
	protected void handlePersonCommunication(Guest guest) {
		// take order or hand over drink
	}
}
