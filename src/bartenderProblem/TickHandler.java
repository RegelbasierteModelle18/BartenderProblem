package bartenderProblem;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class TickHandler {
	private long ticks;
	
	private List<TickListener> listeners;
	
	public TickHandler() {
		ticks = 0;
		listeners = new ArrayList<>();
	}
	
	@ScheduledMethod(start= 1.0, interval= 1.0)
	public void step() {
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) context.getProjection("Continuous Space");
		
		ticks++;
		
		for (TickListener listener : listeners) {
			if (listener != null) {
				listener.onTick(context, grid, space, ticks);
			}
		}
	}
	
	public void addListener(TickListener listener) {
		listeners.add(listener);
	}
	
	public interface TickListener {
		/**
		 * gets called every tick of the model
		 * 
		 * @param context context
		 * @param grid grid
		 * @param space space
		 * @param ticks current ticks
		 */
		void onTick(Context<Object> context, Grid<Object> grid, ContinuousSpace<Object> space, long ticks);
	}
}
