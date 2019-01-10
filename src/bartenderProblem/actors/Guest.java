package bartenderProblem.actors;

import java.util.Random;

import bartenderProblem.SoundHandler;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Guest {
	private int thirst;
	private int orderThreshold; // orders a new drink if 
	private int maxThirst;      // goes home if thirst is higher than maxThirst
	private int thirstGrowth; 
	
	public Guest(int thirst, int orderThreshold, int maxThirst, int thirstGrowth) {
		this.thirst = thirst;
		this.orderThreshold = orderThreshold;
		this.maxThirst = maxThirst;
		this.thirstGrowth = thirstGrowth;	
		SoundHandler.OPENDOOR.randomPlay();
		
	}
	
	@ScheduledMethod(start = 1, interval = 1, shuffle=true)
	public void step() {
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) context.getProjection("Continuous Space");
		
		thirst += thirstGrowth;
		
		if (thirst >= maxThirst) {
			SoundHandler.CLOSEDOOR.randomPlay();
			context.remove(this);
		}
	}
	
	/**
	 * orders drinks
	 * 
	 * @return drink or null for no drink
	 */
	public Drink order() {
		if (thirst >= orderThreshold) {
			return Drink.BEER;
		} else {
			if (new Random().nextInt(100) < 10) {
				return Drink.BEER;
			}
		}
		return null;
	}
	
	/**
	 * gets called on drink delivery
	 * 
	 * @param drink drink that got delivered
	 */
	public void takeDelivery(Drink drink) {
		thirst -= drink.getDeltaThirst();
		if (thirst < 0) {
			thirst = 0;
		}
		SoundHandler.OPENBEER.randomPlay();
		System.out.println("Guest: Yummy! Thank you! :)");
	}
	
	
	public int count() {
		return 1;
	}
	
	public enum Drink {
		BEER (100);
		
		// lowers the thirst by this on drink
		private int dThirst;
		
		Drink (int dThirst) {
			this.dThirst = dThirst;
		}
		
		public int getDeltaThirst() {
			return dThirst;
		}
	}
}
