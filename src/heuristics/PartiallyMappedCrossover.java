package heuristics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import components.Solution;
import repast.simphony.space.grid.GridPoint;

public class PartiallyMappedCrossover implements CrossoverHeuristic {

	@Override
	public Solution applyHeuristic(Solution parent1, Solution parent2, double dos, double iom) {
		// TODO Auto-generated method stub
		Random r = new Random();
		GridPoint[] parent1Tour = (GridPoint[]) parent1.getSolutionRepresentation().toArray();
		GridPoint[] parent2Tour = (GridPoint[]) parent2.getSolutionRepresentation().toArray();
		
		int tourLength = parent1Tour.length;
		
		GridPoint[] child1Tour = new GridPoint[tourLength];
		GridPoint[] child2Tour = new GridPoint[tourLength];
		
		Arrays.fill(child1Tour, null);
		Arrays.fill(child2Tour, null);
		
		
		int index1 = r.nextInt(tourLength);
		int index2 = r.nextInt(tourLength);
		
		for(int i = smaller(index1, index2); i < larger(index1, index2); i++) {
			child1Tour[i] = parent2Tour[i];
			child2Tour[i] = parent1Tour[i];
		}
		
		for(int j = 0; j < tourLength; j++) {
			// If we can fill the cities with no conflict
			if(child1Tour[j] == null && ArrayUtils.indexOf(child1Tour, parent1Tour[j]) == -1) {
				child1Tour[j] = parent1Tour[j];
			}
			// If we find a conflict, consult the mapping
			else if(child1Tour[j] == null && ArrayUtils.indexOf(child1Tour, parent1Tour[j]) != -1) {
				child1Tour[j] = child2Tour[ArrayUtils.indexOf(child1Tour, parent1Tour[j])];
			}
			
			if(child2Tour[j] == null && ArrayUtils.indexOf(child2Tour, parent2Tour[j]) == -1) {
				child2Tour[j] = parent2Tour[j];
			}
			else if(child2Tour[j] == null && ArrayUtils.indexOf(child2Tour, parent2Tour[j]) != -1) {
				child2Tour[j] = child1Tour[ArrayUtils.indexOf(child2Tour, parent2Tour[j])];
			}

		}
		
		double c = r.nextDouble();
		GridPoint[] childTourArray = (c < 0.5) ? child1Tour : child2Tour;
		ArrayList<GridPoint> childTour = new ArrayList<GridPoint>();
		Collections.addAll(childTour, childTourArray);
		Solution offspring = new Solution(parent1.getHost(), childTour);
		return offspring;
	}
	
	public int larger(int a, int b) {
		int ret = (a < b) ? b : a;
		return ret;
	}
	
	public int smaller(int a, int b) {
		int ret = (a < b) ? a : b;
		return ret;
	}

}
