package de.doridian.yiffcraft.gui.menu;

import de.doridian.yiffcraft.KeyMacro;
import de.doridian.yiffcraft.Macros;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import org.lwjgl.input.Keyboard;

public class GuiMacroEdit extends GuiScreen
{
	private GuiTextField newname;
	private GuiButton newrepeatable;
	private String xOldName;
	private int index;
	private boolean xRepeatable;
	
	public GuiMacroEdit(GuiMacros parent, KeyMacro old, int idx)
	{
		parentScreen = parent;
		screenTitle = "Change macro \"" + old.keyMacro + "\" to:";
		xOldName = old.keyMacro;
		xRepeatable = old.keyRepeatable;
		index = idx;
	}
	
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		controlList.add(new GuiButton(0, width / 2 - 100, height / 4 + 96 + 12, "Edit"));
		controlList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12, "Cancel"));
		
		newrepeatable = new GuiButton(2, width / 2 - 100, (height / 4 - 10) + 80, "XREPEATED");
		controlList.add(newrepeatable);
		setRepeatedButtonCap();
		
		newname = new GuiTextField(fontRenderer, width / 2 - 100, (height / 4 - 10) + 50, 200, 20);
		newname.setText(xOldName);
		newname.func_50033_b(true);
		newname.setMaxStringLength(32);
	}
	
	protected void mouseClicked(int i, int j, int k)
	{
		super.mouseClicked(i, j, k);
		newname.mouseClicked(i, j, k);
	}
	
	protected void setRepeatedButtonCap()
	{
		if(xRepeatable)
			newrepeatable.displayString = "Type: Repeating";
		else
			newrepeatable.displayString = "Type: Once";
	}
	
	protected void keyTyped(char c, int i)
	{
		newname.func_50037_a(c, i);
		((GuiButton)controlList.get(0)).enabled = newname.getText().length() > 0;
		if(c == '\r')
		{
			actionPerformed((GuiButton)controlList.get(0));
		}
	}
	
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}

	protected void actionPerformed(GuiButton guibutton)
	{
		if(!guibutton.enabled)
		{
			return;
		}
		if(guibutton.id == 0)
		{
			Macros.keyMacros.get(index).keyMacro = newname.getText();
			Macros.keyMacros.get(index).keyRepeatable = xRepeatable;
			Macros.saveConfig();
			parentScreen.assignButtonCaps();
			mc.displayGuiScreen(parentScreen);
		}
		else if(guibutton.id == 1)
		{
			mc.displayGuiScreen(parentScreen);
		}
		else if(guibutton.id == 2)
		{
			xRepeatable = !xRepeatable;
			setRepeatedButtonCap();
		}
	}
	
	public void drawScreen(int i, int j, float f)
	{
		drawDefaultBackground();
		drawCenteredString(fontRenderer, screenTitle, width / 2, 20, 0xffffff);

		newname.drawTextBox();
		super.drawScreen(i, j, f);
	}

	private GuiMacros parentScreen;
	protected String screenTitle;
}