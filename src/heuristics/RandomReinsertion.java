package heuristics;

import java.util.ArrayList;
import java.util.Random;

import components.Solution;
import repast.simphony.space.grid.GridPoint;

public class RandomReinsertion implements Heuristic {

	@Override
	public double applyHeuristic(Solution currentSolution, double dos, double iom) {
		// TODO Auto-generated method stub
		
		ArrayList<GridPoint> solutionRepresentation = currentSolution.getSolutionRepresentation();
		Random r = new Random();
		int cityIndex, reinsertionIndex;
		cityIndex = r.nextInt(solutionRepresentation.size() - 1);
		reinsertionIndex = cityIndex;
		
		while(cityIndex == reinsertionIndex) {
			reinsertionIndex = r.nextInt(solutionRepresentation.size() - 1);
		}
				
		GridPoint tmp = solutionRepresentation.remove(cityIndex);
		solutionRepresentation.add(reinsertionIndex, tmp);
		currentSolution.setSolutionRepresentation(solutionRepresentation);
		currentSolution.calculateCostInclusive();

		return currentSolution.getCost();
	}

}
