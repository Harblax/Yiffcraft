package de.doridian.yiffcraft.rendering;

import de.doridian.yiffcraft.Location;

public class Catenary {
	//public final double L = 10.0F;
	public double len;
	public Catenary(double length) {
		len = length;
	}
	
	private double a; private double b; private double c;
	public Location[] getPoints(int number) {
		if(number <= 1) return null;
		Location[] points = new Location[number];
		double y = y1;
		double yD = (y2 - y1) / (number - 1);
		for(int i=1;i<=number;i++) {
			int x = 0;
			points[i-1] = new Location((xDiffNormal * x) + loc1.posX, y, (zDiffNormal * x) + loc1.posZ);
			y += yD;
		}
		return points;
	}
	
	private Location loc1; private Location loc2; private Location locDiff;
	public void setFirstNode(Location loc) {
		loc1 = loc;
		determinePositions();
		recompute();
	}
	public void setSecondNode(Location loc) {
		loc2 = loc;
		determinePositions();
		recompute();
	}
	public void setBothNodes(Location loc1x, Location loc2x) {
		loc1 = loc1x; loc2 = loc2x;
		determinePositions();
		recompute();
	}
	
	private double x1; private double x2; private double y1; private double y2;
	private double xDiff; private double zDiff; private double yDiff;
	private double xDiffNormal; private double zDiffNormal;
	private double length;
	private void determinePositions() {
		xDiff = loc2.posX - loc1.posX;
		yDiff = loc2.posY - loc1.posY;
		zDiff = loc2.posZ - loc1.posZ;
		
		length = Math.sqrt((xDiff * xDiff) + (zDiff * zDiff));
		
		x1 = 0;
		x2 = length;
		y1 = loc1.posY;
		y2 = loc2.posY;
		
		xDiffNormal = xDiff / length;
		zDiffNormal = zDiff / length;
	}
	
	private void recompute() {
		
	}
	
	public static double arsinh(double x) {
		return Math.log(x+Math.sqrt(x*x+1));
	}
}