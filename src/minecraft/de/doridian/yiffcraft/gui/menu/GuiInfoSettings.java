package de.doridian.yiffcraft.gui.menu;

import de.doridian.yiffcraft.Yiffcraft;
import de.doridian.yiffcraft.gui.ingame.Radar;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

public class GuiInfoSettings extends GuiScreen
{

	public GuiInfoSettings(GuiScreen parent)
	{
		parentScreen = parent;
		screenTitle = "Yiffcraft Info Config";
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
	
	GuiButton notifyButton;
	GuiButton nicknamesButton;
	
	@SuppressWarnings("unchecked")
	public void initGui()
	{
		i =  (width / 2 - 100);
		j = 0;
		
		nicknamesButton = addButton("Nicknames",1);
		refreshNicknameToggle();
		
		notifyButton = addButton("Notify",3);
		refreshNotifyToggle();
		
		controlList.add(new GuiButton(250, i, height / 6 + 168, "Close"));
	}
	
	private void refreshNicknameToggle()
	{
		String setting = Radar.useNickname ? "On" : "Off";
		nicknamesButton.displayString = "Nicknames: " + setting;
	}
	
	private void refreshNotifyToggle()
	{
		String setting = "Unknown";
		switch(Yiffcraft.notifyMode)
		{
		case 0:
			setting = "None";
			break;
		case 1:
			setting = "Appear";
			break;
		case 2:
			setting = "Appear/Vanish";
			break;
		case 3:
			setting = "Appear/Vanish/Visual";
			break;
		}
		notifyButton.displayString = "Notify: " + setting;
	}
	
	protected void actionPerformed(GuiButton guibutton)
	{
		switch(guibutton.id)
		{
			case 1:
				Radar.useNickname = !Radar.useNickname;
				refreshNicknameToggle();
				break;
			case 3:
				Yiffcraft.notifyMode++;
				if(Yiffcraft.notifyMode > 3)
					Yiffcraft.notifyMode = 0;
				refreshNotifyToggle();
				break;
			case 250:
				mc.displayGuiScreen(parentScreen);
				onGuiClosed();
				break;
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
