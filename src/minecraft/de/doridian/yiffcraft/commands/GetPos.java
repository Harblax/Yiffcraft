package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Chat;
import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.src.EntityPlayerSP;

public class GetPos extends BaseCommand
{
	public void run(String[] args) throws Exception {
		EntityPlayerSP player = Yiffcraft.minecraft.thePlayer;
		Chat.addChat("X:" + (int)(player.posX) + "  Y:" + (int)(player.posY) + "  Z:" + (int)(player.posZ));		
	}
	public String getHelp() {
		return "Prints current coordinates.";
	}
	public String getUsage() {
		return "";
	}
}