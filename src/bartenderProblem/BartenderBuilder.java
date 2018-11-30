package bartenderProblem;

import java.util.Random;

import bartenderProblem.EnvironmentElement.Type;
import bartenderProblem.actors.bartender.StupidBartender;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;

public class BartenderBuilder implements ContextBuilder<Object> {
	public Context<Object> build(Context<Object> context) {
		int xdim = 100, ydim = 100;
		
		// grid for environment
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("Simple Grid", context,
				new GridBuilderParameters<Object>(new repast.simphony.space.grid.BouncyBorders(),
						new RandomGridAdder<Object>(), true, xdim, ydim));
		
		// continuous space for agents walking in the environment
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("Continuous Space", context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.BouncyBorders(), xdim, ydim, 1);
		
		context.add(new StupidBartender());
		
		// create random environment for testing purposes
		for (int i = 0; i < xdim; i++) {
			for (int j = 0; j < ydim; j++) {
				Type type = Type.values()[new Random().nextInt(Type.values().length)];
				EnvironmentElement element = new EnvironmentElement(type);
				context.add(element);
				grid.moveTo(element, i, j);
				space.moveTo(element, i + 0.5, j + 0.5, 0);
			}
		}
		
		return context;
	}
}
