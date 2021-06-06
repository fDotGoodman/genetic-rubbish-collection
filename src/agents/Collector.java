package agents;

import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import components.AgentState;
import components.GeneticAlgorithmState;
import components.Solution;
import heuristics.RandomReinsertion;
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
	private GeneticAlgorithmState gaState;
	private ArrayList<Solution> population;
	
	private RandomReinsertion mutationHeuristic;
	
	
	private int speed, viewDistance, populationSize, currentIteration, maxIterations;
	private boolean removed, removeAllRubbishFlag;
	
	
	
	public Collector(ContinuousSpace<Object> space, Grid<Object> grid, int speed, int viewDistance, boolean removeAllRubbish, int populationSize, int maxIterations) {
		this.space = space;
		this.grid = grid;
		this.speed = speed;
		this.state = AgentState.MAP_STATE;
		this.viewDistance = viewDistance;
		this.gaState = GeneticAlgorithmState.NOT_STARTED;
		this.currentSolution = new Solution(this);
		this.removeAllRubbishFlag = removeAllRubbish;
		this.removed = false;
		this.currentIteration = 0;
		this.maxIterations = maxIterations;
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
				
				if(gaState == GeneticAlgorithmState.NOT_STARTED) {
					
				}
				else if(gaState == GeneticAlgorithmState.INITIALISING) {
					startGeneticAlgorithm();
				}
				else if(gaState == GeneticAlgorithmState.ONGOING) {
					if(currentIteration < maxIterations) {
						nextGeneticAlgorithmIteration();
					}
					else {
						gaState = GeneticAlgorithmState.COMPLETE;
					}
					currentIteration++;
					
				}
				else if(gaState == GeneticAlgorithmState.COMPLETE) {
					finishGeneticAlgorithm();
				}
				
				break;
			
			case ACTION_STATE:
				if(!currentSolution.getSolutionRepresentation().isEmpty()) {
					pt = grid.getLocation(this);
					nghCreator = new GridCellNgh<Rubbish>(grid, pt, Rubbish.class, 1, 1);
					gridCells = nghCreator.getNeighborhood(true);
					SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
					for(GridCell<Rubbish> cell : gridCells) {
						if(cell.size() > 0) {
							removed = currentSolution.removePoint(cell.getPoint());
							if(removeAllRubbishFlag == true || removed == true) {
								for(Rubbish rub : cell.items()) {
									rub.collect();
								}
							}
						}
					}
					if(!removed && !currentSolution.getSolutionRepresentation().isEmpty()) {
						moveByDistance(space, grid, currentSolution.getSolutionRepresentation().get(0), speed);
					}
					removed = false;
				}
				else {
					System.out.println("Successfully collected all rubbish. Switching off...");
					this.state = AgentState.DORMANT_STATE;
				}				
				
				break;
			case DORMANT_STATE:
				
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
		this.gaState = GeneticAlgorithmState.INITIALISING;
		this.currentSolution.finaliseCollection();
		System.out.println("Moving to Calculation State...");
	}
	
	public void moveToActionPhase() {
		this.state = AgentState.ACTION_STATE;
	}
	
	public void startGeneticAlgorithm() {
		System.out.println("Initialising Genetic Algorithm...");
		population = new ArrayList<Solution>();
		for(int i = 0; i < populationSize; i++) {
			Solution tmp = currentSolution.deepClone();
			ArrayList<GridPoint> newRepresentation = tmp.getSolutionRepresentation();
			Collections.shuffle(newRepresentation);
			tmp.setSolutionRepresentation(newRepresentation);
			population.add(tmp);
			tmp.printRoute();
		}
		
		this.mutationHeuristic = new RandomReinsertion();
		this.gaState = GeneticAlgorithmState.ONGOING;
		
		/**
		 * Generate intial population
		 * Evaluate population
		 */
	}
	
	public void nextGeneticAlgorithmIteration() {
		
		Solution tmp = currentSolution.deepClone();
		if(mutationHeuristic.applyHeuristic(tmp, 1, 1) < currentSolution.getCost()) {
			currentSolution = tmp;
			currentSolution.printRoute();
		}
		
		/*
		 * 
		 * reproduction (select parents)
		 * recombination to produce offspring (crossover)
		 * mutation of offspring (mutation)
		 * local search (search)
		 * calculate fitness (evaluate population)
		 * 
		 * if( stoppingCriteria met) {
		 *     finishGeneticAlgorithm()
		 * }
		 */
	}
	
	public void finishGeneticAlgorithm() {
		this.state = AgentState.ACTION_STATE;
	}
	
	
	
}
