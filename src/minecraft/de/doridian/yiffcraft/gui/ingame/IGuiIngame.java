package de.doridian.yiffcraft.gui.ingame;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;
import net.minecraft.src.ScaledResolution;

public abstract class IGuiIngame extends Gui {
	public abstract void renderGui(ScaledResolution res, Minecraft minecraft, FontRenderer fontrenderer);
}
