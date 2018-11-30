package bartenderProblem;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

public class EnvironmentStyle2D extends DefaultStyleOGL2D {
	@Override
	public Color getColor(Object agent) {
		if (agent instanceof EnvironmentElement) {
			return ((EnvironmentElement) agent).getType().getColor();
		}
		return Color.BLACK;
	}
	
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
			spatial = shapeFactory.createRectangle(15, 15);
	    }
	    return spatial;
	}
}
