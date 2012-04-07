package de.doridian.yiffcraft.gui.menu;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class GuiBlockButton extends GuiButton
{
	public final int itemID;
	public final ItemStack itemstack;
	private final String itemName;

	public GuiBlockButton(int i, int j, int k, int id, String s)
	{
		this(i, j, k, 200, 20, id, s);
	}

	public GuiBlockButton(int i, int j, int k, int l, int i1, int id, String s)
	{
		super(i,j,k,l,i1,s);
		width = l;
		height = i1;
		itemID = id;
		itemstack = new ItemStack(itemID,1,0);
		itemName = StringTranslate.getInstance().translateNamedKey(Item.itemsList[itemID].getItemName());
	}
	
	public void drawButton(Minecraft minecraft, int i, int j)
	{
		FontRenderer fontrenderer = minecraft.fontRenderer;
		GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, minecraft.renderEngine.getTexture("/gui/gui.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		boolean flag = i >= xPosition && j >= yPosition && i < xPosition + width && j < yPosition + height;
		int k = getHoverState(flag);
		drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
		drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height);
		mouseDragged(minecraft, i, j);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(flag)
		{
			drawString(fontrenderer, itemName, xPosition + 23, yPosition + (height - 8) / 2, 0xffffa0);
			drawString(fontrenderer, displayString, xPosition + width - (fontrenderer.getStringWidth(displayString) + 5), yPosition + (height - 8) / 2, 0xffffa0);
		} else
		{
			drawString(fontrenderer, itemName, xPosition + 23, yPosition + (height - 8) / 2, 0xe0e0e0);
			drawString(fontrenderer, displayString, xPosition + width - (fontrenderer.getStringWidth(displayString) + 5), yPosition + (height - 8) / 2, 0xe0e0e0);
		}
		
		int x = (int)(xPosition + 5.0F);
		int y = (int)(yPosition + ((((float)height) - 16.0F) / 2.0F));
		
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
		
		itemRenderer.renderItemIntoGUI(fontrenderer, minecraft.renderEngine, itemstack, x, y);
		itemRenderer.renderItemOverlayIntoGUI(fontrenderer, minecraft.renderEngine, itemstack, x, y);
		RenderHelper.disableStandardItemLighting();
		
		GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
		RenderHelper.disableStandardItemLighting();
	}
	
	protected int width;
	protected int height;
	private static RenderItem itemRenderer = new RenderItem();
}
