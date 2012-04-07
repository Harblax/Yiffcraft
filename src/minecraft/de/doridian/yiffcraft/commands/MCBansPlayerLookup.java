package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Chat;
import de.doridian.yiffcraft.PlayerData;
import de.doridian.yiffcraft.gui.ingame.Radar;

public class MCBansPlayerLookup extends BaseCommand
{
	public void run(String[] args) throws Exception {
		PlayerData ply = Radar.findPlayerByName(args[0]);
		
		Chat.addChat("MCBans info on " + ply.getName() + ":");
		
		int imax = ply.MCBansLocal.length;
		Chat.addChat(imax + " local ban(s):");
		for(int i=0;i<imax;i++) {
			Chat.addChat(ply.MCBansLocal[i]);
		}
		
		imax = ply.MCBansGlobal.length;
		Chat.addChat(imax + " global ban(s):");
		for(int i=0;i<imax;i++) {
			Chat.addChat(ply.MCBansGlobal[i]);
		}		
	}
	public String getHelp() {
		return "Displays advanced ban info on a player.";
	}
	public String getUsage() {
		return "[name]";
	}
}