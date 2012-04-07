package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Chat;

public class Help extends BaseCommand
{
	public void run(String[] args) throws Exception {
		if(args.length < 1) {
			String ret = "Available commands: ";
			for (String cmd : Chat.commands.keySet()) {
				ret += Chat.PREFIX + cmd + ", ";
			}
			ret = ret.substring(0,ret.length() - 2);
			Chat.addChat(ret);
		} else {
			String cmd = args[0].toLowerCase();
			if(Chat.commands.containsKey(cmd)) {
				BaseCommand baseCommand = Chat.commands.get(cmd);
				if(baseCommand == null) {
					throw new Exception("Unknown command!");
				} else {
					Chat.addChat(Chat.PREFIX + cmd + " " + baseCommand.getUsage());
					Chat.addChat(baseCommand.getHelp());
				}
			} else {
				throw new Exception("Unknown command!");
			}
		}
	}
	public String getHelp() {
		return "Helps you with commands.";
	}
	public String getUsage() {
		return "[command]";
	}
}