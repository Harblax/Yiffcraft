package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Chat;
import de.doridian.yiffcraft.Util;
import de.doridian.yiffcraft.Yiffcraft;

public class Compass3D extends BaseCommand {
	public void run(String[] args) throws Exception {
		if(args.length < 1) Yiffcraft.compass3D = !Yiffcraft.compass3D;
		else Yiffcraft.compass3D = Util.toBool(args[0]);
		if(Yiffcraft.compass3D)
			Chat.addChat("Compass is now in 3D mode.");
		else
			Chat.addChat("Compass is now in 2D mode.");
	}
	public String getHelp() {
		return "Toggles compass mode between 3D and 2D.";
	}
	public String getUsage() {
		return "[on/off]";
	}
}

