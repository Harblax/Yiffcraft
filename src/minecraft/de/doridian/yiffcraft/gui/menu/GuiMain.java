package de.doridian.yiffcraft.gui.menu;

import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

public class GuiMain extends GuiScreen
{
	protected GuiScreen guiScreen;

	public GuiMain(GuiScreen screen)
	{
		guiScreen = screen;
		screenTitle = "Yiffcraft Menu";
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

	@SuppressWarnings("unchecked")
	public void initGui()
	{
		i =  (width / 2 - 100);
		j = 0;
		
		addButton("Hacks...",5);
		addButton("Wallhack...",1);
		addButton("GUI...",2);
		addButton("Info...",3);
		addButton("Macros...",6);
		
		controlList.add(new GuiButton(250, i, height / 6 + 168, "Close"));
	}
	
	protected void actionPerformed(GuiButton guibutton)
	{
		switch(guibutton.id)
		{
			case 250:
				Yiffcraft.saveConfig();
				mc.displayGuiScreen(this.guiScreen);
				break;
			case 1:
				mc.displayGuiScreen(new GuiWallhacks(this));
				break;
			case 2:
				mc.displayGuiScreen(new GuiUISettings(this));
				break;
			case 3:
				mc.displayGuiScreen(new GuiInfoSettings(this));
				break;
			case 5:
				mc.displayGuiScreen(new GuiHackSettings(this));
				break;
			case 6:
				mc.displayGuiScreen(new GuiMacros(this));
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

	//private GuiScreen parentScreen;
	protected String screenTitle;
}
