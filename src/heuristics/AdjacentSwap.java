package heuristics;

import java.util.ArrayList;
import java.util.Random;

import components.Solution;
import repast.simphony.space.grid.GridPoint;

public class AdjacentSwap implements SwapHeuristic {

	@Override
	public double applyHeuristic(Solution currentSolution, int index1) {
		// TODO Auto-generated method stub
		
		ArrayList<GridPoint> solutionRepresentation = currentSolution.getSolutionRepresentation();
		int index2;
		if(index1 < 0 || index1 >= solutionRepresentation.size()) {
			Random r = new Random();
			index1 = r.nextInt(solutionRepresentation.size());
			System.out.println("Invalid city locations for AdjacentSwap... Randomly picking city locations.");
		}
		if(index1 >= solutionRepresentation.size() - 1) {
			index2 = 0;
		} 
		else {
			index2 = index1 + 1;
		}
		
		GridPoint tmp = solutionRepresentation.get(index1);
		solutionRepresentation.set(index1, solutionRepresentation.get(index2));
		solutionRepresentation.set(index2, tmp);
		
		currentSolution.setSolutionRepresentation(solutionRepresentation);
		currentSolution.calculateCostInclusive();
		return currentSolution.getCost();
	}

}
