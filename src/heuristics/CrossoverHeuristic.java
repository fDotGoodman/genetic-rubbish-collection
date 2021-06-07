package heuristics;

import components.Solution;

public interface CrossoverHeuristic {

	public Solution applyHeuristic(Solution parent1, Solution parent2, double dos, double iom);
	
}
