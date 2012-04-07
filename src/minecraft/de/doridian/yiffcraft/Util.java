package de.doridian.yiffcraft;

import net.minecraft.src.*;
import org.spoutcraft.client.SpoutClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Util
{
	public static WorldClient getWorld() {
		return (WorldClient)SpoutClient.getInstance().getWorld();
	}
	
	public static String sha256(String str) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();
		byte[] inputBytes = digest.digest(str.getBytes());
		StringBuffer outputString = new StringBuffer();
		for(int i = 0; i < inputBytes.length; i++) outputString.append(Integer.toHexString(0xFF & inputBytes[i]));
		return outputString.toString();
	}
	
	public static int GetDamageVS(EntityLiving living)
	{
		return GetDamageVS(Yiffcraft.minecraft.thePlayer.inventory.getDamageVsEntity(living),living);
	}

	public static int GetDamageVS(ItemStack itemStack, EntityLiving living)
	{
		return GetDamageVS(itemStack.getDamageVsEntity(living), living);
	}

	public static int GetDamageVS(Item item, EntityLiving living)
	{
		return GetDamageVS(item.getDamageVsEntity(living), living);
	}

	public static int GetDamageVS(int dmg, EntityLiving living)
	{
		if((float)living.heartsLife > (float)living.heartsHalvesLife / 2.0F)
		{
			if(dmg <= living.lastDamage)
			{
				return 0;
			}
			return (dmg - living.lastDamage);
		}
		else
		{
			return dmg;
		}
	}
	
	private static final Pattern ccPat = Pattern.compile("\u00a7.");
	public static String stripColorCodes(String str)
	{
		return ccPat.matcher(str).replaceAll("");
	}
	
	public static String toStr(boolean val)
	{
		return (val) ? "On" : "Off";
	}
	public static int toColor(boolean val)
	{
		return (val) ? 0x00ff00 : 0xff0000;
	}
	
	public static boolean toBool(String val)
	{
		val = val.toLowerCase();
		return (val.equals("1") || val.equals("on") || val.equals("yes"));
	}

	public static String concatArray(String[] array, int start, String def) {
		if(array.length <= start) return def;
		if(array.length <= start + 1) return array[start];
		String ret = array[start];
		for(int i=start+1;i<array.length;i++) {
			ret += " " + array[i];
		}
		return ret;
	}
	
	public static Location GetMouseOver()
	{
		MovingObjectPosition objectMouseOver = Yiffcraft.minecraft.objectMouseOver;
		if(objectMouseOver == null)
			return new Location(0,-999,0);
		return new Location(objectMouseOver.blockX,objectMouseOver.blockY,objectMouseOver.blockZ);	
	}
	
	public static int GetBlockID(String str) throws Exception
	{
		try {
			int i = Integer.parseInt(str);
			if(i > 0) return i;
		}
		catch(Exception e) { }
		str = "tile."+str.replace('_',' ').trim().toLowerCase();
		for(int j=0;j<256;j++) {
			Block b = Block.blocksList[j];
			if(b == null) continue;
			if(b.getBlockName().toLowerCase().equals(str)) return j;
		}
		throw new Exception("Block not found!");
	}
	public static String getCardinalDirection(double yaw) {
		double rot = (yaw - 90) % 360;
		if (rot < 0) {
			rot += 360.0;
		}
		return getDirection(rot);
	}
	
	public static String getCardinalDirection(double dirX, double dirY) {
		double result;
		result = Math.acos(dirY / Math.sqrt(dirX * dirX + dirY * dirY));
		if(dirX > 0) {
			result = (Math.PI * 2) - result;
		}
		return getCardinalDirection((result / Math.PI) * 180);
	}
	
	public static String getTime(long time) {
		int daytime = (int)(time % 24000), h = daytime / 1000, m = (int)((daytime % 1000) * 0.06f);
		return (h<10 ? "0" : "") + h + (m<10 ? ":0" : ":") + m;
	}

	public static String getRealTime(long time) {
		long d = time / 1728000; time %= 1728000;
		long h = time / 72000; time %= 72000;
		long m = time / 1200; time %= 1200;
		long s = time / 20; time %= 20;
		long u = time / 2;
		return ""+d+(h<10?":0":":")+h+(m<10?":0":":")+m+(s<10?":0":":")+s+"."+u;
	}
	
	private static String getDirection(double rot) {
		if (0 <= rot && rot < 22.5) {
			return "N";
		} else if (22.5 <= rot && rot < 67.5) {
			return "NE";
		} else if (67.5 <= rot && rot < 112.5) {
			return "E";
		} else if (112.5 <= rot && rot < 157.5) {
			return "SE";
		} else if (157.5 <= rot && rot < 202.5) {
			return "S";
		} else if (202.5 <= rot && rot < 247.5) {
			return "SW";
		} else if (247.5 <= rot && rot < 292.5) {
			return "W";
		} else if (292.5 <= rot && rot < 337.5) {
			return "NW";
		} else if (337.5 <= rot && rot < 360.0) {
			return "N";
		} else {
			return null;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static TreeMap sortTreeMap(TreeMap map)
	{
		ValueComparator bvc = new ValueComparator(map);
		TreeMap sorted_map = new TreeMap(bvc);
		sorted_map.putAll(map);
		return sorted_map;
	}
}

@SuppressWarnings("rawtypes")
class ValueComparator implements Comparator {
	  Map base;
	  public ValueComparator(Map base) {
		  this.base = base;
	  }

	  public int compare(Object a, Object b) {
		if((Double)base.get(a) < (Double)base.get(b)) {
		  return 1;
		} else if((Double)base.get(a) == (Double)base.get(b)) {
		  return 0;
		} else {
		  return -1;
		}
	  }
}