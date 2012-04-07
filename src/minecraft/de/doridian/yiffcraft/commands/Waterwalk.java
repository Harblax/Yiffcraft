package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Chat;
import de.doridian.yiffcraft.Yiffcraft;

public class Waterwalk extends BaseCommand {
	public void run(String[] args) throws Exception {
		if(args.length < 1) {
			Yiffcraft.enableWaterwalk = !Yiffcraft.enableWaterwalk;
		} else {
			String arg = args[0].toLowerCase();
			if(arg.equals("on")) {
				Yiffcraft.enableWaterwalk = true;
			} else if(arg.equals("off")) {
				Yiffcraft.enableWaterwalk = false;
			}  else {
				return;
			}
		}
		Chat.addChat("Waterwalk is now " + (Yiffcraft.enableWaterwalk ? "ON" : "OFF"));
	}
	public String getHelp() {
		return "Toggles waterwalk.";
	}
	public String getUsage() {
		return "[on/off]";
	}
}
