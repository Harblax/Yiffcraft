package de.doridian.yiffcraft.gui.menu;

import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

public class GuiHackSettings extends GuiScreen
{

	public GuiHackSettings(GuiScreen parent)
	{
		parentScreen = parent;
		screenTitle = "Yiffcraft Hacks Config";
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
	
	GuiButton flymodeButton;
	GuiButton unpushableButton;
	
	
	@SuppressWarnings("unchecked")
	public void initGui()
	{
		i =  (width / 2 - 100);
		j = 0;
		
		flymodeButton = addButton("Flymode",1);
		refreshFlyModeToggle();
		
		unpushableButton = addButton("Unpushable",3);
		refreshUnpushableToggle();
		
		controlList.add(new GuiButton(250, i, height / 6 + 168, "Close"));
	}
	
	private void refreshFlyModeToggle()
	{
		flymodeButton.displayString = "Flymode: " + (Yiffcraft.flymodeSpecial ? "3D" : "Planar");
	}
	
	private void refreshUnpushableToggle()
	{
		unpushableButton.displayString = "Unpushable: " + (Yiffcraft.enableUnpushablePlayer ? "On" : "Off");
	}

	public boolean needsRerender = false;
	protected void actionPerformed(GuiButton guibutton)
	{
		switch(guibutton.id)
		{
			case 250:
				mc.displayGuiScreen(parentScreen);
				onGuiClosed();
				break;
			case 1:
				Yiffcraft.flymodeSpecial = !Yiffcraft.flymodeSpecial;
				refreshFlyModeToggle();
				break;
			case 3:
				Yiffcraft.enableUnpushablePlayer = !Yiffcraft.enableUnpushablePlayer;
				refreshUnpushableToggle();
				break;
		}
	}
	
	public void onGuiClosed()
	{
		if(needsRerender) Yiffcraft.rerender();
		needsRerender = false;
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
