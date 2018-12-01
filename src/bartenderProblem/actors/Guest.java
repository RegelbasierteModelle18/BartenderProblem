package bartenderProblem.actors;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Guest {
	@ScheduledMethod(start = 1, interval = 1, shuffle=true)
	public void step() {
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) context.getProjection("Continuous Space");
	}
}
