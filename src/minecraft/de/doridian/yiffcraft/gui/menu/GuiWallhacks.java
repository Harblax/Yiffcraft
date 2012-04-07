package de.doridian.yiffcraft.gui.menu;


import de.doridian.yiffcraft.Util;
import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class GuiWallhacks extends GuiScreen
{
	public boolean drawSpecials()
	{
		return false;
	}

	public GuiWallhacks(GuiScreen parent)
	{
		parentScreen = parent;
		screenTitle = "Yiffcraft Wallhacks";
	}
	
	private String getButtonTitle(int ifid)
	{
		return Util.toStr(Yiffcraft.valuableBlocks[ifid]);
	}
	
	private int i;
	private int j;
	@SuppressWarnings("unchecked")
	private void addButton(int ifid)
	{
		controlList.add(new GuiBlockButton(ifid, i + (j % 2) * 180, height / 6 + 24 * (j >> 1), 160, 20, ifid,  getButtonTitle(ifid)));
		j++;
	}
	
	@SuppressWarnings("unchecked")
	public void initGui()
	{
		i =  (width / 2 - 175);
		j = 0;
		
		addButton(73);
		addButton(56);
		addButton(15);
		addButton(14);
		addButton(21);
		addButton(55);
		addButton(48);
		addButton(10);
		addButton(49);
		addButton(16);
		
		controlList.add(new GuiSliderWallhackOpacity(400, width / 2 - 100, height / 6 + 24 * ((j + 3) / 2), (Yiffcraft.wallhackOpacity / 255F), this));
		
		controlList.add(new GuiButton(500, width / 2 - 100, height / 6 + 168, "Apply"));
	}

	private static File getConfig()
	{
		return new File(Yiffcraft.configdir,"wallhack.cfg");
	}
	
	public static void loadConfig()
	{
		try {
			FileReader conf = new FileReader(getConfig());
			int ret;
			while((ret = conf.read()) != -1) {
				if(ret < 256) Yiffcraft.valuableBlocks[ret] = true;
			}
			conf.close();
		}
		catch(Exception e) {
			//Gold
			Yiffcraft.valuableBlocks[14] = true;
			Yiffcraft.valuableBlocks[41] = true;
			
			//Diamond
			Yiffcraft.valuableBlocks[56] = true;
			Yiffcraft.valuableBlocks[57] = true;
		}
		
		//Chests
		Yiffcraft.valuableBlocks[54] = false;
	}
	
	public boolean needsRerender = false;
	protected void actionPerformed(GuiButton guibutton)
	{
		if(guibutton.id == 500)
		{
			mc.displayGuiScreen(parentScreen);
			onGuiClosed();
		}
		else if(guibutton.id < 300)
		{
			GuiBlockButton blockButton = (GuiBlockButton)guibutton;
			Yiffcraft.valuableBlocks[blockButton.itemID] = !Yiffcraft.valuableBlocks[blockButton.itemID];
			switch(blockButton.itemID)
			{
				case 73:
					Yiffcraft.valuableBlocks[74] = Yiffcraft.valuableBlocks[73];
					break;
				case 56:
					Yiffcraft.valuableBlocks[57] = Yiffcraft.valuableBlocks[56];
					break;
				case 15:
					Yiffcraft.valuableBlocks[42] = Yiffcraft.valuableBlocks[15];
					break;
				case 14:
					Yiffcraft.valuableBlocks[41] = Yiffcraft.valuableBlocks[14];
					break;
	
				case 21:
					Yiffcraft.valuableBlocks[22] = Yiffcraft.valuableBlocks[21];
					break;
				case 8:
					Yiffcraft.valuableBlocks[9] = Yiffcraft.valuableBlocks[8];
					break;
				case 10:
					Yiffcraft.valuableBlocks[11] = Yiffcraft.valuableBlocks[10];
					break;
					
				case 55:
					Yiffcraft.valuableBlocks[75] = Yiffcraft.valuableBlocks[55];
					Yiffcraft.valuableBlocks[76] = Yiffcraft.valuableBlocks[55];
					
					Yiffcraft.valuableBlocks[93] = Yiffcraft.valuableBlocks[55];
					Yiffcraft.valuableBlocks[94] = Yiffcraft.valuableBlocks[55];
					break;
			}
			blockButton.displayString = getButtonTitle(blockButton.itemID);
			needsRerender = true;
		}
	}
	
	public void onGuiClosed()
	{
		try {
			FileWriter conf = new FileWriter(getConfig());
			for(short id=0;id<256;id++) {
				if(Yiffcraft.valuableBlocks[id]) conf.write((char)id);
			}
			conf.close();
		}
		catch(Exception e) { e.printStackTrace(); }
		
		if(Yiffcraft.enableWallhack && needsRerender) Yiffcraft.rerender();
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
