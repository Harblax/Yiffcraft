package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Location;
import de.doridian.yiffcraft.Yiffcraft;

public class Up extends BaseCommand
{
	public void run(String[] args) {
		double offset = Double.parseDouble(args[0]);
		Location target = new Location(Yiffcraft.minecraft.thePlayer);
		target.posY += offset;
		Yiffcraft.minecraft.thePlayer.setPosition(target.posX, target.posY, target.posZ);	
	}
	public String getHelp() {
		return "Teleports you up X blocks. (-X for down!)";
	}
	public String getUsage() {
		return "[blocks]";
	}
}