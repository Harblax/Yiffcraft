package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Yiffcraft;

public class Fullbright extends BaseCommand {
	public void run(String[] args) throws Exception {
		Yiffcraft.enableFullbright = !Yiffcraft.enableFullbright;
		Yiffcraft.rerender();
	}
	public String getHelp() {
		return "Toggles fullbright mode.";
	}
	public String getUsage() {
		return "";
	}
}
