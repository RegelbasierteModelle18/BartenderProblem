package bartenderProblem;

import java.util.LinkedList;
import java.util.Random;

import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.space.grid.Grid;

public class Util {
	public static EnvironmentElement getRandomElement(Type type, Context<Object> context) {
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		
		LinkedList<EnvironmentElement> elements = new LinkedList<>();
		
		for (int i = 0; i < grid.getDimensions().getWidth(); i++) {
			for (int j = 0; j < grid.getDimensions().getHeight(); j++) {
				for (Object o : grid.getObjectsAt(i, j)) {
					if (o instanceof EnvironmentElement) {
						if (((EnvironmentElement) o).getType() == type) {
							elements.add((EnvironmentElement) o);
						}
					}
				}
			}
		}
		
		if (elements.size() == 0) {
			return null;
		}
		
		return elements.get(new Random().nextInt(elements.size()));
	}
}
