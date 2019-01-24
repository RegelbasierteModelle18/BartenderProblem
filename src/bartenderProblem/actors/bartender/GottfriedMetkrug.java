package bartenderProblem.actors.bartender;

import bartenderProblem.Log;
import bartenderProblem.SoundHandler;
import bartenderProblem.Util;
import bartenderProblem.actors.Guest;
import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.GridPoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class GottfriedMetkrug extends Bartender{
	private static HashMap<Guest, Guest.Drink> order_status = new HashMap<>();
	private static HashMap<Guest, Guest.Drink> current_delivery = new HashMap<>();
	private static ArrayList<Guest> order_intent = new ArrayList<>();
	private static int num_orderers;
	private static int num_deliverers;
	private HashMap<Guest, Guest.Drink> to_deliver;
	private int capacity;
	private boolean is_deliverer;
	
	public GottfriedMetkrug(int deliveryRange, int orderRange, int capacity, double orderer_deliverer_ratio) {
		super(deliveryRange, orderRange);
		this.capacity = capacity;
		set_type(orderer_deliverer_ratio);
		if(is_deliverer)
			to_deliver = new HashMap<>();
	}
	
	public void set_type(double ratio) {
		if(num_orderers == 0 && num_deliverers == 0) {
			if(ratio > 1)
				num_orderers++;
			else
				num_deliverers++;
		}
		else if(num_deliverers == 0) {
			num_deliverers++;
		}
		else if((double)num_orderers / num_deliverers < ratio) {
			num_orderers++;
			is_deliverer = false;
		}
		else {
			num_deliverers++;
			is_deliverer = true;
		}
	}

	@Override
	protected double calculateHeading(Context<Object> context) {
		ContinuousSpace<Object> space = Util.getSpace(context);
		Collection<Type> avoidElements = new ArrayList<>();
		avoidElements.add(Type.TABLE);
		NdPoint pos = space.getLocation(this);
		NdPoint goal = null;
		if(is_deliverer)
			update_delivery(space);
		// Delivery
		if(is_deliverer && !to_deliver.isEmpty()) {
			Log.println("Heading to deliver order");
			Guest target_guest = (Guest)to_deliver.keySet().toArray()[0];
			goal = space.getLocation(target_guest);
		}
		// Fill up
		else if(is_deliverer) {
			Log.println("Heading to Bar");
			EnvironmentElement nextBar = Util.getNextEnvironmentElement(Type.BAR, avoidElements, context, (int) pos.getX(), (int) pos.getY());
			if(nextBar == null) {
				return 0;
			}
			GridPoint barPosition = Util.getGrid(context).getLocation(nextBar);
			goal = new NdPoint(barPosition.getX(), barPosition.getY());
		}
		// Order
		else if(!is_deliverer){
			Log.println("Heading to take order");
			HashSet<Guest> s = new HashSet<>();
			for(Object g : context.getObjects(Guest.class))
				s.add((Guest)g);
			// Select a guest who has not placed an order
			s.removeAll(order_status.keySet());
			s.removeAll(current_delivery.keySet());
			s.removeAll(order_intent);
			if(!s.isEmpty()) {
				Guest g = (Guest)s.toArray()[new Random().nextInt(s.size())];
				goal = space.getLocation(g);
				order_intent.add(g);		
			}
		}
		if(goal == null) {
			Log.println("Heading nowhere");
			return Math.PI * 2 * new Random().nextDouble();
		}
		List<GridPoint> shortestWay = Util.calculatePath((int) pos.getX(), (int) pos.getY(), (int) goal.getX(), (int) goal.getY(), avoidElements, context);
		if(shortestWay == null || shortestWay.isEmpty()) {
			return new Random().nextDouble() * 2 * Math.PI;
		}
		
		// follow shortest way
		if ((int) pos.getX() > shortestWay.get(0).getX()) {
			return Math.PI;
		} else if ((int) pos.getX() < shortestWay.get(0).getX()) {
			return 0;
		} else if ((int) pos.getY() > shortestWay.get(0).getY()) {
			return 3 * Math.PI / 2;
		} else if ((int) pos.getY() < shortestWay.get(0).getY()) {
			return Math.PI / 2;
		}
		return new Random().nextDouble() * 2 * Math.PI;
	} 
	
	@Override
	protected void handleDelivery(Guest guest) {
		if(is_deliverer && to_deliver.containsKey(guest)) {
			guest.takeDelivery(to_deliver.get(guest));
			current_delivery.remove(guest);
			to_deliver.remove(guest);
		}
	}

	@Override
	protected void handleOrder(Guest guest) {
		if(!is_deliverer && !order_status.containsKey(guest) && !current_delivery.containsKey(guest)) {
			Guest.Drink d = guest.order();
			if(d != null) {
				order_status.put(guest, d);
				order_intent.remove(guest);
			}
		}
	}

	@Override
	protected void fillUp() {
		if(!is_deliverer)
			return;
		int i = 0;
		ArrayList<Guest> remove_list = new ArrayList<>();
		for(Guest g : order_status.keySet()) {
			if(i == capacity)
				break;
			to_deliver.put(g, order_status.get(g));
			remove_list.add(g);
			i++;
		}
		for(Guest g : remove_list) {
			current_delivery.put(g, order_status.get(g));
			order_status.remove(g);
		}
		SoundHandler.FILL_UP.randomPlay();
	}
	
	protected void update_delivery(ContinuousSpace<Object> space) {
		ArrayList<Guest> remove_list = new ArrayList<>();
		for(Guest g : to_deliver.keySet()) {
			if(space.getLocation(g) == null)
				remove_list.add(g);
 		}
		for(Guest g : remove_list)
			to_deliver.remove(g);
	}
	
}
