package controller;

import agents.Collector;
import agents.Rubbish;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class GeneticRubbishCollectionBuilder implements ContextBuilder<Object> {

	ContinuousSpace<Object> space;
	Grid<Object> grid;
	
	int dimensions, rubbishCount, collectorCount, collectorSpeed;
	
	@Override
	public Context build(Context<Object> context) {
		context.setId("genetic-rubbish-collection");
		
		Parameters parameters = RunEnvironment.getInstance().getParameters();
		this.dimensions = parameters.getInteger("dimensions");
		this.rubbishCount = parameters.getInteger("rubbishCount");
		this.collectorCount = parameters.getInteger("collectorCount");
		this.collectorCount = parameters.getInteger("collectorSpeed");
		
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		space = spaceFactory.createContinuousSpace("space", context, new RandomCartesianAdder<Object>(), new repast.simphony.space.continuous.WrapAroundBorders(), 50, 50);
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		this.grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(new WrapAroundBorders(), new SimpleGridAdder<Object>(), true, 50, 50));
		
		
		for(int i = 0; i < rubbishCount; i++) {
			context.add(new Rubbish(space, grid));
		}
		
		for(int i = 0; i < collectorCount; i++) {
			context.add(new Collector(space, grid, collectorSpeed));
		}
		
		
		for(Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int)pt.getX(), (int)pt.getY());
		}
		
		
		return context;
	}

}
