package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Chat;
import de.doridian.yiffcraft.Location;
import de.doridian.yiffcraft.PlayerData;
import de.doridian.yiffcraft.Yiffcraft;
import de.doridian.yiffcraft.gui.ingame.Radar;
import de.doridian.yiffcraft.rendering.particles.PathTracer;
import net.minecraft.src.Entity;

public class FindPath extends BaseCommand
{
	private boolean targetIsEntity = true;
	private Location targetLocation;
	private Entity targetEntity;

	private Thread spawnParticles;
	public FindPath() {
		spawnParticles = new Thread() {
			public void run() {
				while(true) {
					try {
						if(targetIsEntity) {
							if(targetEntity != null) PathTracer.addNew(Yiffcraft.minecraft.thePlayer,targetEntity);
						} else {
							if(targetLocation != null) PathTracer.addNew(Yiffcraft.minecraft.thePlayer,targetLocation);
						}
					}
					catch(Exception e) { }
					try {
						Thread.sleep(500);
					}
					catch(Exception e) { }
				}
			}
		};
		spawnParticles.start();
	}
	
	public void resetTarget() {
		targetLocation = null;
		targetEntity = null;
		Chat.addChat("No longer finding path.");
	}

	public void run(String[] args) throws Exception {
		if(args.length < 1) {
			resetTarget();
		} else if(args.length < 3) {
			PlayerData pdata = Radar.findPlayerByName(args[0]);
			targetIsEntity = true;
			targetEntity = pdata.player;
			targetLocation = null;
			Chat.addChat("Finding path to " + pdata.name + ".");
		} else {
			targetLocation = new Location(Float.parseFloat(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]), 0, 0);
			targetIsEntity = false;
			targetEntity = null;
			Chat.addChat("Finding path to ("+targetLocation.posX+", "+targetLocation.posY+", "+targetLocation.posZ+").");
		}
	}
	public String getHelp() {
		return "Find a path to player or a set of coordinates.";
	}
	public String getUsage() {
		return "[name] OR [X] [Y] [Z]";
	}
}