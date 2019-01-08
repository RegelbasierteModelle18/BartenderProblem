package bartenderProblem.actors.bartender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bartenderProblem.Util;
import bartenderProblem.actors.Guest;
import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.GridPoint;

/**
 * Roland Branntwein - ein Erbe des StupidBartender
 * 
 * Roland arbeitet strukturiert, aber er kann nicht viel tragen.
 * Er unterteilt die Bar in 4 Quadranten, die er der Reihe nach Abgeht - zwischendurch muss er an der Theke aufladen
 * 
 * @author chris
 *
 */

public class RolandBranntwein extends Bartender{
	int storageLimit;		// how many drinks the bartender can hold at a time
	
	public RolandBranntwein(int deliveryRange, int orderRange, int storageLimit) {
		super(deliveryRange, orderRange);
		this.storageLimit = storageLimit;
	}
	
	protected double calculateHeading(Context<Object> context) {
		return 0;
	}
	
	protected void handleDelivery(Guest guest) {
		
	}

	protected void handleOrder(Guest guest) {
		
	}
	
}
