package bartenderProblem.actors.bartender;

import bartenderProblem.actors.Guest;
import bartenderProblem.actors.Guest.Drink;
import repast.simphony.context.Context;
import repast.simphony.util.collections.IndexedIterable;

public class BartholomeusVonPilsner extends Bartender {
	private State state;
	
	private Guest guest;
	private Drink drink;
	
	public BartholomeusVonPilsner(int deliveryRange, int orderRange) {
		super(deliveryRange, orderRange);
		this.state = State.TAKE_ORDER;
	}

	@Override
	protected double calculateHeading(Context<Object> context) {
		IndexedIterable<Object> guests = context.getObjects(Guest.class);
		
		
		
		return 0;
	}

	@Override
	protected void handleDelivery(Guest guest) {
		
	}
	
	@Override
	protected void handleOrder(Guest guest) {
		
	}
	
	@Override
	protected void fillUp() {
		System.out.println("fill");
	}
	
	private enum State {
		TAKE_ORDER,
		DELIVER,
		GET_ORDER;
	}
}
