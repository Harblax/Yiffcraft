package de.doridian.yiffcraft;

import de.doridian.yiffcraft.gui.ingame.GuiCompass;
import de.doridian.yiffcraft.gui.ingame.IGuiIngame;
import de.doridian.yiffcraft.gui.ingame.Radar;
import de.doridian.yiffcraft.gui.menu.GuiMain;
import de.doridian.yiffcraft.preview.YCWorldProxy;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiChat;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ScaledResolution;
import org.lwjgl.opengl.GL11;
import wecui.event.WorldRenderEvent;
import wecui.obfuscation.RenderObfuscation;

import java.util.ArrayList;

public class Renderer {
	public static ArrayList<IGuiIngame> guis = new ArrayList<IGuiIngame>();
	
	private static boolean openMenu = false;
	private static WorldRenderEvent event;
	
	public static void openMenu() {
		openMenu = true;
	}
	
	static {
		guis.add(new Radar());
		guis.add(new GuiCompass());
		event = new WorldRenderEvent(Yiffcraft.wecui);
	}
	
	public static void renderEffects(float f)
	{
		Yiffcraft.aura.render(f);

		event.setPartialTick(f);
		RenderObfuscation.disableLighting();

		GL11.glBlendFunc(770 /*GL_SRC_ALPHA*/, 771 /*GL_ONE_MINUS_SRC_ALPHA*/);
		GL11.glLineWidth(2F);
		GL11.glEnable(3042 /*GL_BLEND*/);
		GL11.glDisable(3008 /*GL_ALPHA_TEST*/);

		YCWorldProxy.renderWorldProxies(f);

		GL11.glDisable(3553 /*GL_TEXTURE_2D*/);

		GL11.glDepthMask(false);
		//GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
		GL11.glDepthFunc(GL11.GL_GEQUAL);

		Yiffcraft.wecui.getEventManager().callEvent(event);

		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDisable(2929 /*GL_DEPTH_TEST*/);

		GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
		GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
		GL11.glDisable(3042 /*GL_BLEND*/);
		GL11.glEnable(3008 /*GL_ALPHA_TEST*/);

		RenderObfuscation.enableLighting();
	}
	
	public static void renderGui()
	{
		if(openMenu) {
			Yiffcraft.minecraft.displayGuiScreen(new GuiMain(Yiffcraft.minecraft.currentScreen));
			openMenu = false;
		}
		
		if(Yiffcraft.guiMode == 3) return;
		FontRenderer fontrenderer = Yiffcraft.minecraft.fontRenderer;
		
		ScaledResolution scaledresolution = new ScaledResolution(Yiffcraft.minecraft.gameSettings, Yiffcraft.minecraft.displayWidth, Yiffcraft.minecraft.displayHeight);
		for(IGuiIngame gui : guis) gui.renderGui(scaledresolution, Yiffcraft.minecraft, fontrenderer);

		if(Yiffcraft.infoMode != 3)
		{
			String posStr;
			if(Yiffcraft.compassMode == 4)
				posStr = "(" + (int)(Yiffcraft.minecraft.thePlayer.posX) + "," + (int)(Yiffcraft.minecraft.thePlayer.posY) + "," + (int)(Yiffcraft.minecraft.thePlayer.posZ) + "):" + Util.getCardinalDirection(Yiffcraft.minecraft.thePlayer.rotationYaw);
			else
				posStr = "(" + (int)(Yiffcraft.minecraft.thePlayer.posX) + "," + (int)(Yiffcraft.minecraft.thePlayer.posY) + "," + (int)(Yiffcraft.minecraft.thePlayer.posZ) + ")";
			if(Yiffcraft.infoMode == 2)
			{
				Location mouse = Util.GetMouseOver();
				String blockInfo = "";
				if(mouse.posY != -999)
				{
					int id = Yiffcraft.minecraft.theWorld.getBlockId((int)mouse.posX, (int)mouse.posY, (int)mouse.posZ);
					if(id != 0)
						blockInfo = "[" + id + "]";
				}
				ItemStack heldItem = Yiffcraft.minecraft.thePlayer.inventory.mainInventory[Yiffcraft.minecraft.thePlayer.inventory.currentItem];
				String itemInfo = "";
				if(heldItem != null)
				{
					itemInfo = "[" + heldItem.itemID + "]";
				}
				else
				{
					itemInfo = "[-]";
				}
				
				long var25 = Runtime.getRuntime().maxMemory();
				long var30 = Runtime.getRuntime().totalMemory();
				long var29 = Runtime.getRuntime().freeMemory();
				long var21 = var30 - var29;
				String meminfo = "F:(" + net.minecraft.client.Minecraft.framesPerSecond + ") M:" + var21 * 100L / var25 + "%(" + var21 / 1024L / 1024L + "/" + var25 / 1024L / 1024L + ") P:(" + Yiffcraft.clientPing + "/" + Yiffcraft.serverPing + ")";
				
				fontrenderer.drawStringWithShadow("Yiffcraft " + posStr + " ["+Util.getTime(Yiffcraft.minecraft.theWorld.getWorldTime())+"] (" + Yiffcraft.minecraft.theWorld.getWorldChunkManager().getBiomeGenAt((int)(Yiffcraft.minecraft.thePlayer.posX),(int)(Yiffcraft.minecraft.thePlayer.posZ)).biomeName + ") " + meminfo + " " + itemInfo + " " + blockInfo, 0, 2, 0xe0e0e0);
			}
			else
			{
				fontrenderer.drawStringWithShadow("Yiffcraft " + posStr + " ["+Util.getTime(Yiffcraft.minecraft.theWorld.getWorldTime())+"]", 0, 2, 0xe0e0e0);
			}
		}
		else
		{
			fontrenderer.drawStringWithShadow("Yiffcraft", 0, 2, 0xe0e0e0);
		}
		
		long curLag = System.currentTimeMillis() - Yiffcraft.lastPacket;
		if(curLag > 1500)
		{
			int scaledheight = scaledresolution.getScaledHeight();
			int color;
			if(curLag < 5000)
				color = 0xffff00;
			else
				color = 0xff0000;
			if(Yiffcraft.minecraft.currentScreen instanceof GuiChat)
			{
				fontrenderer.drawStringWithShadow("Lagging: " + curLag + "ms", 2, scaledheight - 24, color);
			}
			else
			{
				fontrenderer.drawStringWithShadow("Lagging: " + curLag + "ms", 2, scaledheight - 12, color);
			}
		}
		
		if(Yiffcraft.guiMode == 1 || Yiffcraft.minecraft.currentScreen instanceof GuiChat || Yiffcraft.minecraft.gameSettings.showDebugInfo) return;
		
		int k = 2;

		if(Speedhack.getSpeedMultiplier() != 1.0F)
			fontrenderer.drawStringWithShadow("Speed: " + ((int)Math.round(Speedhack.getSpeedMultiplier() * 100)) + "%", 2, k+=10, 0x00ff00);
		if(Yiffcraft.enableFullbright)
			fontrenderer.drawStringWithShadow("Fullbright", 2, k+=10, 0x00ff00);
		if(Yiffcraft.enableWallhack)
			fontrenderer.drawStringWithShadow("Wallhack", 2, k+=10, 0x00ff00);
		if(Yiffcraft.enableFly && Yiffcraft.enableOutOfBody)
			fontrenderer.drawStringWithShadow((Yiffcraft.flymodeSpecial ? "NoClip: 3D" : "NoClip: Planar"), 2, k+=10, 0x00ff00);
		else
		{
			if(Yiffcraft.enableFly)	
				fontrenderer.drawStringWithShadow((Yiffcraft.flymodeSpecial ? "Fly: 3D" : "Fly: Planar"), 2, k+=10, 0x00ff00);
			if(Yiffcraft.enableOutOfBody)
				fontrenderer.drawStringWithShadow("OutOfBody", 2, k+=10, 0x00ff00);
		}
		if(Yiffcraft.enableUnpushablePlayer)
			fontrenderer.drawStringWithShadow("Unpushable", 2, k+=10, 0x00ff00);
		if(Yiffcraft.aura.smash.enable)
			fontrenderer.drawStringWithShadow("Smash", 2, k+=10, 0x00ff00);
		if(Yiffcraft.aura.kill.enableKillMobs)
			fontrenderer.drawStringWithShadow("KillMobs", 2, k+=10, 0x00ff00);
		if(Yiffcraft.aura.kill.enableKillPlayers)
			fontrenderer.drawStringWithShadow("KillPlayers", 2, k+=10, 0x00ff00);
		if(Yiffcraft.aura.kill.enableKillAnimals)
			fontrenderer.drawStringWithShadow("KillAnimals", 2, k+=10, 0x00ff00);
		if(Yiffcraft.enableAutoRun)
			fontrenderer.drawStringWithShadow("AutoRun", 2, k+=10, 0x00ff00);
		if(Yiffcraft.stepHeight != 0.5F)
			fontrenderer.drawStringWithShadow("Step: " + (Yiffcraft.stepHeight), 2, k+=10, 0x00ff00);
		if(Yiffcraft.enableWaterwalk)
			fontrenderer.drawStringWithShadow("Waterwalk", 2, k+=10, 0x00ff00);
		if(Yiffcraft.enableOutOfBody)
		{
			double x = (Yiffcraft.realLoc.posX - Yiffcraft.minecraft.thePlayer.posX);
			double z = (Yiffcraft.realLoc.posZ - Yiffcraft.minecraft.thePlayer.posZ);
			int dist = (int)Math.round(Math.sqrt((x*x) + (z*z)));
			
			String s = "BodyDist: " + dist;
			int color2 = 0x00ff00;
			if(dist > 100) color2 = 0xff0000;
			else if(dist > 60) color2 = 0xffff00;
			fontrenderer.drawStringWithShadow(s, 2, k+=10, color2);
		}
		if(AutoBreak.enabled)
		{
			String blockInfo = "";
			if(AutoBreak.breakingBlock)
				blockInfo = "(" + AutoBreak.blockDamage + "/1.0)";
			fontrenderer.drawStringWithShadow("AutoBreak: (" + AutoBreak.blocks.size() + ") " + blockInfo, 2, k+=10, 0x00ff00);
		}
	}
}
