package agents;

import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

import java.util.List;
import java.util.Random;

import components.AgentState;
import components.Solution;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;

public class Collector extends Agent {

	public ContinuousSpace<Object> space;
	public Grid<Object> grid;
	private AgentState state;
	private Random r;
	private Solution currentSolution;
	
	private int speed, viewDistance;
	
	
	public Collector(ContinuousSpace<Object> space, Grid<Object> grid, int speed, int viewDistance) {
		this.space = space;
		this.grid = grid;
		this.speed = speed;
		this.state = AgentState.MAP_STATE;
		this.viewDistance = viewDistance;
		this.currentSolution = new Solution(this);
		r = new Random();
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		switch(state) {
			case MAP_STATE:
				GridPoint pt = grid.getLocation(this);
				GridCellNgh<Rubbish> nghCreator = new GridCellNgh<Rubbish>(grid, pt, Rubbish.class, viewDistance, viewDistance);
				List<GridCell<Rubbish>> gridCells = nghCreator.getNeighborhood(true);
				SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
				int maxCount = -1;
				for(GridCell<Rubbish> cell : gridCells) {
					if(cell.size() > 0) {
						if(!currentSolution.getSolutionRepresentation().contains(cell.getPoint())) {
							//System.out.println("Added rubbish at coordinates X=" + cell.getPoint().getX() + ", Y=" + cell.getPoint().getY());
							currentSolution.addPoint(cell.getPoint());
						}
						else {
							//System.out.println("Not adding X=" + cell.getPoint().getX() + ", Y=" + cell.getPoint().getY());
						}
					}
				}
				moveRandomly(space, grid, r, speed);
				
				
				
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
