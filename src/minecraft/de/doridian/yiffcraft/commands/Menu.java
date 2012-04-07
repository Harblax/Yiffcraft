package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Renderer;

public class Menu extends BaseCommand {
	public void run(String[] args) throws Exception {
		Renderer.openMenu();
	}
	public String getHelp() {
		return "Displays main menu";
	}
	public String getUsage() {
		return "";
	}
}
