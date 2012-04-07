package de.doridian.yiffcraft.gui.ingame;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class GuiNotice extends Gui {

   private Minecraft theGame;
   private int windowWidth;
   private int windowHeight;
   private String line1;
   private String line2;
   private long time;
   private RenderItem itemRender;
   private ItemStack itemStack;


   public GuiNotice(Minecraft var1) {
	  this.theGame = var1;
	  this.itemRender = new RenderItem();
   }

   public void queueNotification(String user, String msg, ItemStack stack) {
	  this.line1 = user;
	  this.line2 = msg;
	  this.time = System.currentTimeMillis();
	  this.itemStack = stack;
   }

   private void updateNotificationWindowScale() {
	  GL11.glViewport(0, 0, this.theGame.displayWidth, this.theGame.displayHeight);
	  GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
	  GL11.glLoadIdentity();
	  GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
	  GL11.glLoadIdentity();
	  this.windowWidth = this.theGame.displayWidth;
	  this.windowHeight = this.theGame.displayHeight;
	  ScaledResolution var1 = new ScaledResolution(this.theGame.gameSettings, this.theGame.displayWidth, this.theGame.displayHeight);
	  this.windowWidth = var1.getScaledWidth();
	  this.windowHeight = var1.getScaledHeight();
	  GL11.glClear(256);
	  GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
	  GL11.glLoadIdentity();
	  GL11.glOrtho(0.0D, (double)this.windowWidth, (double)this.windowHeight, 0.0D, 1000.0D, 3000.0D);
	  GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
	  GL11.glLoadIdentity();
	  GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
   }

   public void updateNotificationWindow() {
	  if(this.time != 0L) {
		 double var8 = (double)(System.currentTimeMillis() - this.time) / 3000.0D;
			this.updateNotificationWindowScale();
			GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
			GL11.glDepthMask(false);
			double var9 = var8 * 2.0D;
			if(var9 > 1.0D) {
			   var9 = 2.0D - var9;
			}

			var9 *= 4.0D;
			var9 = 1.0D - var9;
			if(var9 < 0.0D) {
			   var9 = 0.0D;
			}

			var9 *= var9;
			var9 *= var9;
			int var5 = this.windowWidth - 160;
			int var6 = 0 - (int)(var9 * 36.0D);
			int var7 = this.theGame.renderEngine.getTexture("/achievement/bg.png");
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var7);
			GL11.glDisable(2896 /*GL_LIGHTING*/);
			this.drawTexturedModalRect(var5, var6, 96, 202, 160, 32);
			this.theGame.fontRenderer.drawString(this.line1, var5 + 30, var6 + 7, -256);
			this.theGame.fontRenderer.drawString(this.line2, var5 + 30, var6 + 18, -1);

			GL11.glPushMatrix();
			GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
			RenderHelper.enableStandardItemLighting();
			GL11.glPopMatrix();
			GL11.glDisable(2896 /*GL_LIGHTING*/);
			GL11.glEnable('\u803a');
			GL11.glEnable(2903 /*GL_COLOR_MATERIAL*/);
			if(this.itemStack != null) {
				GL11.glEnable(2896 /*GL_LIGHTING*/);
				this.itemRender.renderItemIntoGUI(this.theGame.fontRenderer, this.theGame.renderEngine, this.itemStack, var5 + 8, var6 + 8);
				GL11.glDisable(2896 /*GL_LIGHTING*/);
			}
			GL11.glDepthMask(true);
			GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
	  }
   }
}