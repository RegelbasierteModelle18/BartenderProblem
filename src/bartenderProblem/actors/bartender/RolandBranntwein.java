package bartenderProblem.actors.bartender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bartenderProblem.Util;
import bartenderProblem.actors.Guest;
import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

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
	double lastHeading = 0;
	Context<Object> context = ContextUtils.getContext(this);
	NdPoint myPosition =  Util.getSpace(context).getLocation(this);
	
	public RolandBranntwein(int deliveryRange, int orderRange, int storageLimit) {
		super(deliveryRange, orderRange);
		this.storageLimit = storageLimit;
	}
	
	protected double calculateHeading(Context<Object> context) {
		if(lastHeading < 2 * Math.PI) {
			lastHeading += 0.2;
		} else {
			lastHeading = 0;
		}
		return lastHeading;
	}
	
	protected void handleDelivery(Guest guest) {
		
	}

	protected void handleOrder(Guest guest) {	
		
	}
	
	@Override
	protected void fillUp() {
		// TODO Auto-generated method stub
		
	}
}
