package de.doridian.yiffcraft.gui.ingame;

import de.doridian.yiffcraft.*;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

import java.util.Map;
import java.util.TreeMap;

public final class Radar extends IGuiIngame
{	
	public static int infoType = 0;
	public static boolean useNickname = true;

	public static char getPlayerColor(EntityPlayer playerx)
	{
		if(!(playerx instanceof EntityOtherPlayerMP)) return 'f';
		EntityOtherPlayerMP player = (EntityOtherPlayerMP)playerx;
		PlayerData pdata = players.get(player.entityId);
		if(pdata == null) return 'f';
		return pdata.color;
	}
	
	public static String getPlayerName(EntityPlayer playerx)
	{
		if(!(playerx instanceof EntityOtherPlayerMP)) return playerx.username;
		EntityOtherPlayerMP player = (EntityOtherPlayerMP)playerx;
		PlayerData pdata = players.get(player.entityId);
		if(pdata == null) return playerx.username;
		if(Yiffcraft.nametagMode == 1)
			return "\u00a7" + pdata.color + pdata.getDispName();
		else if(Yiffcraft.nametagMode == 2)
			return "\u00a7" + pdata.color + pdata.getDispName() + "\u00a7" + pdata.color + pdata.getExtendedInfo(1);
		else if(Yiffcraft.nametagMode == 3)
			return "\u00a7" + pdata.color + pdata.getDispName() + "\u00a7" + pdata.color + pdata.getExtendedInfo(3);
		else if(Yiffcraft.nametagMode == 4)
			return "\u00a7" + pdata.color + pdata.getDispName() + "\u00a7" + pdata.color + pdata.getExtendedInfo(4);
		else if(Yiffcraft.nametagMode == 5)
			return "\u00a7" + pdata.color + pdata.getDispName() + "\u00a7" + pdata.color + pdata.getExtendedInfo(5);
		else
			return "";
	}
	
	public static PlayerData findPlayerByName(String name) throws Exception
	{
		name = name.toLowerCase();
		PlayerData pdat = null;
		for(PlayerData pdata : players.valueCollection()) {
			if(Util.stripColorCodes(pdata.getName()).toLowerCase().contains(name)) {
				if(pdat != null) throw new Exception("Multiple players found!");
				pdat = pdata;
			}
		}
		if(pdat == null) throw new Exception("No players found!");
		return pdat;
	}
	
	public static TIntObjectHashMap<PlayerData> players = new TIntObjectHashMap<PlayerData>();
	public static void reinit()
	{
		players.clear();
	}

	@SuppressWarnings("unchecked")
	public void renderGui(ScaledResolution res, Minecraft minecraft, FontRenderer fontrenderer)
	{
		if(Yiffcraft.radarMode == 5) return;
		int scaledwidth = res.getScaledWidth();
		String s;
		int i = 0;
		TreeMap<Integer,Double> sorted = new TreeMap<Integer,Double>();
		for(PlayerData pdata : players.valueCollection()) {
			int id = pdata.entityID;
			double dist = pdata.getDistance();
			sorted.put(id, dist);
		}
		sorted = Util.sortTreeMap(sorted);
		
		for(Map.Entry<Integer,Double> xobj : sorted.entrySet()) {
			PlayerData pdata = players.get(xobj.getKey());
			
			if(Yiffcraft.radarMode == 2)
				s = pdata.getDispName();
			else if(Yiffcraft.radarMode == 3)
				s = pdata.getDispName() + "\u00a7" + pdata.color + pdata.getExtendedInfo(4);
			else if(Yiffcraft.radarMode == 4)
				s = pdata.getDispName() + "\u00a7" + pdata.color + pdata.getExtendedInfo(5);
			else
				s = pdata.getDispName() + "\u00a7" + pdata.color + pdata.getExtendedInfo(2);
			fontrenderer.drawStringWithShadow(s, scaledwidth - fontrenderer.getStringWidth(s) - 2, 2 + (i * 10), pdata.color);
			i++;
		}
	}
	
	public static void addPlayer(NetClientHandler netclient, int entityID, EntityOtherPlayerMP player, Packet20NamedEntitySpawn packet20namedentityspawn)
	{	
		PlayerData pdata = players.get(entityID);
		if(pdata != null) {
			pdata.refreshLocation();
			pdata.player = player;
			pdata.setName(player.username);
			return;
		} else {
			pdata = new PlayerData(netclient, player, packet20namedentityspawn.entityId);
			players.put(pdata.entityID, pdata);
		}

		Location localpos = new Location(Yiffcraft.minecraft.thePlayer);
		localpos.posX = localpos.posX * 32D;
		localpos.posY = localpos.posY * 32D;
		localpos.posZ = localpos.posZ * 32D;
		if(Math.round(localpos.distance(pdata.pos) / 32D)<8)
		{
			if(Yiffcraft.notifyMode > 0)
				Chat.addChat("Warning: " + pdata.getName() + " appeared next to you.");
		}
		else if(Yiffcraft.notifyMode > 2)
		{
			Chat.addChat(pdata.getName() + " has entered visual range.");
		}
	}
	
	public static boolean removePlayer(NetClientHandler netclient, int entityID)
	{
		PlayerData pdata = players.get(entityID);
		if(pdata == null) return true;
		pdata.Remove();
		if(Yiffcraft.notifyMode > 2)
			Chat.addChat(pdata.getName() + " has left visual range.");
		return false;
	}
	
	public static void updateLocalPlayerPosition()
	{
		for(PlayerData pdata : Radar.players.valueCollection()) {
			pdata.refreshYawToPlayer();
		}
	}
	
	public static void updatePosition(NetClientHandler netclient, int entityID, Entity entity)
	{
		PlayerData pdata = players.get(entityID);
		if(pdata == null) return;
		
		if(entity == null || !(entity instanceof EntityOtherPlayerMP)) return;
		pdata.refreshLocation();
	}
}