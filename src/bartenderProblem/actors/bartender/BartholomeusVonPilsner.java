package bartenderProblem.actors.bartender;

import bartenderProblem.actors.Guest;
import repast.simphony.context.Context;

public class BartholomeusVonPilsner extends Bartender {
	
	
	public BartholomeusVonPilsner(int deliveryRange, int orderRange) {
		super(deliveryRange, orderRange);
	}

	@Override
	protected double calculateHeading(Context<Object> context) {
		return 0;
	}

	@Override
	protected void handleDelivery(Guest guest) {
		
	}
	
	@Override
	protected void handleOrder(Guest guest) {
		
	}
}
