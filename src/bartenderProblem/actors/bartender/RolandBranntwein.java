package bartenderProblem.actors.bartender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	Context<Object> context;
	Map<Guest, Guest.Drink> orderList;
	Map<Guest, Guest.Drink> storage;
	
	
	public RolandBranntwein(int deliveryRange, int orderRange, int storageLimit) {
		super(deliveryRange, orderRange);
		this.storageLimit = storageLimit;
		orderList = new HashMap<Guest, Guest.Drink>();
		storage = new HashMap<Guest, Guest.Drink>();
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
		 
		if(getDistanceTo(guest) > deliveryRange) {
			return;
		}
		if(!orderList.containsKey(guest) || !storage.containsKey(guest)) {
			return;
		}
		guest.takeDelivery(orderList.remove(guest));
		storage.remove(guest);
	}

	protected void handleOrder(Guest guest) {	
		if(orderList.size() <= storageLimit){
			return;
		}
		if(getDistanceTo(guest) > orderRange) {
			return;
		}
		if(orderList.containsKey(guest)) {
			return;
		}
		Guest.Drink drink = guest.order();
		if(drink != null) {
			orderList.put(guest, drink);
		}
	}
	
	private double getDistanceTo(Guest guest) {
		context = ContextUtils.getContext(this);
		NdPoint myPos = Util.getSpace(context).getLocation(this);
		NdPoint guestPos =  Util.getSpace(context).getLocation(guest);
		return Math.sqrt(Math.pow(myPos.getX() - guestPos.getX(), 2) + Math.pow(myPos.getY() - guestPos.getY(), 2));
	}
	
	@Override
	protected void fillUp() {
		for(Map.Entry<Guest, Guest.Drink> order : orderList.entrySet()) {
			if(!storage.containsKey(order.getKey())) {
				storage.put(order.getKey(), order.getValue());
			}
		}
	}
}
