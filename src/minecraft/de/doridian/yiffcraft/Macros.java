package de.doridian.yiffcraft;

import org.lwjgl.input.Keyboard;

import java.io.*;
import java.util.ArrayList;

public class Macros {
	
	public static ArrayList<KeyMacro> keyMacros;
	public static boolean disableMacros;
	
	public static void init()
	{
		keyMacros = new ArrayList<KeyMacro>();
		
		if(!getConfig().exists())
		{
			resetMacros();
			saveConfig();
		}
		
		loadConfig();
	}
	
	public static void resetMacros()
	{
		keyMacros = new ArrayList<KeyMacro>();
		keyMacros.add(new KeyMacro("-gui", Keyboard.KEY_TAB));
		keyMacros.add(new KeyMacro("-sh", Keyboard.KEY_HOME));
		keyMacros.add(new KeyMacro("-sh +", Keyboard.KEY_PRIOR, true));
		keyMacros.add(new KeyMacro("-sh -", Keyboard.KEY_NEXT, true));
		keyMacros.add(new KeyMacro("-oob", Keyboard.KEY_N));
		keyMacros.add(new KeyMacro("-waterw", Keyboard.KEY_M));
		keyMacros.add(new KeyMacro("-fly", Keyboard.KEY_V));
		keyMacros.add(new KeyMacro("-wallh", Keyboard.KEY_C));
		keyMacros.add(new KeyMacro("-fullbr", Keyboard.KEY_X));
	}
	
	private static File getConfig()
	{
		return new File(Yiffcraft.configdir,"macros.cfg");
	}
	
	public static void loadConfig()
	{
		keyMacros = new ArrayList<KeyMacro>();
		
		try {
			BufferedReader file = new BufferedReader(new FileReader(getConfig()));
			try {
				int key; String macro; boolean repeat;
				String line; int pos; int pos2;
				while((line = file.readLine()) != null) {
					pos = line.indexOf('=');
					pos2 = line.indexOf('=',pos+1);
					if(pos < 0) continue;
					try {
						key = Integer.parseInt(line.substring(0,pos).trim().toLowerCase());
						repeat = Boolean.parseBoolean(line.substring(pos+1,pos2));
						macro = line.substring(pos2+1);
						keyMacros.add(new KeyMacro(macro,key,repeat));
					} catch(Exception e) {}
				}
			}
			catch(Exception e) { e.printStackTrace(); }
			file.close();
		}
		catch(Exception e) {
			resetMacros();
			e.printStackTrace(); 
		}
		System.out.println("Loaded " + keyMacros.size() + " macros");
	}
	
	public static void saveConfig()
	{
		try {
			PrintWriter file = new PrintWriter(new FileWriter(getConfig()));
			for(int i=0;i<keyMacros.size();i++)
				file.println(keyMacros.get(i).toString());
			file.close();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
}
