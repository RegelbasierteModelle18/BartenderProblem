package bartenderProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bartenderProblem.actors.Guest;
import bartenderProblem.actors.bartender.StupidBartender;
import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
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
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;

public class BartenderBuilder implements ContextBuilder<Object> {
	public Context<Object> build(Context<Object> context) {
		int xdim = 100, ydim = 50;
		
		// grid for environment
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("Simple Grid", context,
				new GridBuilderParameters<Object>(new repast.simphony.space.grid.BouncyBorders(),
						new RandomGridAdder<Object>(), true, xdim, ydim));
		
		// continuous space for agents walking in the environment
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("Continuous Space", context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.BouncyBorders(), xdim, ydim, 1);
		
		// add task that get called every tick to enable guests to enter the bar
		TickHandler.getInstance().addListener(new TickHandler.TickListener() {
			@Override
			public void onTick(Context<Object> context, Grid<Object> grid, ContinuousSpace<Object> space) {
				// guest enters with the probability of 10%
				if (new Random().nextInt(100) < 10) {
					Guest guest = new Guest(0, 10, 100, 1);
					context.add(guest);
				}
			}
		});
		
		
		context.add(TickHandler.getInstance());
		context.add(new StupidBartender(1, 1));
		
		// create random environment for testing purposes
		/*for (int i = 0; i < xdim; i++) {
			for (int j = 0; j < ydim; j++) {
				Type type = Type.values()[new Random().nextInt(Type.values().length)];
				EnvironmentElement element = new EnvironmentElement(type);
				context.add(element);
				grid.moveTo(element, i, j);
				space.moveTo(element, i + 0.5, j + 0.5, 0);
			}
		}
		*/
		for(int i = xdim - 4; i < xdim; i++){
			for(int j = 0; j < jdim; j++){
				EnvironmentElement elem = new EnvironmentElement(Type.BAR);
				context.add(elem);
				gird.moveTo(elem, i, j);
				space.moveTo(elem, i, + 0.5, j + 0.5, 0);
			}
		}
		
		return context;
	}
}
