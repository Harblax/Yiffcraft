package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Chat;
import de.doridian.yiffcraft.Util;
import de.doridian.yiffcraft.Yiffcraft;

public class Aura extends BaseCommand
{
	public void run(String[] args) {
		if(args.length < 1) {
			Chat.addChat("Mob Kill aura: " + Util.toStr(Yiffcraft.aura.kill.enableKillMobs));
			Chat.addChat("Animal Kill aura: " + Util.toStr(Yiffcraft.aura.kill.enableKillAnimals));
			Chat.addChat("Player Kill aura: " + Util.toStr(Yiffcraft.aura.kill.enableKillPlayers));
			Chat.addChat("Smash aura: " + Util.toStr(Yiffcraft.aura.smash.enable));
			Chat.addChat("Destroy aura: " + Util.toStr(Yiffcraft.aura.destroy.enableDestroyAll));
			Chat.addChat("Destroy Item aura: " + Util.toStr(Yiffcraft.aura.destroy.enableDestroyItems));
			return;
		}
		String type = args[0].toLowerCase();
		if(type.equals("smash")) {
			if(args.length < 2) {
				Yiffcraft.aura.smash.enable = !Yiffcraft.aura.smash.enable;
			} else {
				Yiffcraft.aura.smash.enable = Util.toBool(args[1]);
			}
			Chat.addChat("Smash aura: " + Util.toStr(Yiffcraft.aura.smash.enable));
		} else if(type.equals("item")) {
			if(args.length < 2) {
				Yiffcraft.aura.destroy.enableDestroyItems = !Yiffcraft.aura.destroy.enableDestroyItems;
			} else {
				Yiffcraft.aura.destroy.enableDestroyItems = Util.toBool(args[1]);
			}
			Chat.addChat("Destroy Item aura: " + Util.toStr(Yiffcraft.aura.destroy.enableDestroyItems));
		} else if(type.equals("destroy")) {
			if(args.length < 2) {
				Yiffcraft.aura.destroy.enableDestroyAll = !Yiffcraft.aura.destroy.enableDestroyAll;
			} else {
				Yiffcraft.aura.destroy.enableDestroyAll = Util.toBool(args[1]);
			}
			Chat.addChat("Destroy aura: " + Util.toStr(Yiffcraft.aura.destroy.enableDestroyAll)); 
		} else if(type.equals("kill")) {
			if(args.length < 2) {
				Yiffcraft.aura.kill.enableKillMobs = !(Yiffcraft.aura.kill.enableKillMobs || Yiffcraft.aura.kill.enableKillAnimals || Yiffcraft.aura.kill.enableKillPlayers);
			} else {
				Yiffcraft.aura.kill.enableKillMobs = Util.toBool(args[1]);
			}
			Yiffcraft.aura.kill.enableKillAnimals = Yiffcraft.aura.kill.enableKillMobs;
			Yiffcraft.aura.kill.enableKillPlayers = Yiffcraft.aura.kill.enableKillMobs;
			Chat.addChat("All Kill auras: " + Util.toStr(Yiffcraft.aura.kill.enableKillMobs));						
		} else if(type.equals("killmob") || type.equals("killmobs") || type.equals("killmonster") || type.equals("killmonsters")) {
			if(args.length < 2) {
				Yiffcraft.aura.kill.enableKillMobs = !Yiffcraft.aura.kill.enableKillMobs;
			} else {
				Yiffcraft.aura.kill.enableKillMobs = Util.toBool(args[1]);
			}
			Chat.addChat("Mob Kill aura: " + Util.toStr(Yiffcraft.aura.kill.enableKillMobs));
		} else if(type.equals("killanimal") || type.equals("killanimals")) {
			if(args.length < 2) {
				Yiffcraft.aura.kill.enableKillAnimals = !Yiffcraft.aura.kill.enableKillAnimals;
			} else {
				Yiffcraft.aura.kill.enableKillAnimals = Util.toBool(args[1]);
			}
			Chat.addChat("Animal Kill aura: " + Util.toStr(Yiffcraft.aura.kill.enableKillAnimals));
		} else if(type.equals("killplayer") || type.equals("killplayers") || type.equals("killply") || type.equals("killplys")) {
			if(args.length < 2) {
				Yiffcraft.aura.kill.enableKillPlayers = !Yiffcraft.aura.kill.enableKillPlayers;
			} else {
				Yiffcraft.aura.kill.enableKillPlayers = Util.toBool(args[1]);
			}
			Chat.addChat("Player Kill aura: " + Util.toStr(Yiffcraft.aura.kill.enableKillPlayers));
		}		
	}
	public String getHelp() {
		return "Enables/disables an aura (smash = break all items that break instantly (torches, redstone, etc.), item = destroy dropped items). If you provide no arguments, current aura status is displayed.";
	}
	public String getUsage() {
		return "[kill/killmob/killplayer/killanimal/smash/item] [on/off]";
	}
}