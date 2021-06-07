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
import heuristics.PartiallyMappedCrossover;
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
	private ArrayList<Solution> offspring;
	
	private RandomReinsertion mutationHeuristic;
	private PartiallyMappedCrossover crossoverHeuristic;
	
	private double generationalGap;
	private int speed, viewDistance, populationSize, currentIteration, maxIterations, numberOfOffspring;
	private boolean removed, removeAllRubbishFlag;
	
	
	
	public Collector(ContinuousSpace<Object> space, 
			Grid<Object> grid, 
			int speed, int viewDistance, 
			boolean removeAllRubbish, 
			int populationSize, 
			int maxIterations,
			double generationalGap
			) {
		
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
		this.populationSize = populationSize;
		this.maxIterations = maxIterations;
		this.generationalGap = generationalGap;
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
		System.out.println("Initialising Genetic Algorithm..." + populationSize);
		population = new ArrayList<Solution>();
		for(int i = 0; i < populationSize; i++) {
			Solution tmp = currentSolution.deepClone();
			ArrayList<GridPoint> newRepresentation = tmp.getSolutionRepresentation();
			Collections.shuffle(newRepresentation);
			tmp.setSolutionRepresentation(newRepresentation);
			population.add(tmp);
		}
		
		this.mutationHeuristic = new RandomReinsertion();
		this.crossoverHeuristic = new PartiallyMappedCrossover();
		this.gaState = GeneticAlgorithmState.ONGOING;
		
		numberOfOffspring = (int) Math.floor(population.size() * generationalGap);
		
		/**
		 * Generate intial population
		 * Evaluate population
		 */
	}
	
	public void nextGeneticAlgorithmIteration() {
		
		offspring = new ArrayList<Solution>();
		Solution[] parents;
		for(int i = 0; i < numberOfOffspring; i++) {
			parents = rouletteWheelSelection();
			offspring.add(crossoverHeuristic.applyHeuristic(parents[0], parents[1], 1, 1));
		}
		
		
		/*
		Solution tmp = currentSolution.deepClone();
		if(mutationHeuristic.applyHeuristic(tmp, 1, 1) < currentSolution.getCost()) {
			currentSolution = tmp;
			currentSolution.printRoute();
		}
		*/
		
		/*
		 * 
		 * selection (select parents)							DONE
		 * reproduction (crossover)								DONE
		 * mutation of offspring (mutation)						DONE
		 * local search (search)						
		 * calculate fitness (evaluate population)				DONE
		 * replacement											DONE
		 * if( stoppingCriteria met) {
		 *     finishGeneticAlgorithm()
		 * }
		 */
		for(int i = 0; i < numberOfOffspring; i++) {
			int worstIndex = 0;
			for(int j = 1; j < this.populationSize; j++) {
				if(population.get(j).getCost() > population.get(worstIndex).getCost()) {
					worstIndex = j;
				}
			}
			population.remove(worstIndex);
		}
		population.addAll(offspring);
	}
	
	public void finishGeneticAlgorithm() {
		this.state = AgentState.ACTION_STATE;
	}
	
	public Solution[] rouletteWheelSelection() {
		Solution[] parents = new Solution[2];
		Random r = new Random();
		double totalFitness = 0;
		double likelihood = 0;
		double spin;
		int parent1, parent2;
		parent1 = parent2 = 0;
		for(Solution individual : population) {
			totalFitness += individual.getCost();
		}
		
		while(parent1 == 0) {
			for(int i = 0; i < population.size(); i++) {
				spin = r.nextDouble();
				double candidateCost = population.get(i).getCost();
				likelihood = (candidateCost / totalFitness);
				if(spin < likelihood) {
					parent1 = i;
					parent2 = i;
				}
			}
		}
	
		while(parent1 == parent2) {
			for(int j = 0; j < population.size(); j++) {
				spin = r.nextDouble();
				double candidateCost = population.get(j).getCost();
				likelihood = (candidateCost / totalFitness);
				if(spin < likelihood) {
					parent2 = j;
				}
			}
		}
		parents[0] = population.get(parent1);
		parents[1] = population.get(parent2);
		return parents;
	}
	
}
