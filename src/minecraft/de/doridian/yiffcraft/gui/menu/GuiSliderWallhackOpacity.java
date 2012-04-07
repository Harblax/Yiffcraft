package de.doridian.yiffcraft.gui.menu;

import de.doridian.yiffcraft.Yiffcraft;

public class GuiSliderWallhackOpacity extends GuiSlider
{

	public GuiSliderWallhackOpacity(int i, int j, int k, float f, GuiWallhacks gui)
	{
		super(i, j, k, f);
		myGui = gui;
	}
	
	public GuiSliderWallhackOpacity(int i, int j, int k, int w, int h, float f, GuiWallhacks gui)
	{
		super(i, j, k, w, h, f);
		myGui = gui;
	}
	
	protected void onSliderValueChanged()
	{
		Yiffcraft.wallhackOpacity = (int)(sliderValue * 255F);
		displayString = "Wallhack opacity: " + (int)(sliderValue * 100F) + "%";
		
		if(myGui != null && Yiffcraft.enableWallhack) myGui.needsRerender = true;
	}
	
	private GuiWallhacks myGui;
}
