package de.doridian.yiffcraft;

import org.spoutcraft.client.entity.EntityData;

public class Speedhack {
	public static double getSpeedMultiplier() {
		EntityData dat = Yiffcraft.minecraft.thePlayer.getData();
		return dat.getWalkingMod();
	}
	
	public static void setSpeedMultiplier(double mult) {
		EntityData dat = Yiffcraft.minecraft.thePlayer.getData();
		dat.setAirspeedMod(mult);
		dat.setWalkingMod(mult);
		dat.setSwimmingMod(mult);
	}
}
