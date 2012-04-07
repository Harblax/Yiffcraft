package de.doridian.yiffcraft.overrides;

import de.doridian.yiffcraft.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiIngame;

public class YCGuiIngame extends GuiIngame {
	public YCGuiIngame(Minecraft mc) {
		super(mc);
	}

	public void renderGameOverlay(float var1, boolean var2, int var3, int var4) {
		super.renderGameOverlay(var1, var2, var3, var4);
		Renderer.renderGui();
	}
}
