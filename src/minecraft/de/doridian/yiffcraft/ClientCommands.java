package de.doridian.yiffcraft;

import de.doridian.yiffcraft.overrides.YCGuiChat;
import de.doridian.yiffcraft.overrides.YCRenderPlayer;
import de.doridian.yiffcraft.preview.YCWorldProxy;
import net.minecraft.src.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;

public class ClientCommands {
	static class ClientCommandException extends Exception {
		public ClientCommandException(String message) {
			super(message);
		}
	}

	private static YCWorldProxy myWorldProxy = null;
	
	public static void outgoing(String str, NetClientHandler handler) {
		outgoing(str.getBytes(), handler);
	}

	public static void register(NetClientHandler handler)
	{
		byte[] array = "yiffcraft".getBytes();
		Packet250CustomPayload payload = new Packet250CustomPayload();
		payload.channel = "REGISTER";
		payload.length = array.length;
		payload.data = array;
		if(handler == null) {
			Yiffcraft.SendPacket(payload);
		} else {
			handler.addToSendQueue(payload);
		}
	}

	public static void outgoing(byte[] array, NetClientHandler handler) {
		Packet250CustomPayload payload = new Packet250CustomPayload();
		payload.channel = "yiffcraft";
		payload.length = array.length;
		payload.data = array;
		if(handler == null) {
			Yiffcraft.SendPacket(payload);
		} else {
			handler.addToSendQueue(payload);
		}
	}

	public static void incoming(byte[] array)
	{
		try {
			String str = new String(array);
			incoming(str.charAt(0), str.substring(1));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void incoming(char cmd, final String args)
	{
		try {
			switch(cmd) {
				case 'c':
					commandSheet(args);
					break;
				case 't':
					transmute(args);
					break;
				case 'd':
					dataValue(args);
					break;
				default:
					Chat.addChat("Unknown YCC command: " + cmd + "|" + args);
					break;
			}
		} catch(Exception e) {
			Chat.addChat("Error in YCC command " + cmd + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	static class ShapeYCData {
		public static final int ENTITY_STATUS = -1;
		public static final int VEHICLE_TYPE = -2;
		public static final int ITEM_TYPE = -3;
		public static final int ITEM_DATA = -4;
		public static final int ITEM_COUNT = -5;
		public static final int PAINTING_NAME = -6;
	}
	
	private static void dataValue(String args) throws Exception {
		String[] argArr = args.split("\\|");
		if(argArr.length != 3) {
			throw new ClientCommandException("Invalid argc");
		}

		Entity renderAs = YCRenderPlayer.instance.getRenderAs();
		if(renderAs == null) {
			throw new ClientCommandException("EntData command while not being transmuted?!");
		}

		Integer index = Integer.valueOf(argArr[0]);
		Class cls = Class.forName(argArr[1]);
		Object value = cls.getConstructor(String.class).newInstance(argArr[2]);

		if(index >= 0) {
			renderAs.getDataWatcher().updateObject(index, value);
			return;
		}

		switch(index) {
			case ShapeYCData.ENTITY_STATUS:
				renderAs.handleHealthUpdate((Byte)value);
				break;
			case ShapeYCData.VEHICLE_TYPE:
				if(!(renderAs instanceof EntityMinecart)) break;
				EntityMinecart minecart = (EntityMinecart)renderAs;
				int val = (Integer)value;
				switch(val) {
					case 10:
					case 11:
					case 12:
						minecart.minecartType = val - 10;
						break;
				}
				break;
			case ShapeYCData.ITEM_TYPE:
				if(!(renderAs instanceof EntityItem)) break;
				EntityItem item = (EntityItem)renderAs;
				item.item.itemID = (Integer)value;
				break;
			case ShapeYCData.ITEM_DATA:
				if(!(renderAs instanceof EntityItem)) break;
				EntityItem item2 = (EntityItem)renderAs;
				item2.item.setItemDamage((Integer)value);
				break;
			case ShapeYCData.ITEM_COUNT:
				if(!(renderAs instanceof EntityItem)) break;
				EntityItem item3 = (EntityItem)renderAs;
				item3.item.stackSize = (Integer)value;
				break;
			case ShapeYCData.PAINTING_NAME:
				if(!(renderAs instanceof EntityPainting)) break;
				EntityPainting painting = (EntityPainting)renderAs;
				painting.art = EnumArt.valueOf((String)value);
				break;
			default:
				throw new ClientCommandException("Unknown special index: " + index);
		}
	}
	
	private static HashMap<String, Class> simpleTransmutes = new HashMap<String, Class>();

	static {
		simpleTransmutes.put("FishingHook", EntityFishHook.class);
		simpleTransmutes.put("Potion", EntityPotion.class);
		simpleTransmutes.put("Egg", EntityEgg.class);
	}
	
	private static void transmute(String args) {
		if(myWorldProxy == null || !myWorldProxy.isValid()) {
			myWorldProxy = YCWorldProxy.getWorldProxy(false, true, false);
		}

		if(args.isEmpty()) {
			YCRenderPlayer.instance.setRenderAs(null);
			return;
		}

		double yOffset = 0; float yawOffset = 0;
		String[] argArr = args.split("\\|");
		if(argArr.length >= 3) {
			args = argArr[0];
			yawOffset = Float.parseFloat(argArr[1]);
			yOffset = Double.parseDouble(argArr[2]);
		}

		Entity ent = null;

		if(simpleTransmutes.containsKey(args)) {
			try {
				Class cls = simpleTransmutes.get(args);
				Constructor cnst = cls.getConstructor(World.class);
				ent = (Entity)cnst.newInstance(myWorldProxy);
			}
			catch(Exception e) { e.printStackTrace(); }
		} else {
			ent = EntityList.createEntityByName(args, myWorldProxy);
			if(ent instanceof EntityItem) {
				ent = new EntityItem(myWorldProxy, Yiffcraft.minecraft.thePlayer.posX, Yiffcraft.minecraft.thePlayer.posY, Yiffcraft.minecraft.thePlayer.posZ, new ItemStack(Block.stone));
			} else if(ent instanceof EntityPainting) {
				((EntityPainting)ent).art = EnumArt.Alban;
			}
		}

		if(ent == null) return;

		YCRenderPlayer.instance.yawOffset = yawOffset;
		YCRenderPlayer.instance.yOffset = yOffset;
		YCRenderPlayer.instance.setRenderAs(ent);
	}
	
	private static void commandSheet(final String args) {
		new Thread() {
			private HashSet<URL> loadedURLs;

			public void run() {
				loadedURLs = new HashSet<URL>();
				HashMap<String, String> additionalCommands = new HashMap<String, String>();
				addToHashmap(additionalCommands, null, args);
				YCGuiChat.reloadCommands(additionalCommands);
			}

			private void addToHashmap(HashMap<String, String> additionalCommands, URL lastURL, String ctext) {
				try {
					URL url = new URL(lastURL, ctext);
					if(loadedURLs.contains(url)) return;
					loadedURLs.add(url);
					URLConnection conn = url.openConnection();
					conn.connect();
					BufferedReader buffre = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String cline;
					while((cline = buffre.readLine()) != null) {
						if(cline.isEmpty()) continue;
						switch(cline.charAt(0)) {
							case '@':
								addToHashmap(additionalCommands, url, cline.substring(1));
								break;
							case ';':
								//Ignore the line, its a comment!
								break;
							default:
								int splpos = cline.indexOf('|');
								if(splpos <= 0) break;
								String cmd = cline.substring(0, splpos);
								String cmdUsage = cline.substring(splpos + 1);
								additionalCommands.put(cmd, cmdUsage);
								break;
						}

					}
					buffre.close();
				}
				catch(Exception e) { System.out.println("Error parsing: " + ctext); e.printStackTrace(); }
			}
		}.start();
	}
}
