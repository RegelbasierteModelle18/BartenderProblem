package bartenderProblem;

import java.awt.Color;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class EnvironmentElement {
	private Type type;
	
	public EnvironmentElement(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	@ScheduledMethod(start= 1.0, interval= 1.0)
	public void step() {
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) context.getProjection("Continuous Space");
		
	}
	
	public enum Type {
		TABLE (Color.GREEN),
		FREE_SPACE (Color.BLACK);
		
		private Color color;
		
		private Type(Color color) {
			this.color = color;
		}
		
		public Color getColor() {
			return this.color;
		}
	}
}
