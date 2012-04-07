package de.doridian.yiffcraft.gui.menu;

import net.minecraft.src.GuiScreen;
import net.minecraft.src.ScaledResolution;

public class GuiTextBase extends GuiScreen
{
	public int textXpos = 5;
	public int textYpos = 5;
	public int textColor = 0xffffffff;
	
	public byte textAlign = 0; //0 = left, 1 = center, 2 = right
	
	private int cy;
	private int tw;
	public void drawLine(String s)
	{
		int cx;
		int sw = fontRenderer.getStringWidth(s);
		switch(textAlign) {
			case 1:
				cx = (tw - sw) / 2;
				break;
			case 2:
				cx = tw - (sw + textXpos);
				break;
			default:
				cx = textXpos;
				break;
		}
		drawString(fontRenderer,s,cx,cy,textColor);
		drawLine();
	}
	
	public void drawLine()
	{
		cy += 10;
	}
	
	public void drawScreen(int i, int j, float f)
	{
		ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		drawDefaultBackground();
		cy = textYpos;
		tw = (mc.displayWidth / scaledresolution.scaleFactor);
		drawLines(i,j,f);	
		super.drawScreen(i, j, f);
	}
	
	public void drawLines(int i, int j, float f)
	{
	
	}
	
	public boolean drawSpecials()
	{
		return false;
	}
}
