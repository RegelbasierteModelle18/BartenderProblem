package bartenderProblem;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class TickHandler {
	private static TickHandler instance;
	
	private List<TickListener> listeners;
	
	private TickHandler() {
		listeners = new ArrayList<>();
	}
	
	@ScheduledMethod(start= 1.0, interval= 1.0)
	public void step() {
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) context.getProjection("Continuous Space");
		
		for (TickListener listener : listeners) {
			if (listener != null) {
				listener.onTick(context, grid, space);
			}
		}
	}
	
	public void addListener(TickListener listener) {
		listeners.add(listener);
	}
	
	public static TickHandler getInstance() {
		if (instance == null) {
			instance = new TickHandler();
		}
		return instance;
	}
	
	public interface TickListener {
		/**
		 * gets called every tick of the model
		 * 
		 * @param context context
		 * @param grid grid
		 * @param space space
		 */
		void onTick(Context<Object> context, Grid<Object> grid, ContinuousSpace<Object> space);
	}
}
