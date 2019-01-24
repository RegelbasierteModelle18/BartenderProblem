package bartenderProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bartenderProblem.actors.Guest;
import bartenderProblem.actors.bartender.BartholomeusVonPilsner;
import bartenderProblem.actors.bartender.EnolfVonPilsner;
import bartenderProblem.actors.bartender.OswaldBranntwein;
import bartenderProblem.actors.bartender.RolandBranntwein;
import bartenderProblem.actors.bartender.HubertMetkrug;
import bartenderProblem.actors.bartender.GottfriedMetkrug;
import bartenderProblem.actors.bartender.StupidBartender;
import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameter;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;

public class BartenderBuilder implements ContextBuilder<Object> {
	private static int finishedTasks = 0;
	private static int threadName = new Random().nextInt(1000);
	
	public static double TICKS = 12000;
	
	public Context<Object> build(Context<Object> context) {
		
		if(TICKS > 0) {
			RunEnvironment.getInstance().endAt(TICKS);
		}
		
		SoundHandler.mute();
		Log.mute();
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					SoundHandler.BACKGROUNDMUSIC.play();
					try {
						Thread.sleep(8 * 60 * 1000 + 8 * 1000); // restart song every 8 minutes and 8 seconds
					} catch (InterruptedException e) {
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
		
		/* Parameters from RunEnvironment */
		Parameters p = RunEnvironment.getInstance().getParameters();
		int numBartholomeus = (Integer)((Parameters) p).getValue("numBartholomeus");
		int numEnolf = (Integer)((Parameters) p).getValue("numEnolf");
		int numRoland = (Integer)((Parameters) p).getValue("numRoland");
		int numOswald = (Integer)((Parameters) p).getValue("numOswald");
		int numHubert = (Integer)((Parameters) p).getValue("numHubert");
		int numGottfried = (Integer)((Parameters) p).getValue("numGottfried");
		int tableDensity = (Integer)((Parameters) p).getValue("tableDensity");
		int guestDensity = (Integer)((Parameters) p).getValue("guestDensity");
		int xdim = (Integer)((Parameters) p).getValue("xdim");
		int ydim = (Integer)((Parameters) p).getValue("ydim");
		
		
		//int xdim = 50, ydim = 50;
		
		// grid for environment
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("Simple Grid", context,
				new GridBuilderParameters<Object>(new repast.simphony.space.grid.BouncyBorders(),
						new RandomGridAdder<Object>(), true, xdim, ydim));
		
		// continuous space for agents walking in the environment
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("Continuous Space", context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.BouncyBorders(), xdim, ydim, 1);
		

		TickHandler myTickHandler = new TickHandler();
		
		// add task that get called every tick to enable guests to enter the bar
		myTickHandler.addListener(new TickHandler.TickListener() {
			@Override
			public void onTick(Context<Object> context, Grid<Object> grid, ContinuousSpace<Object> space, long ticks) {
				if (TICKS > 0 && ticks % TICKS == 0) {
					System.out.println("Thread " + threadName + ": " + (++finishedTasks) + " runs finished");
				}
				
				// guest enters with the probability of 10%
				if (new Random().nextInt(100) < guestDensity) {
					
					// search table
					EnvironmentElement table = Util.getRandomEnvironmentElement(Type.TABLE, context);
					if(table == null) {
						return;
					}
					
					GridPoint tableLocation = grid.getLocation(table);
					
					// check for other guests that took seat on the same table
					for (Object o : new ContinuousWithin(context, table, 0.5).query()) {
						if (o instanceof Guest) {
							return;
						}
					}
					
					// spawn guest
					Guest guest = new Guest(0, 100, 300, 1);
					context.add(guest);
					grid.moveTo(guest, tableLocation.getX(), tableLocation.getY());
					space.moveTo(guest, tableLocation.getX() + 0.5, tableLocation.getY() + 0.5, 0);
				}
			}
		});
		
		context.add(myTickHandler);
		
		/* ADD BARTENDERS */
		for (int i = 0; i < numBartholomeus; i++)
			context.add(new BartholomeusVonPilsner(2, 2));
		for (int i = 0; i < numEnolf; i++)
			EnolfVonPilsner.distribute(context, 0, xdim, 0, ydim, 1, 1, 2, 2);
		
		for (int i = 0; i < numRoland; i++)
			context.add(new RolandBranntwein(2, 2, 3));
		for (int i = 0; i < numOswald; i++)
			context.add(new OswaldBranntwein(2, 2, 3));
		
		for (int i = 0; i < numHubert; i++)
			context.add(new HubertMetkrug(2, 2, 3));
		for (int i = 0; i < numGottfried; i++)
			context.add(new GottfriedMetkrug(2, 2, 3, 1));
		
		
		// create random environment for testing purposes
		int tableCount = 0;
		Type type;
		for (int i = 0; i < xdim; i++) {
			for (int j = 0; j < ydim; j++) {
				if(i > xdim - 4) {
					type = Type.BAR;
				} else if((i >= xdim / 2 - 2 && i <= xdim / 2 + 2) && j == 0){
					//entry
					type = Type.ENTRY;
					
				}else if(new Random().nextInt(100) < tableDensity ) {
					// desk
					type = Type.TABLE;
					tableCount++;
				} else {
					//free space
					type = Type.FREE_SPACE;
					//type = Type.values()[new Random().nextInt(Type.values().length)];
					
				}
				EnvironmentElement element = new EnvironmentElement(type);
				context.add(element);
				grid.moveTo(element, i, j);
				space.moveTo(element, i + 0.5, j + 0.5, 0);
			}	
		}
		context.add(new Statistics(tableCount));
		return context;
	}
}
