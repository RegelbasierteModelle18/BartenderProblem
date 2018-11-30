package bartenderProblem.actors.bartender;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public abstract class Bartender {
	@ScheduledMethod(start = 1, interval = 1, shuffle=true)
	public void step() {
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) context.getProjection("Continuous Space");
				
		space.moveByVector(this, 1, calculateHeading(),0,0);
		NdPoint point = space.getLocation(this);
		grid.moveTo(this, (int) point.getX(), (int) point.getY());
	}
	
	protected abstract double calculateHeading();
}
