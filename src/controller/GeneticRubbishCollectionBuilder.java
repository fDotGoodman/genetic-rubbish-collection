package controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import agents.Collector;
import agents.Rubbish;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
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
	
	int dimensions, rubbishCount, collectorCount, collectorSpeed, finishMapTick, viewDistance, geneticAlgorithmCutOffTick, populationSize;
	boolean collectAllRubbish;
	
	@Override
	public Context build(Context<Object> context) {
		context.setId("genetic-rubbish-collection");
		
		Parameters parameters = RunEnvironment.getInstance().getParameters();
		this.dimensions = parameters.getInteger("dimensions");
		this.rubbishCount = parameters.getInteger("rubbishCount");
		this.collectorCount = parameters.getInteger("collectorCount");
		this.collectorSpeed = parameters.getInteger("collectorSpeed");
		this.finishMapTick = parameters.getInteger("mapTerminationTick");
		this.viewDistance = parameters.getInteger("viewDistance");
		this.populationSize = parameters.getInteger("populationSize");
		this.geneticAlgorithmCutOffTick = parameters.getInteger("geneticAlgorithmCutOffTick");
		this.collectAllRubbish = parameters.getBoolean("collectAllRubbish");
		
	    ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
	    ScheduleParameters scheduleParams = ScheduleParameters.createOneTime(finishMapTick);
	    schedule.schedule(scheduleParams, this, "triggerMapEnd", context);
	    scheduleParams = ScheduleParameters.createOneTime(geneticAlgorithmCutOffTick);
	    schedule.schedule(scheduleParams, this, "triggerMapEnd", context);
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		space = spaceFactory.createContinuousSpace("space", context, new RandomCartesianAdder<Object>(), new repast.simphony.space.continuous.WrapAroundBorders(), 50, 50);
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		this.grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(new WrapAroundBorders(), new SimpleGridAdder<Object>(), true, 50, 50));
		
		
		for(int i = 0; i < rubbishCount; i++) {
			context.add(new Rubbish(space, grid));
		}
		
		for(int i = 0; i < collectorCount; i++) {
			context.add(new Collector(space, grid, collectorSpeed, viewDistance, collectAllRubbish, populationSize));
		}
		
		
		for(Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int)pt.getX(), (int)pt.getY());
		}
		
		return context;
	}
	
	public void triggerMapEnd(Context context) {
		Stream<Collector> collectorStream = context.getObjectsAsStream(Collector.class);
		List<Collector> collectorList = collectorStream.collect(Collectors.toList());
		collectorList.forEach((collector) -> collector.moveToCalculationPhase());
		
	}

}
