package de.doridian.yiffcraft.aura;

//import org.lwjgl.opengl.GL11;

import de.doridian.yiffcraft.Location;
import org.lwjgl.util.ReadableColor;

//import net.minecraft.src.ModelRendererTurbo;

public abstract class BaseAura {
	//static ModelRendererTurbo modelRenderer;
	public BaseAura() {
		/*modelRenderer = new ModelRendererTurbo(0, 0);
		modelRenderer.addSphere(0, 0, 0, 1, 3, 1, 64, 64);
		modelRenderer.setPosition(0, 0, 0);*/
	}
	
	public void render(Location localpos, float f) {
		/*ReadableColor c = getColor();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glColor4b(c.getRedByte(), c.getGreenByte(), c.getBlueByte(), (byte)64);
		//modelRenderer.setPosition((float)localpos.posX, (float)localpos.posY, (float)localpos.posZ);
		modelRenderer.render(getRange());*/
	}
	
	public abstract float getRange();
	public abstract ReadableColor getColor();
	public abstract void run(Location localpos);
}
