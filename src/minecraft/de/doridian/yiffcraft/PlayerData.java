package de.doridian.yiffcraft;

import de.doridian.yiffcraft.gui.ingame.Radar;
import net.minecraft.src.EntityOtherPlayerMP;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.StringTranslate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class PlayerData
{
	public int entityID;
	public String name;
	public Location lastPos;
	public Location pos;
	public double yawToPlayer;
	public EntityOtherPlayerMP player;
	public char color;
	public NetClientHandler netclient;
	public int ping;
	
	public String nickname;
	
	public int lastAction;
	public String heldItem;
	
	public float MCBansRep;
	public int MCBansCount;
	
	public String[] MCBansLocal;
	public String[] MCBansGlobal;
	
	public void refreshYawToPlayer()
	{
		Location localpos = new Location(Yiffcraft.minecraft.thePlayer);
		localpos.posX = localpos.posX * 32D;
		localpos.posY = localpos.posY * 32D;
		localpos.posZ = localpos.posZ * 32D;
		double diffX = pos.posX - localpos.posX;
		double diffZ = pos.posZ - localpos.posZ;
		double result = Math.acos(diffZ / Math.sqrt(diffX * diffX + diffZ * diffZ)) / (Math.PI * 1);
		if(diffX > 0) {
			result = 2 - result;
		}
		yawToPlayer = result * 180;
	}
	
	public void refreshLocation()
	{
		if(player.inventory.mainInventory[player.inventory.currentItem] != null)
			heldItem = StringTranslate.getInstance().translateNamedKey(player.inventory.mainInventory[player.inventory.currentItem].getItem().getItemName()).trim();
		else
			heldItem = "None";
		if(heldItem.equals(""))
			heldItem = "Unknown";
		pos = new Location();
		pos.posX = player.serverPosX;
		pos.posY = player.serverPosY;
		pos.posZ = player.serverPosZ;
		pos.rotationYaw = player.rotationYaw;
		pos.rotationPitch = player.rotationPitch;
		
		refreshYawToPlayer();
		

		if(player.isDead) color = '0';
		else if(player.isSwinging) color = 'd';
		else if(player.isPlayerSleeping()) color = 'b';
		else if(player.canEntityBeSeen(Yiffcraft.minecraft.thePlayer)) color = 'f';
		else if(((int)(System.currentTimeMillis() / 1000L) - lastAction) > 30) color = '8';
		else if(player.isSneaking()) color = 'e';
		else if(player.isInWater()) color = '9';
		else color = '7';

		double diffX = Math.abs(pos.posX - lastPos.posX);
		double diffY = Math.abs(pos.posY - lastPos.posY);
		double diffZ = Math.abs(pos.posZ - lastPos.posZ);
		double diffP = Math.abs(pos.rotationPitch - lastPos.rotationPitch);
		double diffR = Math.abs(pos.rotationYaw - lastPos.rotationYaw);
		if( diffX > 0.1 || diffY > 0.1 || diffZ > 0.1 || diffP > 0.1 ||	diffR > 0.1 ) 
		{
			lastAction = (int)(System.currentTimeMillis() / 1000L);
		}
		lastPos = pos;
	}
	
	public PlayerData(NetClientHandler netclientx, EntityOtherPlayerMP ply, int entID)
	{
		netclient = netclientx;
		
		player = ply;
		entityID = entID;
		
		color = 'f';
		
		MCBansRep = -1.0F;
		MCBansCount = -1;
		MCBansGlobal = new String[0];
		MCBansLocal = new String[0];
		
		if(lastPos == null)
			lastPos = new Location();
		lastAction = (int)(System.currentTimeMillis() / 1000L);
		
		refreshLocation();
		
		setName(ply.username);
	}
	
	public GetInfoThread getInfoThread;
	private void getInfo()
	{
		cancelGetInfo();
		getInfoThread = new GetInfoThread();
		getInfoThread.start();
	}

	private void cancelGetInfo()
	{
		if(getInfoThread == null) return;
		getInfoThread.stop();
	}
	
	public void setName(String newname)
	{
		if(newname.equals(name)) return;
		name = newname;
		nickname = name;
		
		getInfo();
	}
	
	public String getName()
	{
		return (Radar.useNickname) ? nickname : name;
	}
	
	public String getDispName()
	{
		String namcol = "\u00a7" + color;
		return namcol = namcol + getName() + namcol;
	}
	
	public double getDistance()
	{
		Location localpos = new Location(Yiffcraft.minecraft.thePlayer);
		localpos.posX = localpos.posX * 32D;
		localpos.posY = localpos.posY * 32D;
		localpos.posZ = localpos.posZ * 32D;
		return localpos.distance(pos) / 32D;
	}
	
	public String getExtendedInfo(int type)
	{
		String output = "";
		switch(Radar.infoType) {
		case 1:
			output = "(REP: " + MCBansRep + ")";
			break;
		case 2:
			output = "(BAN# G="+MCBansGlobal.length+", L="+MCBansLocal.length+")";
			break;
		}
		Location localpos = new Location(Yiffcraft.minecraft.thePlayer);
		localpos.posX = localpos.posX * 32D;
		localpos.posY = localpos.posY * 32D;
		localpos.posZ = localpos.posZ * 32D;
		switch(type) {
		case 1:
			output += " (" + Math.round(localpos.distance(pos) / 32D) + ")";
			break;
		case 2:
			output += " (" + Math.round(localpos.distance(pos) / 32D) + ": " + Math.round(pos.posY / 32D) + ")";
			break;
		case 3:
			output += " (" + Math.round(localpos.distance(pos) / 32D) + ": " + Math.round(pos.posY / 32D) + ")";
			break;
		case 4:
			output += " (" + Math.round(localpos.distance(pos) / 32D) + ": " + heldItem + ")";
			break;
		case 5:
			Integer diff = (int)(System.currentTimeMillis() / 1000L)  - lastAction;
			String str = ((diff < 5) ? "A" : diff.toString());
			output += " (" + Math.round(localpos.distance(pos) / 32D) + ": " + str + ")";
			break;
		}
		return output;
	}
	
	class GetInfoThread extends Thread
	{
		public void run()
		{
			try {
				URL url = new URL("http://ssl.yiffcraft.net/client/api"+Yiffcraft.URLappendix+"&query=nickname&param="+name);
				URLConnection conn = url.openConnection();
				System.setProperty("http.agent", "");
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				nickname = rd.readLine().trim();
				if(nickname.length() < 1) nickname = name;
				rd.close();
			}
			catch(Exception e) { }
			
			try {
				URL url = new URL("http://ssl.yiffcraft.net/client/api"+Yiffcraft.URLappendix+"&query=pinfo&param="+name);
				URLConnection conn = url.openConnection();
				System.setProperty("http.agent", "");
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				MCBansRep = Float.parseFloat(rd.readLine());
				MCBansCount = Integer.parseInt(rd.readLine());
				
				MCBansLocal = new String[Integer.parseInt(rd.readLine())];
				MCBansGlobal = new String[Integer.parseInt(rd.readLine())];
				
				for(int i=0;i<MCBansLocal.length;i++) {
					MCBansLocal[i] = rd.readLine();
				}
				
				for(int i=0;i<MCBansGlobal.length;i++) {
					MCBansGlobal[i] = rd.readLine();
				}
				
				rd.close();
			}
			catch(Exception e) { }
		}
	}
	
	public void Remove()
	{
		Radar.players.remove(entityID);
	}
}