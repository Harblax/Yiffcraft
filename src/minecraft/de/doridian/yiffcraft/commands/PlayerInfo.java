package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Chat;
import de.doridian.yiffcraft.PlayerData;
import de.doridian.yiffcraft.gui.ingame.Radar;

public class PlayerInfo extends BaseCommand
{
	public void run(String[] args) throws Exception {
		PlayerData ply = Radar.findPlayerByName(args[0]);
		Chat.addChat("Username: " + ply.name);
		Chat.addChat("Nickname: " + ply.nickname);
	}
	public String getHelp() {
		return "Displays YiffCraft information on a player.";
	}
	public String getUsage() {
		return "[name]";
	}
}