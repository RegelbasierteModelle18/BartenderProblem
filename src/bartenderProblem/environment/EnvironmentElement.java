package bartenderProblem.environment;

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
		// Table is kind of brown
		TABLE (new Color(205,133,63)),
		// free Space is kind of green
		FREE_SPACE (new Color(34,139,34)),
		// Bar is similar brown as TABLE
		BAR(new Color(139,69,19)),
		// entry is kind of yellow
		ENTRY(new Color(250,250,210)),
		WALL(new Color(47, 79, 79));
		
		private Color color;
		
		private Type(Color color) {
			this.color = color;
		}
		
		public Color getColor() {
			return this.color;
		}
	}
}
