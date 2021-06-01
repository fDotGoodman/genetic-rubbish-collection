package components;

import java.util.ArrayList;

import agents.Collector;
import repast.simphony.space.continuous.NdPoint;

public class Solution {

	private double cost;
	private int solutionLength;
	private ArrayList<NdPoint> solutionRepresentation;
	private Collector host;
	
	public Solution(Collector hostAgent) {
		this.solutionRepresentation = new ArrayList<NdPoint>();
		this.host = hostAgent;
	}
	
	public Solution(Collector hostAgent, ArrayList<NdPoint> solRep) {
		this.solutionRepresentation = solRep;
		this.host = hostAgent;
		finaliseCollection();
	}
	
	public void finaliseCollection() {
		solutionLength = solutionRepresentation.size();
		this.cost = calculateCostInclusive();
	}
	
	public void addPoint(NdPoint newPoint) {
		solutionRepresentation.add(newPoint);
	}
	
	public double calculateCostInclusive() {
		double accumulatedCost = 0;
		accumulatedCost += distanceBetweenTwoPoints(host.space.getLocation(host), solutionRepresentation.get(0));
		accumulatedCost += calculateCostExclusive();
		
		return accumulatedCost;
	}
	
	public double calculateCostExclusive() {
		double accumulatedCost = 0;
		for(int i = 0; i < solutionLength - 1; i++) {
			accumulatedCost += distanceBetweenTwoPoints(solutionRepresentation.get(i), solutionRepresentation.get(i + 1));
		}
		
		return accumulatedCost;
	}
	
	public double distanceBetweenTwoPoints(NdPoint point1, NdPoint point2) {
		return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) + Math.pow(point1.getY() - point2.getY(), 2));
	}
	
}
