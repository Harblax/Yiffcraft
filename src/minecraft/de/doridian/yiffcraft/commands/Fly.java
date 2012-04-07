package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Yiffcraft;

public class Fly extends BaseCommand {
	public void run(String[] args) throws Exception {
		if(args.length < 1) {
			Yiffcraft.enableFly = !Yiffcraft.enableFly;
			return;
		}
		String arg = args[0].toLowerCase();
		if(arg.equals("3d")) {
			Yiffcraft.flymodeSpecial = true;
		} else if(arg.equals("planar")) {
			Yiffcraft.flymodeSpecial = false;
		} else if(arg.equals("tog")) {
			Yiffcraft.flymodeSpecial = !Yiffcraft.flymodeSpecial;
		}
	}
	public String getHelp() {
		return "Toggles fly hack or fly mode.";
	}
	public String getUsage() {
		return "[3d/planar/tog]";
	}
}

