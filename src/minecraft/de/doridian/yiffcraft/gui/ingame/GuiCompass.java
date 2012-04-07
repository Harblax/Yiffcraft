package de.doridian.yiffcraft.gui.ingame;

import de.doridian.yiffcraft.Location;
import de.doridian.yiffcraft.PlayerData;
import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.ScaledResolution;

public class GuiCompass extends IGuiIngame {
	@Override
	public void renderGui(ScaledResolution res, Minecraft minecraft, FontRenderer fontrenderer) {
		if(Yiffcraft.compassMode == 4) return;
		int width = res.getScaledWidth();
		int height = res.getScaledHeight();
		int x1 = (int)(width * 0.25);
		int x2 = (int)(width * 0.75);
		int y1 = 10;
		int y2 = 30;
		
		if(Yiffcraft.compass3D) {
			int w = (x2 - x1) / 2;
			int h = height / 2;
			y1 = h - w;
			y2 = h + w; 
		}
		
		if(Yiffcraft.compassMode == 3)
			drawRect(x1, y1, x2, y2, 0xaa333333);
		else
			drawRect(x1, y1, x2, y2 + 10, 0xaa333333);
		
		if(Yiffcraft.compass3D) { 
			y1 = (int)(res.getScaledHeight() / 2);
			y2 = y1;
		} else {
			y2++;
		}
		
		int cwidth = (x2 - x1) / 2;
		int cx = (x1 + x2) / 2;
		
		double localYaw = normalizeRotation(minecraft.thePlayer.cameraYaw + minecraft.thePlayer.rotationYaw + 180);
		double localPitch = normalizeRotation(minecraft.thePlayer.cameraPitch + minecraft.thePlayer.rotationPitch + 180);
		
		drawCompassText("W", 0, localYaw, localPitch, cx, cwidth, y1, fontrenderer, 0xffffffff, 9999);
		drawCompassText("NW", 45, localYaw, localPitch, cx, cwidth, y1, fontrenderer, 0xffff7878, 9999);
		drawCompassText("N", 90, localYaw, localPitch, cx, cwidth, y1, fontrenderer, 0xffff0000, 9999);
		drawCompassText("NE", 135, localYaw, localPitch, cx, cwidth, y1, fontrenderer, 0xffff7878, 9999);
		drawCompassText("E", 180, localYaw, localPitch, cx, cwidth, y1, fontrenderer, 0xffffffff, 9999);
		drawCompassText("SE", 225, localYaw, localPitch, cx, cwidth, y1, fontrenderer, 0xff78ff78, 9999);
		drawCompassText("S", 270, localYaw, localPitch, cx, cwidth, y1, fontrenderer, 0xff00ff00, 9999);
		drawCompassText("SW", 315, localYaw, localPitch, cx, cwidth, y1, fontrenderer, 0xff78ff78, 9999);
		
		for(int i=0;i<360;i += 15) {
			drawCompassTick(i, localYaw, localPitch, cx, cwidth, y2, 10);
		}
		
		if(Yiffcraft.compassMode == 3) return;
		
		Location localpos = new Location(Yiffcraft.minecraft.thePlayer);
		localpos.posX = localpos.posX * 32D;
		localpos.posY = localpos.posY * 32D;
		localpos.posZ = localpos.posZ * 32D;
		
		for(PlayerData pdata : Radar.players.valueCollection()) {
			if(Yiffcraft.compassMode == 2)
				drawCompassPlayer(pdata.getDispName() + pdata.getExtendedInfo(1), normalizeRotation(pdata.yawToPlayer), localYaw, localPitch, cx, cwidth, y2, fontrenderer, 0xffffffff, pdata.pos.distance2D(localpos) / 32D, pdata.pos.distanceY(localpos) / 32D);
			else
				drawCompassPlayer(pdata.getDispName(), normalizeRotation(pdata.yawToPlayer), localYaw, localPitch, cx, cwidth, y2, fontrenderer, 0xffffffff, pdata.pos.distance2D(localpos) / 32D, pdata.pos.distanceY(localpos) / 32D);
		}
	}
	
	private double normalizeRotation(double rotation) {
		return rotation % 360;
	}
	
	private void drawCompassPlayer(String str, double yaw, double localYaw, double localPitch, int x, int width, int y, FontRenderer fontrenderer, int color, double distancexz, double distancey) {
		x = getCompassX(yaw, localYaw, x, width, distancexz);
		y = getCompassY(yaw, localYaw, localPitch, y, width, distancexz);
		if(y < 0) return;
		
		int yoff = 0;
		if(Yiffcraft.compass3D) {
			if(distancey > 64) distancey = 64; if(distancey < -64) distancey = -64;
			yoff = (int)((distancey / 64) * width);
			drawRect(x, y, x + 1, y + yoff, 0xaa666666);
		}
		
		int sw = fontrenderer.getStringWidth(str) / 2;
		fontrenderer.drawString(str, x - sw, y + yoff, color);
	}
	
	private void drawCompassText(String str, double yaw, double localYaw, double localPitch, int x, int width, int y, FontRenderer fontrenderer, int color, double distancexz) {
		x = getCompassX(yaw, localYaw, x, width, distancexz);
		y = getCompassY(yaw, localYaw, localPitch, y, width, distancexz);
		if(y < 0) return;
		int sw = fontrenderer.getStringWidth(str) / 2;
		fontrenderer.drawString(str, x - sw, y, color);
	}
	
	private void drawCompassTick(double yaw, double localYaw, double localPitch, int x, int width, int y, int tickheight) {
		x = getCompassX(yaw, localYaw, x, width, 9999);
		y = getCompassY(yaw, localYaw, localPitch, y, width, 9999);
		if(y < 0) return;
		drawRect(x, y - tickheight, x + 1, y, 0xaa666666);
	}
	
	private int getCompassX(double yaw, double localYaw, int x, int width, double distance){
		double yawDiff = yaw - localYaw;
		yawDiff = (yawDiff / 180D) * Math.PI;
		yawDiff = (width * Math.sin(yawDiff) * -1);
		if(Yiffcraft.compass3D && distance < 128) yawDiff *= (distance / 128);
		return x + (int)yawDiff;
	}
	
	private int getCompassY(double yaw, double localYaw, double localPitch, int y, int width, double distancexz) {
		double yawDiff = yaw - localYaw;
		yawDiff = (yawDiff / 180D) * Math.PI;
		yawDiff = Math.cos(yawDiff);
		if(!Yiffcraft.compass3D) {
			if(yawDiff > 0) return -1;
			else return y;
		}
		yawDiff = (width * -1 * yawDiff * Math.sin((localPitch / 180D) * Math.PI));
		if(distancexz < 128) yawDiff *= (distancexz / 128);
		return y + (int)yawDiff;
	}
}
