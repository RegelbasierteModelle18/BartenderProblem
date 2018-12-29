package bartenderProblem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import bartenderProblem.environment.EnvironmentElement;
import bartenderProblem.environment.EnvironmentElement.Type;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Util {
	public static Grid<Object> getGrid(Context<Object> context) {
		return (Grid<Object>) context.getProjection("Simple Grid");
	}
	
	public static ContinuousSpace<Object> getSpace(Context<Object> context) {
		return (ContinuousSpace<Object>) context.getProjection("Continuous Space");
	}
	
	// delivers random envirnment of specific type
	public static EnvironmentElement getRandomEnvironmentElement(Type type, Context<Object> context) {
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		
		LinkedList<EnvironmentElement> elements = new LinkedList<>();
		
		for (int i = 0; i < grid.getDimensions().getWidth(); i++) {
			for (int j = 0; j < grid.getDimensions().getHeight(); j++) {
				for (Object o : grid.getObjectsAt(i, j)) {
					if (o instanceof EnvironmentElement) {
						if (((EnvironmentElement) o).getType() == type) {
							elements.add((EnvironmentElement) o);
						}
					}
				}
			}
		}
		
		if (elements.size() == 0) {
			return null;
		}
		
		return elements.get(new Random().nextInt(elements.size()));
	}
	
	// delivers nearest environment element of given type - avoids types on avoid elements (null to allow all types)
	public static EnvironmentElement getNextEnvironmentElement(Type type, Collection<Type> avoidElements, Context<Object> context, int x, int y) {
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		
		boolean[][] visited = new boolean[grid.getDimensions().getWidth()][grid.getDimensions().getHeight()];
		Queue<GridPoint> queue = new LinkedList<GridPoint>();
		queue.add(new GridPoint(x, y));
		
		while (queue.size() > 0) {
			GridPoint currentPosition = queue.poll();
			
			boolean avoid = false;
			for (Object o : grid.getObjectsAt(currentPosition.getX(), currentPosition.getY())) {
				if (o instanceof EnvironmentElement) {
					if (avoidElements != null && avoidElements.contains(((EnvironmentElement) o).getType())) {
						avoid = true;
						break;
					}
					if (((EnvironmentElement) o).getType() == type) {
						return (EnvironmentElement) o;
					}
				}
			}
			if(avoid) {
				continue;
			}
			
			if (currentPosition.getX() > 0) {
				if (!visited[currentPosition.getX() - 1][currentPosition.getY()]) {
					queue.add(new GridPoint(currentPosition.getX() - 1, currentPosition.getY()));
					visited[currentPosition.getX() - 1][currentPosition.getY()] = true;
				}
			}
			if (currentPosition.getX() < grid.getDimensions().getWidth() - 1) {
				if (!visited[currentPosition.getX() + 1][currentPosition.getY()]) {
					queue.add(new GridPoint(currentPosition.getX() + 1, currentPosition.getY()));
					visited[currentPosition.getX() + 1][currentPosition.getY()] = true;
				}
			}
			if (currentPosition.getY() > 0) {
				if (!visited[currentPosition.getX()][currentPosition.getY() - 1]) {
					queue.add(new GridPoint(currentPosition.getX(), currentPosition.getY() - 1));
					visited[currentPosition.getX()][currentPosition.getY() - 1] = true;
				}
			}
			if (currentPosition.getY() < grid.getDimensions().getHeight() - 1) {
				if (!visited[currentPosition.getX()][currentPosition.getY() + 1]) {
					queue.add(new GridPoint(currentPosition.getX(), currentPosition.getY() + 1));
					visited[currentPosition.getX()][currentPosition.getY() + 1] = true;
				}
			}
		}
		
		return null;
	}
	
	// delivers shortest way in an array: first element is the next position to be visited and last is the finish - avoids types of avoid elements (null to allow all types)
	public static List<GridPoint> calculatePath(int xFrom, int yFrom, int xTo, int yTo, Collection<Type> avoidElements, Context<Object> context) {
		Path path = doWideSearch(xFrom, yFrom, xTo, yTo, avoidElements, context);
		if(path == null) {
			return null;
		}
		
		List<GridPoint> result = new ArrayList<>();
		while (path.lastMove != null) {
			result.add(0, new GridPoint(path.x, path.y));
			path = path.lastMove;
		}
		
		return result;
	}
	
	private static Path doWideSearch(int xFrom, int yFrom, int xTo, int yTo, Collection<Type> avoidElements, Context<Object> context) {
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		
		boolean[][] visited = new boolean[grid.getDimensions().getWidth()][grid.getDimensions().getHeight()];
		Queue<Path> queue = new LinkedList<Path>();
		queue.add(new Path(xFrom, yFrom));
		
		while (queue.size() > 0) {
			Path currentPosition = queue.poll();
			
			if(currentPosition.x == xTo && currentPosition.y == yTo) {
				return currentPosition;
			}
			
			boolean avoid = false;
			for (Object o : grid.getObjectsAt(currentPosition.x, currentPosition.y)) {
				if (o instanceof EnvironmentElement) {
					if (avoidElements != null && avoidElements.contains(((EnvironmentElement) o).getType())) {
						avoid = true;
						break;
					}
				}
			}
			if(avoid) {
				continue;
			}
			
			if (currentPosition.x > 0) {
				if (!visited[currentPosition.x - 1][currentPosition.y]) {
					queue.add(new Path(currentPosition.x - 1, currentPosition.y, currentPosition));
					visited[currentPosition.x - 1][currentPosition.y] = true;
				}
			}
			if (currentPosition.x < grid.getDimensions().getWidth() - 1) {
				if (!visited[currentPosition.x + 1][currentPosition.y]) {
					queue.add(new Path(currentPosition.x + 1, currentPosition.y, currentPosition));
					visited[currentPosition.x + 1][currentPosition.y] = true;
				}
			}
			if (currentPosition.y > 0) {
				if (!visited[currentPosition.x][currentPosition.y - 1]) {
					queue.add(new Path(currentPosition.x, currentPosition.y - 1, currentPosition));
					visited[currentPosition.x][currentPosition.y - 1] = true;
				}
			}
			if (currentPosition.y < grid.getDimensions().getHeight() - 1) {
				if (!visited[currentPosition.x][currentPosition.y + 1]) {
					queue.add(new Path(currentPosition.x, currentPosition.y + 1, currentPosition));
					visited[currentPosition.x][currentPosition.y + 1] = true;
				}
			}
		}
		
		return null;
	}
	
	private static class Path {
		// what is access control?
		Path lastMove;
		int x;
		int y;
			
		public Path(int x, int y, Path lastMove) {
			this.x = x;
			this.y = y;
			this.lastMove = lastMove;
		}
		
		public Path(int x, int y) {
			this(x, y, null);
		}
	}
}
