package heuristics;

import components.Solution;

public interface Heuristic {

	public double applyHeuristic(Solution currentSolution, double dos, double iom);
	
}
