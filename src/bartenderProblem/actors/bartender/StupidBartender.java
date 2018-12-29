package bartenderProblem.actors.bartender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bartenderProblem.Util;
import bartenderProblem.actors.Guest;
import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.GridPoint;

public class StupidBartender extends Bartender {
	
	public StupidBartender(int deliveryRange, int orderRange) {
		super(deliveryRange, orderRange);
	}
	
	protected double calculateHeading(Context<Object> context) {
		NdPoint myPosition =  Util.getSpace(context).getLocation(this);
		
		Collection<Type> avoidElements = new ArrayList<>();
		avoidElements.add(Type.TABLE);	// avoid tables
		
		// get element that he searched for
		EnvironmentElement nextBar = Util.getNextEnvironmentElement(Type.ENTRY, avoidElements, context, (int) myPosition.getX(), (int) myPosition.getY());
		if(nextBar == null) {
			return 0;
		}
		GridPoint barPosition = Util.getGrid(context).getLocation(nextBar);

		// calculate the shortest way to aimed position
		List<GridPoint> shortestWay = Util.calculatePath((int) myPosition.getX(), (int) myPosition.getY(), barPosition.getX(), barPosition.getY(), avoidElements, context);
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
}
