package de.doridian.yiffcraft;

import net.minecraft.src.Entity;

public class Target {
	private Entity targetEnt;
	private Location targetLoc;
	private boolean targetIsEntity;

	public Target(PlayerData ply) {
		this(ply.player);
	}
	
	public Target(Entity ent) {
		targetIsEntity = true;
		targetEnt = ent;
		targetLoc = null;
	}
	
	public Target(Location loc) {
		targetIsEntity = false;
		targetLoc = loc;
		targetEnt = null;
	}
	
	public Location getPos() {
		if(targetIsEntity) return new Location(targetEnt);
		else return targetLoc.clone();
	}
}