package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Yiffcraft;

public class AutoRun extends BaseCommand {
	public void run(String[] args) throws Exception {
		Yiffcraft.enableAutoRun = !Yiffcraft.enableAutoRun;
	}
	public String getHelp() {
		return "Toggles auto run. Keeps you continuously running and removes the need to use the forward button.";
	}
	public String getUsage() {
		return "";
	}
}