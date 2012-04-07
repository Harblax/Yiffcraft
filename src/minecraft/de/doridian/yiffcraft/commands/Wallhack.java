package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Yiffcraft;

public class Wallhack extends BaseCommand {
	public void run(String[] args) throws Exception {
		Yiffcraft.enableWallhack = !Yiffcraft.enableWallhack;
		Yiffcraft.rerender();
	}
	public String getHelp() {
		return "Toggles wallhack.";
	}
	public String getUsage() {
		return "";
	}
}
