package de.doridian.yiffcraft.commands;

public class BaseCommand
{
	public void run(String[] args) throws Exception {
		throw new Exception("Implement me!");
	}
	public String getHelp() {
		return "IMPLEMENT ME";
	}
	public String getUsage() {
		return "IMPLEMENT ME";
	}
}