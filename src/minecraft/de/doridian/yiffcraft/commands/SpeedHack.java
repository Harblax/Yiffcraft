package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Chat;
import de.doridian.yiffcraft.Speedhack;

public class SpeedHack extends BaseCommand
{
	public void run(String[] args) {
		if(args.length < 1) {
			Speedhack.setSpeedMultiplier(1F);
			return;
		}
		String arg = args[0].toLowerCase();
		if(arg.equals("+")) {
			Speedhack.setSpeedMultiplier(Speedhack.getSpeedMultiplier() + 0.01F);
		} else if(arg.equals("-")) {
			Speedhack.setSpeedMultiplier(Speedhack.getSpeedMultiplier() - 0.01F);
		} else {
			Speedhack.setSpeedMultiplier(Float.parseFloat(arg));
			Chat.addChat("Speed multiplier set to "+Speedhack.getSpeedMultiplier() + ".");
		}
	}
	public String getHelp() {
		return "Toggles and changes speed hack.";
	}
	public String getUsage() {
		return "[clock/normal/speed/+/-]";
	}
}