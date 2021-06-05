package components;

import java.util.ArrayList;

import agents.Collector;
import repast.simphony.space.grid.GridPoint;

public class Solution {

	private double cost;
	private int solutionLength;
	private ArrayList<GridPoint> solutionRepresentation;
	private Collector host;
	
	public Solution(Collector hostAgent) {
		this.solutionRepresentation = new ArrayList<GridPoint>();
		this.host = hostAgent;
		this.cost = 0;
	}
	
	public Solution(Collector hostAgent, ArrayList<GridPoint> solRep) {
		this.solutionRepresentation = solRep;
		this.host = hostAgent;
		finaliseCollection();
	}
	
	public ArrayList<GridPoint> getSolutionRepresentation() {
		return this.solutionRepresentation;
	}
	
	public void setSolutionRepresentation(ArrayList<GridPoint> solutionRepresentation) {
		this.solutionRepresentation = solutionRepresentation;
	}
	
	public void finaliseCollection() {
		solutionLength = solutionRepresentation.size();
		this.cost = calculateCostInclusive();
		printRoute();
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public double getCost() {
		return this.cost;
	}
	
	public void addPoint(GridPoint newPoint) {
		//System.out.println("Added rubbish at coordinates X=" + newPoint.getX() + ", Y=" + newPoint.getY());
		solutionRepresentation.add(newPoint);
	}
	
	public boolean removePoint(GridPoint point) {
		if(solutionRepresentation.contains(point)) {
			solutionRepresentation.remove(point);
			
			return true;
		}
		else {
			return false;
		}
	}
	
	public double calculateCostInclusive() {
		double accumulatedCost = 0;
		accumulatedCost += calculateEuclideanDistance(host.grid.getLocation(host), solutionRepresentation.get(0));
		accumulatedCost += calculateCostExclusive();
		
		return accumulatedCost;
	}
	
	public double calculateCostExclusive() {
		double accumulatedCost = 0;
		for(int i = 0; i < solutionLength - 1; i++) {
			accumulatedCost += calculateEuclideanDistance(solutionRepresentation.get(i), solutionRepresentation.get(i + 1));
		}
		
		return accumulatedCost;
	}
	
	public double calculateEuclideanDistance(GridPoint point1, GridPoint point2) {
		return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) + Math.pow(point1.getY() - point2.getY(), 2));
	}
	
	public Solution deepClone() {
		ArrayList<GridPoint> cloneRepresentation = new ArrayList<GridPoint>();
		
		for(GridPoint p : solutionRepresentation) {
			int[] coord = new int[2];
			coord[0] = p.getX();
			coord[1] = p.getY();
			cloneRepresentation.add(new GridPoint(coord));
		}
		return new Solution(this.host, cloneRepresentation);
	}
	
	public void printRoute() {
		String route = new String("Cost: ");
		if(cost == 0) {
			route += " Not Yet Calculated, ";
		}
		else {
			route += ("" + this.cost + ", ");
		}
		route += ("Route Length: " + solutionLength + ", Route: ");
		route += "(" + solutionRepresentation.get(0).getX() + "," + solutionRepresentation.get(0).getY() + ")";
		for(int i = 1; i < solutionLength; i++) {
			route+= " -> (" + solutionRepresentation.get(i).getX() + "," + solutionRepresentation.get(i).getY() + ")";
		}
		System.out.println(route);
	}
	
}
