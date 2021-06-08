package heuristics;

import components.Solution;

public class DavisHillClimbing implements Heuristic {

	@Override
	public double applyHeuristic(Solution currentSolution, double dos, double iom) {
		// TODO Auto-generated method stub
		
		AdjacentSwap mutationHeuristic = new AdjacentSwap();
		int counter = 0;
		Solution bestCandidate = currentSolution;
		Solution currentCandidate;
		double bestFitness, currentFitness;
		bestFitness = currentSolution.getCost();
		bestCandidate = currentSolution.deepClone();
		while(counter < Math.floor(dos * 10)) {
			
			for(int i = 0; i < currentSolution.getSolutionLength(); i++) {
				currentCandidate = bestCandidate.deepClone();
				currentFitness = mutationHeuristic.applyHeuristic(currentCandidate, i);
				if(currentFitness < bestFitness) {
					bestCandidate = currentCandidate.deepClone();
					bestFitness = bestCandidate.getCost();
				}
				
			}
			
			// Handles swapping the last city with the first city
			currentCandidate = bestCandidate.deepClone();
			currentFitness = mutationHeuristic.applyHeuristic(currentCandidate, currentSolution.getSolutionLength());
			if(currentFitness < bestFitness) {
				bestCandidate = currentCandidate.deepClone();
				bestFitness = bestCandidate.getCost();
			}
			
			counter++;
		}

		currentSolution = bestCandidate.deepClone();
		return currentSolution.getCost();
	}

}
