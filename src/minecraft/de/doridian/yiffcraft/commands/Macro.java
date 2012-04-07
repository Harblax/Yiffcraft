package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Chat;
import de.doridian.yiffcraft.KeyMacro;
import de.doridian.yiffcraft.Macros;
import de.doridian.yiffcraft.Util;
import org.lwjgl.input.Keyboard;


public class Macro extends BaseCommand
{
	public void run(String[] args) {
		if(args.length < 1) {
			for(int i = 0;i < Macros.keyMacros.size();i++)
			{
				String key = Macros.keyMacros.get(i).keyName();
				String macro = Macros.keyMacros.get(i).keyMacro;
				Chat.addChat(key + ": " + macro);
			}
			return;
		}
		String arg = args[0].toLowerCase();
		if(arg.equals("list")) {
			for(int i = 0;i < Macros.keyMacros.size();i++)
			{
				String key = Macros.keyMacros.get(i).keyName();
				String macro = Macros.keyMacros.get(i).keyMacro;
				Chat.addChat(key + ": " + macro);
			}
			return;
		} else if(arg.equals("reset")) {
			Macros.resetMacros();
			Macros.saveConfig();
			Chat.addChat("Macros have been reset to their defaults.");
		} else if(arg.equals("reload")) {
			Macros.loadConfig();
			Chat.addChat("Reloaded all macros from the save file.");
		} else if(arg.equals("save")) {
			Macros.saveConfig();
			Chat.addChat("Saved macros to the save file.");
		} else if(arg.equals("rem")) {
			Chat.addChat("Type the key you want to remove macros from.");
			doRemove();
		} else if(arg.equals("add")) {
			String macro = Util.concatArray(args, 1, "");
			if(macro.length() < 1) {
				Chat.addChat("You didn't specify a macro.");
				return;
			}
			Chat.addChat("Type the key you want use for the macro.");
			doAdd(macro);
		}
	}
	
	private void doRemove() 
	{
		action = 1;
		Thread getKeyThread = new GetKeyThread();
		getKeyThread.start();
	}
	
	private void doAdd(String macro)
	{
		action = 2;
		actionMacro = macro;
		Thread getKeyThread = new GetKeyThread();
		getKeyThread.start();
	}
	
	private int action;
	private String actionMacro;
	class GetKeyThread extends Thread
	{
		public void run()
		{
			Macros.disableMacros = true;
			boolean needsInput = true;
			while(needsInput) {
				while(Keyboard.next()) {
					if(Keyboard.getEventKeyState()) {
						if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
							Chat.addChat("Canceled macro command.");
							return;
						}
						int typedKey = Keyboard.getEventKey();
						if(action == 1)
						{
							for(int i = 0;i < Macros.keyMacros.size();i++)
			 				{
			 					if(typedKey == Macros.keyMacros.get(i).keyCode)
			 						Macros.keyMacros.remove(i);
			 				}
							Macros.saveConfig();
							Chat.addChat("Removed all macros for " + Keyboard.getKeyName(typedKey) + ".");
						} else if(action == 2) {
							Macros.keyMacros.add(new KeyMacro(actionMacro,typedKey));
							Macros.saveConfig();
							Chat.addChat(actionMacro + " bound to " + Keyboard.getKeyName(typedKey) + ".");
						}
						needsInput = false;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) { needsInput = false; }
				}
			}
			Macros.disableMacros = false;
		}
	}
	
	public String getHelp() {
		return "Manipulate macro keybinds.";
	}
	public String getUsage() {
		return "[add/rem/save/reload/reset/list]";
	}
}
