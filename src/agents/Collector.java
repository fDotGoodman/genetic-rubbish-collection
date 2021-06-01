package agents;

import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

import java.util.List;

import components.AgentState;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;

public class Collector extends Agent {

	public ContinuousSpace<Object> space;
	public Grid<Object> grid;
	private AgentState state;
	
	private int speed, viewDistance;
	
	public Collector(ContinuousSpace<Object> space, Grid<Object> grid, int speed, int viewDistance) {
		this.space = space;
		this.grid = grid;
		this.speed = speed;
		this.state = AgentState.MAP_STATE;
		this.viewDistance = viewDistance;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		switch(state) {
			case MAP_STATE:
				GridPoint pt = grid.getLocation(this);
				GridCellNgh<Rubbish> nghCreator = new GridCellNgh<Rubbish>(grid, pt, Rubbish.class, viewDistance, viewDistance);
				List<GridCell<Rubbish>> gridCells = nghCreator.getNeighborhood(true);
				SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

				GridPoint pointWithMostRubbish = null;
				int maxCount = -1;
				for(GridCell<Rubbish> cell : gridCells) {
					if(cell.size() > maxCount) {
						pointWithMostRubbish = cell.getPoint();
						maxCount = cell.size();
					}
				}
				moveByDistance(space, grid, pointWithMostRubbish, speed);
				
				
				
				break;
			
			case CALCULATION_STATE:
				
				break;
			
			case ACTION_STATE:
				
				break;
			
			default:
				break;
				
		}
	}
	
	public void moveTowards(GridPoint pt) {
		if(!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			space.moveByVector(this, speed, angle, 0);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
		}
	}
	
	public void moveToCalculationPhase() {
		this.state = AgentState.CALCULATION_STATE;
		System.out.println("This shit bussin, respectfully");
	}
	
	public void moveToActionPhase() {
		this.state = AgentState.ACTION_STATE;
	}
}
