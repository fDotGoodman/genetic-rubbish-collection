package agents;

import java.util.Random;

import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Agent {

	public void moveToLocation(ContinuousSpace<Object> space, Grid<Object> grid, GridPoint destination) {
		if(!destination.equals(grid.getLocation(this))) {
			double[] dst = new double[2];
			
			dst[0] = destination.getX();
			dst[1] = destination.getY();
		}
	}
	
	public void moveRandomly(ContinuousSpace<Object> space, Grid<Object> grid, Random r, int distance) {
		double angle = r.nextDouble() * 2 * Math.PI;
		
		space.moveByVector(this, distance, angle, 0);
		grid.moveTo(this, (int)space.getLocation(this).getX(), (int)space.getLocation(this).getY());
	}
	
	public void moveByDistance(ContinuousSpace<Object> space, Grid<Object> grid, GridPoint destination, int distance) {
		if(!destination.equals(grid.getLocation(this))) {
			NdPoint currentPoint = space.getLocation(this);
			NdPoint destinationPoint = new NdPoint(destination.getX(), destination.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, currentPoint, destinationPoint);
			
			space.moveByVector(this, 1, angle, 0);
			currentPoint = space.getLocation(this);
			grid.moveTo(this, (int)space.getLocation(this).getX(), (int)space.getLocation(this).getY());
			
		}
	}
	
}
