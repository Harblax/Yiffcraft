package de.doridian.yiffcraft;

import de.doridian.yiffcraft.aura.Aura;
import de.doridian.yiffcraft.gui.ingame.GuiNotice;
import de.doridian.yiffcraft.gui.ingame.Radar;
import de.doridian.yiffcraft.gui.menu.GuiMain;
import de.doridian.yiffcraft.gui.menu.GuiWallhacks;
import de.doridian.yiffcraft.overrides.YCGuiChat;
import de.doridian.yiffcraft.overrides.YCGuiIngame;
import de.doridian.yiffcraft.overrides.YCRenderPlayer;
import de.doridian.yiffcraft.preview.YCWorldProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import wecui.WorldEditCUI;
import com.sk89q.worldedit.WorldEdit;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public final class Yiffcraft
{
	public static boolean[] valuableBlocks = new boolean[256];
	private static boolean isInited = false;
	public static Minecraft minecraft;
	public static final String YCFULLVERSION = "YC (#@BUILDNUM@/@GITREV@)";
	public static String licenseKey = "INVALID";
	public static boolean isSpecial = false;
	
	public static MinecraftApplet mcApplet;

	public static Aura aura;

	public static GuiNotice guiNotice;

	public static String currentServer = "";

	public static String URLappendix;

	private static void invalidKey()
	{
		System.out.println("Your license key is invalid, API calls *will* fail!");
	}

	public static void init(Minecraft mc, MinecraftApplet mcA)
	{
		minecraft = mc;
		mcApplet = mcA;

		if(mcApplet != null) {
			if(mcApplet.getParameter("licensekey").length() < 2) {
				invalidKey();
			}
			licenseKey = mcApplet.getParameter("licensekey");
		} else {
			isSpecial = true;
			licenseKey = "@@LICENSEKEY@@";
			minecraft.session.username = "YiffcraftTest";
		}

		SSLConnector.init();

		new Thread() {
			public void run() {
				URLappendix = "";
				try {
					URLappendix = "?user=" + URLEncoder.encode(minecraft.session.username, "UTF-8") + "&key=" + URLEncoder.encode(licenseKey, "UTF-8") + "&version=" + URLEncoder.encode(YCFULLVERSION, "UTF-8");
				}
				catch(Exception e) {
					e.printStackTrace(); 
					invalidKey();
				}
				try {
					URL url = new URL("http://ssl.yiffcraft.net/client/verify"+URLappendix);
					URLConnection conn = url.openConnection();
					System.setProperty("http.agent", "");
					conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
					conn.connect();
					BufferedReader buffre = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String check = buffre.readLine();
					buffre.close();
					if(check.equals("SPECIAL")) isSpecial = true;
					else if(!check.equals("OK")) invalidKey();
				}
				catch(Exception e) {
					e.printStackTrace();
					invalidKey();
				}

				Chat.init();
			}
		}.start();

		if(isInited) return;
		isInited = true;
		aura = new Aura();
		aura.start();
		
		configdir = new File(Minecraft.getMinecraftDir(), "config");
		configdir.mkdirs();
		
		Macros.init();
		Chat.init();
		
		minecraft.ingameGUI = new YCGuiIngame(minecraft);

		guiNotice = new GuiNotice(minecraft);

		wecui = new WorldEditCUI(minecraft);
		wecui.initialize();

		wecui.getLocalPlugin().onVersionEvent(WorldEdit.getVersion());
	}

	public static WorldEditCUI wecui;

	public static int[] rainbowOffsets = new int[256];
	public static char[] rainbowChars = {'5','1','3','2','6'};
	public static String rainbowizeText(String text, int ID) {
		if(ID < 0 || ID >= rainbowOffsets.length) return text;

		int offset = rainbowOffsets[ID] + 1;
		int useoffset = offset / 4;

		StringBuilder sb = new StringBuilder();
		int imax = text.length();
		int j = 0;
		for(int i = 0; i < imax; i++) {
			char c = text.charAt(i);
			if(c != ' ') {
				sb.append('\u00a7');
				sb.append(rainbowChars[(j + useoffset) % rainbowChars.length]);
				j++;
			}
			sb.append(c);
		}
		if(useoffset >= rainbowChars.length) {
			offset = 0;
		}
		rainbowOffsets[ID] = offset;
		return sb.toString();
	}

	/*public static void getNewSplash() {
		new Thread() {
			public void run() {
				try {
					URL url = new URL("http://ssl.yiffcraft.net/client/splash"+Yiffcraft.URLappendix);
					URLConnection conn = url.openConnection();
					System.setProperty("http.agent", "");
					conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
					conn.connect();
					BufferedReader buffre = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					GuiMainMenu.splashText = buffre.readLine();
					buffre.close();
				}
				catch(Exception ex) { ex.printStackTrace(); }
			}
		}.start();
	}*/

	public static boolean enableWallhack = false;
	public static boolean enableFullbright = false;
	public static boolean enableFly = false;
	public static boolean flymodeSpecial = true;
	public static boolean enableOutOfBody = false;
	public static int wallhackOpacity = 128;
	public static long lastPacket = 0;
	public static int guiMode = 1;
	public static int infoMode = 1;
	public static int compassMode = 1;
	public static boolean compass3D = false;
	public static int radarMode = 1;
	public static boolean enableUnpushablePlayer = false; //TODO: Add mobs, damage knockback
	public static boolean enableWaterwalk = false;
	public static int chatMode = 1; 
	public static float stepHeight = 0.5F;;
	public static File configdir = null;
	public static int notifyMode = 1;
	public static boolean enableAutoRun = false;
	public static String currentIP;
	public static int currentPort;
	public static int serverPing = -1;
	public static long lastPingPacket = 0;
	public static long clientPing = 0;
	public static int nametagMode = 0;

	public static void connectingTo(String world, int port)
	{
		currentServer = world + "_" + port;
		currentIP = world;
		currentPort = port;

		setDefaults(false);

		YCWorldProxy.dropAllWorldProxies();
		
		new Thread() {
			public void run() {
				try {
					URL url = new URL("http://ssl.yiffcraft.net/client/api"+Yiffcraft.URLappendix+"&query=checkserver&server="+Yiffcraft.currentServer);
					URLConnection conn = url.openConnection();
					System.setProperty("http.agent", "");
					conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
					conn.connect();
					BufferedReader buffre = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String check = buffre.readLine();
					buffre.close();
					if(check.equals("OK")) {

					} else if(check.equals("SPECIAL")) {

					} else {
						invalidKey();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
					invalidKey();
				}
			}
		}.start();

		Radar.reinit();
		AutoBreak.reinit();

		YCGuiChat.reloadCommands();

		Chat.ycchatinited = false;

		YCRenderPlayer.instance.setRenderAs(null);

		loadConfig();
	}

	public static File getConfig()
	{
		return new File(configdir, "main.cfg");
	}

	public static void loadConfig()
	{
		GuiWallhacks.loadConfig();
		
		try {
			BufferedReader file = new BufferedReader(new FileReader(getConfig()));
			try {
				String name; String value;
				String line; int pos;
				while((line = file.readLine()) != null) {
					try {
						pos = line.indexOf('=');
						if(pos < 0) continue;
						name = line.substring(0,pos).trim().toLowerCase();
						value = line.substring(pos+1).trim();
						if(name.equals("wallhack-opacity")) {
							wallhackOpacity = Integer.parseInt(value);
						} else if(name.equals("flymode-special")) {
							flymodeSpecial = Boolean.parseBoolean(value);
						} else if(name.equals("radar-nicknames")) {
							Radar.useNickname = Boolean.parseBoolean(value);
						} else if(name.equals("unpushable-player")) {
							enableUnpushablePlayer = Boolean.parseBoolean(value);
						} else if(name.equals("gui")) {
							guiMode = Integer.parseInt(value);
						} else if(name.equals("compass")) {
							compassMode = Integer.parseInt(value);
						} else if(name.equals("radar")) {
							radarMode = Integer.parseInt(value);
						} else if(name.equals("step-height")) {
							stepHeight = Float.parseFloat(value);
						} else if(name.equals("info")) {
							infoMode = Integer.parseInt(value);
						} else if(name.equals("show-bans")) {
							Radar.infoType = Integer.parseInt(value);
						} else if(name.equals("notify")) {
							notifyMode = Integer.parseInt(value);
						} else if(name.equals("name-tag")) {
							nametagMode = Integer.parseInt(value);
						}
					}
					catch(Exception e) { }
				}
			}
			catch(Exception e) { e.printStackTrace(); }
			file.close();
		}
		catch(Exception e) { e.printStackTrace(); }

		GuiWallhacks.loadConfig();
	}

	public static void saveConfig()
	{
		try {
			PrintWriter file = new PrintWriter(new FileWriter(getConfig()));
			file.println("wallhack-opacity="+wallhackOpacity);
			file.println("flymode-special="+flymodeSpecial);
			file.println("radar-nicknames="+Radar.useNickname);
			file.println("unpushable-player="+enableUnpushablePlayer);
			file.println("gui="+guiMode);
			file.println("compass="+compassMode);
			file.println("radar="+radarMode);
			file.println("step-height="+stepHeight);
			file.println("info="+infoMode);
			file.println("show-bans="+Radar.infoType);
			file.println("notify="+notifyMode);
			file.println("name-tag="+nametagMode);
			file.close();
		}
		catch(Exception e) { e.printStackTrace(); }
	}

	public static void setDefaults(boolean all)
	{
		enableOutOfBody = false;
		aura.smash.enable = false;
		aura.destroy.enableDestroyAll = false;
		aura.destroy.enableDestroyItems = false;
		aura.kill.enableKillMobs = false;
		aura.kill.enableKillAnimals = false;
		aura.kill.enableKillPlayers = false;
		enableWallhack = false;
		enableFullbright = false;
		enableAutoRun = false;
		chatMode = 1;

		if(!all) return; 

		enableFly = false;
		stepHeight = 0.5F;
		Speedhack.setSpeedMultiplier(1.0F);
		enableUnpushablePlayer = false;
	}

	public static Location realLoc;
	public static boolean realOnGround;

	public static void RefreshOutOfBody()
	{
		EntityPlayerSP player = minecraft.thePlayer;
		if(enableOutOfBody) {
			realLoc = new Location(player);
			realOnGround = player.onGround;
			if(minecraft.theWorld instanceof WorldClient) {
				EntityOtherPlayerMP newply = new EntityOtherPlayerMP(minecraft.theWorld, "YOU");
				newply.setPositionAndRotation(realLoc.posX,realLoc.posY-player.yOffset,realLoc.posZ,realLoc.rotationYaw,realLoc.rotationPitch);
				WorldClient worldClient = (WorldClient)minecraft.theWorld;
				worldClient.addEntityToWorld(-1,newply);
			}
		} else {
			if(!(minecraft.theWorld instanceof WorldClient)) return;
			WorldClient worldClient = (WorldClient)minecraft.theWorld;
			worldClient.removeEntityFromWorld(-1);
			player.setPositionAndRotation(realLoc.posX,realLoc.posY,realLoc.posZ,realLoc.rotationYaw,realLoc.rotationPitch);
		}
	}

	private static boolean[] oldKeyStates = new boolean[256];
	private static boolean checkDoTrigger(int key)
	{
		boolean state = Keyboard.isKeyDown(key);
		if(state != oldKeyStates[key])
		{
			oldKeyStates[key] = state;
			return state;
		}
		return false;
	}
	
	public static void onTick()
	{
		checkKeys();
	}

	public static void checkKeys()
	{
		if(minecraft.currentScreen != null) return;
		
		for(int i = 0;i < Macros.keyMacros.size();i++)
		{
			int key = Macros.keyMacros.get(i).keyCode;
			if(Macros.keyMacros.get(i).keyRepeatable) {
				if(Keyboard.isKeyDown(key))
					Macros.keyMacros.get(i).run();
			} else {
				if(checkDoTrigger(key))
					Macros.keyMacros.get(i).run();
			}
		}
	}

	public static void rerender()
	{
		if (minecraft.renderGlobal != null && minecraft.renderGlobal.worldRenderers != null) {
			WorldRenderer[] renderers = minecraft.renderGlobal.worldRenderers;
			for(int i = 0; i < renderers.length; i++) {
				renderers[i].markDirty();
			}
		}
	}

	public static void BlockChanged(int x, int y, int z, int id, int metadata)
	{
		if(AutoBreak.breakingBlock)
		{
			if(x == AutoBreak.blockX && y == AutoBreak.blockY && z == AutoBreak.blockZ)
				AutoBreak.cancelBreakBlock();
		}
	}

	public static boolean SendPacket(Packet pack)
	{
		EntityPlayerSP ply = minecraft.thePlayer;
		if(ply == null || !(ply instanceof EntityClientPlayerMP)) return false;
		((EntityClientPlayerMP)ply).sendQueue.addToSendQueue(pack);
		return true;
	}
}