package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Chat;
import de.doridian.yiffcraft.PlayerData;
import de.doridian.yiffcraft.Yiffcraft;
import de.doridian.yiffcraft.gui.ingame.Radar;
import net.minecraft.src.EntityPlayerSP;

public class OOBCamTo extends BaseCommand
{
	public void run(String[] args) throws Exception {
		PlayerData ply = Radar.findPlayerByName(args[0]);
		if(ply == null) {
			Chat.addChat("None or multiple players found!");
			return;
		}
		if(!Yiffcraft.enableOutOfBody) {
			Yiffcraft.enableOutOfBody = true;
			Yiffcraft.RefreshOutOfBody();
		}
		EntityPlayerSP player = Yiffcraft.minecraft.thePlayer;
		Yiffcraft.minecraft.thePlayer.setPositionAndRotation(ply.pos.posX / 32D, (ply.pos.posY / 32D) + player.yOffset, ply.pos.posZ / 32D, ply.pos.rotationYaw, ply.pos.rotationPitch);
		Chat.addChat("Set out of body camera to " + ply.getName() + ".");		
	}
	public String getHelp() {
		return "Sets out of body camera to player.";
	}
	public String getUsage() {
		return "[name]";
	}
}