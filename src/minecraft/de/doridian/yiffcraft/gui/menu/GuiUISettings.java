package de.doridian.yiffcraft.gui.menu;

import de.doridian.yiffcraft.Yiffcraft;
import de.doridian.yiffcraft.gui.ingame.Radar;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

public class GuiUISettings extends GuiScreen
{

	public GuiUISettings(GuiScreen parent)
	{
		parentScreen = parent;
		screenTitle = "Yiffcraft UI Config";
	}
	
	private int i;
	private int j;
	@SuppressWarnings("unchecked")
	private GuiButton addButton(String title, int buttonid)
	{
		GuiButton ret = new GuiButton(buttonid, i, height / 6 + 24 * j, title);
		controlList.add(ret);
		j++;
		return ret;
	}
	
	GuiButton guiButton;
	GuiButton mcbansButton;
	GuiButton chatButton;
	GuiButton nametagsButton;
	GuiButton compassButton;
	GuiButton radarButton;
	GuiButton infoButton;
	
	@SuppressWarnings("unchecked")
	public void initGui()
	{
		i =  (width / 2 - 100);
		j = 0;
		
		guiButton = addButton("GUI",1);
		refreshGuiToggle();
		
		mcbansButton = addButton("MCBans",2);
		refreshMCBansToggle();
		
		chatButton = addButton("Chat",3);
		refreshChatToggle();
		
		nametagsButton = addButton("Nametags",4);
		refreshNametagsToggle();
		
		compassButton = addButton("Compass",5);
		refreshCompassToggle();
		
		radarButton = addButton("Radar",6);
		refreshRadarToggle();
		
		infoButton = addButton("Info",7);
		refreshInfoToggle();
		
		controlList.add(new GuiButton(250, i, height / 6 + 168, "Close"));
	}
	
	private void refreshGuiToggle()
	{
		String setting = "Unknown";
		switch(Yiffcraft.guiMode)
		{
		case 1:
			setting = "No Overlay";
			break;
		case 2:
			setting = "All";
			break;
		case 3:
			setting = "Off";
			break;
		}
		guiButton.displayString = "GUI: " + setting;
	}
	
	private void refreshMCBansToggle()
	{
		String setting = "Unknown";
		switch(Radar.infoType)
		{
		case 0:
			setting = "None";
			break;
		case 1:
			setting = "Showing REP";
			break;
		case 2:
			setting = "Showing BAN#";
			break;
		}
		mcbansButton.displayString = "MCBans: " + setting;
	}
	
	private void refreshChatToggle()
	{
		String setting = "Unknown";
		switch(Yiffcraft.chatMode)
		{
		case 1:
			setting = "Enabled";
			break;
		case 2:
			setting = "No IRC";
			break;
		case 3:
			setting = "No Chat";
			break;
		}
		chatButton.displayString = "Chat: " + setting;
	}
	
	private void refreshNametagsToggle()
	{
		String setting = "Unknown";
		switch(Yiffcraft.nametagMode)
		{
		case 1:
			setting = "Normal";
			break;
		case 2:
			setting = "Distance";
			break;
		case 3:
			setting = "Distance/Altitude";
			break;
		case 4:
			setting = "Distance/Held Item";
			break;
		case 5:
			setting = "Distance/Idle Time";
			break;
		case 6:
			setting = "Off";
			break;
		}
		nametagsButton.displayString = "Nametags: " + setting;
	}
	
	private void refreshCompassToggle()
	{
		String setting = "Unknown";
		switch(Yiffcraft.compassMode)
		{
		case 1:
			setting = "Normal";
			break;
		case 2:
			setting = "Distance";
			break;
		case 3:
			setting = "No Players";
			break;
		case 4:
			setting = "Off";
			break;
		}
		compassButton.displayString = "Compass: " + setting;
	}
	
	private void refreshRadarToggle()
	{
		String setting = "Unknown";
		switch(Yiffcraft.radarMode)
		{
		case 1:
			setting = "Distance/Altitude";
			break;
		case 2:
			setting = "Names Only";
			break;
		case 3:
			setting = "Distance/Held Item";
			break;
		case 4:
			setting = "Distance/Idle Time";
			break;
		case 5:
			setting = "Off";
			break;
		}
		radarButton.displayString = "Radar: " + setting;
	}
	
	private void refreshInfoToggle()
	{
		String setting = "Unknown";
		switch(Yiffcraft.infoMode)
		{
		case 1:
			setting = "Position/Time";
			break;
		case 2:
			setting = "Pos/Time/Biome/Block";
			break;
		case 3:
			setting = "Off";
			break;
		}
		infoButton.displayString = "Info: " + setting;
	}

	protected void actionPerformed(GuiButton guibutton)
	{
		switch(guibutton.id)
		{
			case 250:
				mc.displayGuiScreen(parentScreen);
				onGuiClosed();
				break;
			case 1:
				Yiffcraft.guiMode++;
				if(Yiffcraft.guiMode > 3)
					Yiffcraft.guiMode = 1;
				refreshGuiToggle();
				break;
			case 2:
				Radar.infoType++;
				if(Radar.infoType > 2)
					Radar.infoType = 0;
				refreshMCBansToggle();
				break;
			case 3:
				Yiffcraft.chatMode++;
				if(Yiffcraft.chatMode > 3)
					Yiffcraft.chatMode = 1;
				Yiffcraft.minecraft.ingameGUI.clearChatMessages();
				refreshChatToggle();
				break;
			case 4:
				Yiffcraft.nametagMode++;
				if(Yiffcraft.nametagMode > 6)
					Yiffcraft.nametagMode = 1;
				refreshNametagsToggle();
				break;
			case 5:
				Yiffcraft.compassMode++;
				if(Yiffcraft.compassMode > 4)
					Yiffcraft.compassMode = 1;
				refreshCompassToggle();
				break;
			case 6:
				Yiffcraft.radarMode++;
				if(Yiffcraft.radarMode > 5)
					Yiffcraft.radarMode = 1;
				refreshRadarToggle();
				break;
			case 7:
				Yiffcraft.infoMode++;
				if(Yiffcraft.infoMode > 3)
					Yiffcraft.infoMode = 1;
				refreshInfoToggle();
		}
	}
	
	public void onGuiClosed()
	{
		Yiffcraft.saveConfig();
	}

	public void drawScreen(int i, int j, float f)
	{
		drawDefaultBackground();
		drawCenteredString(fontRenderer, screenTitle, width / 2, 20, 0xffffff);

		super.drawScreen(i, j, f);
	}

	private GuiScreen parentScreen;
	protected String screenTitle;
}
