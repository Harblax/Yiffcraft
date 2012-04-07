package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.AutoBreak;

public class ABreak extends BaseCommand {
	public void run(String[] args) throws Exception {
		AutoBreak.enabled = !AutoBreak.enabled;
		if(!AutoBreak.enabled)
			AutoBreak.reinit();
	}
	public String getHelp() {
		return "Toggles autobreak. Autobreak makes a queue of blocks and breaks one block right after the other with the best tool available on your hotbar.";
	}
	public String getUsage() {
		return "";
	}
}
