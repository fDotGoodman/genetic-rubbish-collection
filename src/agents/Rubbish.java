package agents;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Rubbish {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	
	public Rubbish(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}
	
	public void collect() {
		ContextUtils.getContext(this).remove(this);
	}
	
	public NdPoint getLocation() {
		return space.getLocation(this);
	}
}