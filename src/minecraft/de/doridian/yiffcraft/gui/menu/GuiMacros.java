package de.doridian.yiffcraft.gui.menu;

import de.doridian.yiffcraft.KeyMacro;
import de.doridian.yiffcraft.Macros;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

public class GuiMacros extends GuiScreen
{
	public boolean drawSpecials()
	{
		return false;
	}

	public GuiMacros(GuiScreen parent)
	{
		parentScreen = parent;
		screenTitle = "Yiffcraft Macros";
	}
	
	private int x1;
	private int x2;
	private int x3;
	
	private int j;
	
	private GuiButton[] buttonsBind = new GuiButton[6];
	private GuiButton[] buttonsKey = new GuiButton[6];
	private GuiButton[] buttonsDel = new GuiButton[6];
	private void addButton()
	{
		GuiButton tmp = new GuiButton(j, x1, height / 6 + 24 * j, 240, 20, "Bind");
		controlList.add(tmp);
		buttonsBind[j] = tmp;
		
		tmp = new GuiButton(100 + j, x2, height / 6 + 24 * j, 60, 20, "Key");
		controlList.add(tmp);
		buttonsKey[j] = tmp;
		
		tmp = new GuiButton(200 + j, x3, height / 6 + 24 * j, 60, 20, "Delete");
		controlList.add(tmp);
		buttonsDel[j] = tmp;
	}
	
	private int scrollOffset = 0;
	
	public void initGui()
	{
		x1 = 20;
		x2 = 270;
		x3 = 340;
		
		for(j=0;j<6;j++) addButton();
		
		scrollDown = new GuiButton(502, x3 + 35, height / 6 + 168, 25, 20, "v");
		scrollUp = new GuiButton(501, x3, height / 6 + 168, 25, 20, "^");
		
		controlList.add(new GuiButton(500, x1, height / 6 + 168, 240, 20, "Close"));
		controlList.add(scrollDown);
		controlList.add(scrollUp);
		controlList.add(new GuiButton(503, x2, height / 6 + 168, 60, 20, "Add"));
		
		assignButtonCaps();
		enableScrollButtons();
	}
	
	public void assignButtonCaps()
	{
		for(j=0;j<6;j++) {
			int idx = j + scrollOffset;
			try {
				KeyMacro macro = Macros.keyMacros.get(idx);
				buttonsBind[j].displayString = macro.keyMacro + ((macro.keyRepeatable) ? " (REPEATING)" : "");
				buttonsKey[j].displayString = macro.keyName();
				
				buttonsBind[j].enabled = true;
				buttonsKey[j].enabled = true;
				buttonsDel[j].enabled = true;
			}
			catch(Exception e) {
				buttonsBind[j].displayString = "None";
				buttonsKey[j].displayString = "None";
				
				buttonsBind[j].enabled = false;
				buttonsKey[j].enabled = false;
				buttonsDel[j].enabled = false;
			}
		}
	}
	
	private void enableScrollButtons()
	{
		scrollDown.enabled = ((scrollOffset + 6) < Macros.keyMacros.size());
		scrollUp.enabled = (scrollOffset > 0);
	}
	
	private boolean needsRerender = false;
	protected void actionPerformed(GuiButton guibutton)
	{
		if(!guibutton.enabled) return;
		if(guibutton.id >= 500)
		{
			switch(guibutton.id) {
				case 500:
					mc.displayGuiScreen(parentScreen);
					break;
				case 501:
					scrollOffset--;
					assignButtonCaps();
					enableScrollButtons();
					break;
				case 502:
					scrollOffset++;
					assignButtonCaps();
					enableScrollButtons();
					break;
				case 503:
					KeyMacro macro = new KeyMacro("", 0);
					Macros.keyMacros.add(macro);
					int idx = Macros.keyMacros.size() - 1;
					
					if(idx > 5) {
						scrollOffset = idx - 5;
					}
					enableScrollButtons();
					
					Macros.saveConfig();
					
					mc.displayGuiScreen(new GuiMacroEdit(this, macro, idx));
					break;
			}
		}
		else if(guibutton.id >= 200)
		{
			try {
				Macros.keyMacros.remove((guibutton.id - 200) + scrollOffset);
				if(scrollOffset > 0 && ((scrollOffset + 7) > Macros.keyMacros.size())) scrollOffset--;
				assignButtonCaps();
				enableScrollButtons();
				Macros.saveConfig();
			}
			catch(Exception e) { e.printStackTrace(); }
		}
		else if(guibutton.id >= 100)
		{
			try {
				if(this.editMacroId >= 0) {
					assignButtonCaps();
				}
				int idx = (guibutton.id - 100) + scrollOffset;
				guibutton.displayString = "> " + guibutton.displayString + " <";
				this.editMacroId = idx;
			}
			catch(Exception e) { e.printStackTrace(); }			
		}
		else
		{
			try {
				int idx = (guibutton.id) + scrollOffset;
				mc.displayGuiScreen(new GuiMacroEdit(this, Macros.keyMacros.get(idx), idx));
			}
			catch(Exception e) { e.printStackTrace(); }
		}
	}
	
	protected int editMacroId;
	
	protected void keyTyped(char var1, int var2) {
		if(this.editMacroId >= 0) {
		   Macros.keyMacros.get(editMacroId).keyCode = var2;
		   this.editMacroId = -1;
		   Macros.saveConfig();
		   assignButtonCaps();
		} else {
		   super.keyTyped(var1, var2);
		}
	 }
	
	public void drawScreen(int i, int j, float f)
	{
		drawDefaultBackground();
		drawCenteredString(fontRenderer, screenTitle, width / 2, 20, 0xffffff);

		super.drawScreen(i, j, f);
	}

	private GuiButton scrollDown = null;
	private GuiButton scrollUp = null;
	
	private GuiScreen parentScreen;
	protected String screenTitle;
}
