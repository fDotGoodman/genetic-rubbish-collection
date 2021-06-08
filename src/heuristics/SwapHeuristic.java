package heuristics;

import components.Solution;

public interface SwapHeuristic {
	
	public double applyHeuristic(Solution currentSolution, int index1);
	
}
