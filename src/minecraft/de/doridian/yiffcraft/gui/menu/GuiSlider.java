package de.doridian.yiffcraft.gui.menu;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class GuiSlider extends net.minecraft.src.GuiButton
{

	public GuiSlider(int i, int j, int k, float f)
	{
		this(i, j, k, 200, 20, f);
	}
	
	public GuiSlider(int i, int j, int k, int w, int h, float f)
	{
		super(i, j, k, w, h, "");
		dragging = false;
		sliderValue = f;
		onSliderValueChanged();
	}

	protected int getHoverState(boolean flag)
	{
		return 0;
	}

	protected void mouseDragged(Minecraft minecraft, int i, int j)
	{
		if(!enabled)
		{
			return;
		}
		if(dragging)
		{
			sliderValue = (float)(i - (xPosition + 4)) / (float)(this.field_52008_a - 8);
			if(sliderValue < 0.0F)
			{
				sliderValue = 0.0F;
			}
			if(sliderValue > 1.0F)
			{
				sliderValue = 1.0F;
			}
			onSliderValueChanged();
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(xPosition + (int)(sliderValue * (float)(field_52008_a - 8)), yPosition, 0, 66, 4, 20);
		drawTexturedModalRect(xPosition + (int)(sliderValue * (float)(field_52008_a - 8)) + 4, yPosition, 196, 66, 4, 20);
	}

	public boolean mousePressed(Minecraft minecraft, int i, int j)
	{
		if(super.mousePressed(minecraft, i, j))
		{
			sliderValue = (float)(i - (xPosition + 4)) / (float)(field_52008_a - 8);
			if(sliderValue < 0.0F)
			{
				sliderValue = 0.0F;
			}
			if(sliderValue > 1.0F)
			{
				sliderValue = 1.0F;
			}
			onSliderValueChanged();
			dragging = true;
			return true;
		} else
		{
			return false;
		}
	}

	public void mouseReleased(int i, int j)
	{
		dragging = false;
	}
	
	protected void onSliderValueChanged()
	{
		//OVERRIDE ME!
	}

	public float sliderValue;
	public boolean dragging;
}
