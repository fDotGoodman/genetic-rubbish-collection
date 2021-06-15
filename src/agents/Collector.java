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
import components.MemeticAlgorithmState;
import components.Solution;
import heuristics.DavisHillClimbing;
import heuristics.OrderedCrossover;
import heuristics.RandomReinsertion;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;

/**
 * Collector class extending the Agent object. The collector is the main acting agent in this, it maps rubbish, optimises its TSP route, then actions the path
 * @author Felix
 *
 */
public class Collector extends Agent {

	public ContinuousSpace<Object> space;
	public Grid<Object> grid;
	private AgentState state;
	private Random r;
	private Solution currentSolution;
	private MemeticAlgorithmState gaState;
	private ArrayList<Solution> population;
	private ArrayList<Solution> offspring;
	private GridPoint targetLocation;
	
	private RandomReinsertion mutationHeuristic;
	private OrderedCrossover crossoverHeuristic;
	private DavisHillClimbing hillClimbingHeuristic;
	
	private double generationalGap;
	private int speed, viewDistance, populationSize, currentIteration, maxIterations, numberOfOffspring, dos, iom;
	private boolean removed, removeAllRubbishFlag;
	
	
	/**
	 * Constructor to instantiate the variables
	 * @param space The ContinuousSpace projection
	 * @param grid The Grid Projection
	 * @param speed The distance this agent can move by when moving in either the map or action states
	 * @param viewDistance The distance the collector can see for when mapping out rubbish in the Map phase
	 * @param removeAllRubbish A boolean flag to denote whether or not the Collector collects all rubbish it encounters in the Action phase, regardless of its inclusion in its optimised route
	 * @param populationSize The number of individuals in the GA population
	 * @param maxIterations The maximum number of iterations/epochs the GA will run for
	 * @param generationalGap Double variable denoting the percentage of those in the GA population that are replaced by fitter offspring
	 * @param dos The Depth of Search parameter for hill climbing
	 * @param iom The Intensity of Mutation parameter for mutation heuristics
	 */
	public Collector(ContinuousSpace<Object> space, 
			Grid<Object> grid, 
			int speed, 
			int viewDistance, 
			boolean removeAllRubbish, 
			int populationSize, 
			int maxIterations,
			double generationalGap,
			int dos,
			int iom
			) {
		
		this.space = space;
		this.grid = grid;
		this.speed = speed;
		this.state = AgentState.MAP_STATE;
		this.viewDistance = viewDistance;
		this.gaState = MemeticAlgorithmState.NOT_STARTED;
		this.currentSolution = new Solution(this);
		this.removeAllRubbishFlag = removeAllRubbish;
		this.removed = false;
		this.currentIteration = 0;
		this.populationSize = populationSize;
		this.maxIterations = maxIterations;
		this.generationalGap = generationalGap;
		this.dos = dos;
		this.iom = iom;
		r = new Random();
		this.targetLocation = new GridPoint(r.nextInt(this.grid.getDimensions().getWidth()), r.nextInt(this.grid.getDimensions().getWidth()));
	}
	
	
	@ScheduledMethod(start = 1, interval = 1)
	/**
	 * A scheduled method that determining the agent actions at each tick of the simulation
	 */
	public void step() {
		switch(state) {
			case MAP_STATE:
				GridPoint pt = grid.getLocation(this);
				GridCellNgh<Rubbish> nghCreator = new GridCellNgh<Rubbish>(grid, pt, Rubbish.class, viewDistance, viewDistance);
				List<GridCell<Rubbish>> gridCells = nghCreator.getNeighborhood(true);
				SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

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
				
				if( Solution.calculateEuclideanDistance( pt,  targetLocation)  < viewDistance) {
					this.targetLocation = new GridPoint(r.nextInt(this.grid.getDimensions().getWidth()), r.nextInt(this.grid.getDimensions().getWidth()));
				}
				else {
					moveByDistance(space, grid, targetLocation, speed);
				}
				
				break;
			
			case CALCULATION_STATE:
				
				if(gaState == MemeticAlgorithmState.NOT_STARTED) {
					
				}
				else if(gaState == MemeticAlgorithmState.INITIALISING) {
					startGeneticAlgorithm();
				}
				else if(gaState == MemeticAlgorithmState.ONGOING) {
					if(currentIteration < maxIterations) {
						nextGeneticAlgorithmIteration();
					}
					else {
						gaState = MemeticAlgorithmState.COMPLETE;
					}
					currentIteration++;
					
				}
				else if(gaState == MemeticAlgorithmState.COMPLETE) {
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
						removed = currentSolution.removePoint(cell.getPoint());
						if(removeAllRubbishFlag == true || removed == true) {
							for(Rubbish rub : cell.items()) {
								rub.collect();
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
	
	/**
	 * Transition method to finish the Map phase of the simulation, and begin the Memetic Algorithm
	 */
	public void moveToCalculationPhase() {
		this.state = AgentState.CALCULATION_STATE;
		this.gaState = MemeticAlgorithmState.INITIALISING;
		this.currentSolution.finaliseCollection();
		System.out.println("Moving to Calculation State...");
	}
	
	/**
	 * Transition function to move the Collector to the action state
	 */
	public void moveToActionPhase() {
		this.state = AgentState.ACTION_STATE;
	}
	
	/**
	 * Method to initialise the Memetic Algorithm, instantiating a population and creating the relevant heuristics
	 */
	public void startGeneticAlgorithm() {
		System.out.println("Initialising Genetic Algorithm...");
		currentSolution.printRoute();
		population = new ArrayList<Solution>();
		for(int i = 0; i < populationSize; i++) {
			Solution tmp = currentSolution.deepClone();
			ArrayList<GridPoint> newRepresentation = tmp.getSolutionRepresentation();
			Collections.shuffle(newRepresentation);
			tmp.setSolutionRepresentation(newRepresentation);
			population.add(tmp);
		}
		
		this.mutationHeuristic = new RandomReinsertion();
		this.crossoverHeuristic = new OrderedCrossover();
		this.hillClimbingHeuristic = new DavisHillClimbing();
		this.gaState = MemeticAlgorithmState.ONGOING;
		
		numberOfOffspring = (int) Math.floor(population.size() * generationalGap);
	}
	
	/**
	 * Method to process the next Memetic Algorithm iteration, involving parent selection, crossover, mutation, hill climbing and replacement
	 */
	public void nextGeneticAlgorithmIteration() {
		System.out.println("Epoch: " + this.currentIteration);
		offspring = new ArrayList<Solution>();
		Solution[] parents;
		for(int i = 0; i < numberOfOffspring; i++) {
			parents = rouletteWheelSelection();
			Solution candidateOffspring = crossoverHeuristic.applyHeuristic(parents[0], parents[1], dos, iom);
	
			mutationHeuristic.applyHeuristic(candidateOffspring, dos, iom);
			hillClimbingHeuristic.applyHeuristic(candidateOffspring, dos, iom);
			offspring.add(candidateOffspring);
		}
		
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
	
	/**
	 * Method to finish the Memetic Algorithm, and transition the Collector to the action phase
	 */
	public void finishGeneticAlgorithm() {
		Solution bestSolution = population.get(0);
		double bestFitness = population.get(0).getCost();
		for(int i = 1; i < population.size(); i++) {
			if(population.get(i).getCost() < bestFitness) {
				bestSolution = population.get(i);
				bestFitness = population.get(i).getCost();
			}
		}
		
		this.currentSolution = bestSolution;
		currentSolution.printRoute();
		System.out.println("Genetic Algorithm Complete!");
		this.state = AgentState.ACTION_STATE;
	}
	
	/**
	 * Method implementing the Roulette Wheel parent selection algorithm
	 * @return A solution array containing both chosen parents
	 */
	public Solution[] rouletteWheelSelection() {
		Solution[] parents = new Solution[2];
		Random r = new Random();
		double totalFitness = 0;
		double likelihood = 0;
		double spin;
		int parent1, parent2;
		parent1 = 0;
		parent2 = 0;
		for(Solution individual : population) {
			totalFitness += individual.getCost();
		}
		
		while(parent1 == 0) {
			for(int p1counter = 0; p1counter < population.size(); p1counter++) {
				spin = r.nextDouble();
				double candidateCost = population.get(p1counter).getCost();
				likelihood = (candidateCost / totalFitness);
				if(spin < likelihood) {
					parent1 = p1counter;
					parent2 = p1counter;
				}
			}
		}
	
		while(parent1 == parent2) {
			for(int p2counter = 0; p2counter < population.size(); p2counter++) {
				spin = r.nextDouble();
				double candidateCost = population.get(p2counter).getCost();
				likelihood = (candidateCost / totalFitness);
				if(spin < likelihood) {
					parent2 = p2counter;
				}
			}
		}
		parents[0] = population.get(parent1);
		parents[1] = population.get(parent2);
		return parents;
	}
	
}
