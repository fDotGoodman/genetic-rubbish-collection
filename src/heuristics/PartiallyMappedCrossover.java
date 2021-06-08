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
		GridPoint[] parent1Tour = parent1.getSolutionRepresentation().toArray(new GridPoint[0]);
		GridPoint[] parent2Tour = parent2.getSolutionRepresentation().toArray(new GridPoint[0]);
		
		int tourLength = parent1Tour.length;
		
		GridPoint[] child1Tour = new GridPoint[tourLength];
		GridPoint[] child2Tour = new GridPoint[tourLength];
		
		Arrays.fill(child1Tour, null);
		Arrays.fill(child2Tour, null);
		 
		int index1 = r.nextInt(tourLength);
		int index2 = index1;
		
		while(index2 == index1) {
			index2 = r.nextInt(tourLength);
		}
		
		// Step 1
		for(int i = smaller(index1, index2); i < larger(index1, index2); i++) {
			child2Tour[i] = parent1Tour[i];
		}
		// Step 2 - Find those elements i
		for(int i = smaller(index1, index2); i < larger(index1, index2); i++) {
			// Step 2 - Find those elements i
			if(ArrayUtils.contains(child2Tour, parent2Tour[i]) == false) {
				// Step 3 - This gives us the element j
				GridPoint elementJ = child2Tour[i];
				int jPositionInP2 = ArrayUtils.indexOf(parent2Tour, elementJ);
				if(child2Tour[jPositionInP2] == null) {
					child2Tour[jPositionInP2] = parent2Tour[i];
				}
				else {
					GridPoint elementK = child2Tour[jPositionInP2];
					int kPositionInP2 = ArrayUtils.indexOf(parent2Tour, elementK);
					child2Tour[kPositionInP2] = parent2Tour[i];
				}
			}
		}
		
		// Step 6 - Populate the rest
		for(int i = 0; i < parent1.getSolutionLength(); i++) {
			if(child2Tour[i] == null) {
				child2Tour[i] = parent2Tour[i];
			}
		}
		
		// Step 1
		for(int i = smaller(index1, index2); i < larger(index1, index2); i++) {
			child1Tour[i] = parent2Tour[i];
		}
		// Step 2 - Find those elements i
		for(int i = smaller(index1, index2); i < larger(index1, index2); i++) {
			// Step 2 - Find those elements i
			if(ArrayUtils.contains(child1Tour, parent2Tour[i]) == false) {
				// Step 3 - This gives us the element j
				GridPoint elementJ = child1Tour[i];
				int jPositionInP1 = ArrayUtils.indexOf(parent1Tour, elementJ);
				if(child1Tour[jPositionInP1] == null) {
					child1Tour[jPositionInP1] = parent1Tour[i];
				}
				else {
					GridPoint elementK = child1Tour[jPositionInP1];
					int kPositionInP1 = ArrayUtils.indexOf(parent1Tour, elementK);
					child1Tour[kPositionInP1] = parent1Tour[i];
				}
			}
		}
		
		// Step 6 - Populate the rest
		for(int i = 0; i < parent1.getSolutionLength(); i++) {
			if(child1Tour[i] == null) {
				child1Tour[i] = parent1Tour[i];
			}
		}

		
		double c = r.nextDouble();
		GridPoint[] childTourArray = (c < 0.5) ? child1Tour : child2Tour;
		ArrayList<GridPoint> finalChildTour = new ArrayList<GridPoint>();
		Collections.addAll(finalChildTour, childTourArray);
		Solution offspring = new Solution(parent1.getHost(), finalChildTour);
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
