package de.doridian.yiffcraft.commands;

import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.src.EntityPlayerSP;

import java.awt.*;

public class CubeShot extends BaseCommand {
	public void run(String[] args) {
		
		float yawBase = 0;
		try {
			yawBase = Float.parseFloat(args[0]);
		}
		catch(Exception e) { }
		
		int ssSize = 512;
		try {
			ssSize = Integer.parseInt(args[1]);
		}
		catch(Exception e) { }
		
		final float _yawBase = yawBase;
		final int _ssSize = ssSize;
		
		new Thread() {
			public void run() {
				Canvas canvas = Yiffcraft.minecraft.mcCanvas;
				boolean hide = Yiffcraft.minecraft.gameSettings.hideGUI;
				//float fov = Yiffcraft.minecraft.gameSettings.fovSetting;
				
				Yiffcraft.minecraft.gameSettings.hideGUI = true;
				//Yiffcraft.minecraft.gameSettings.fovSetting = 90;
				Yiffcraft.minecraft.mcCanvas = new Canvas();
				Yiffcraft.minecraft.mcCanvas.setBounds(0, 0, _ssSize, _ssSize);
				
				float rotInc = 90F;
				EntityPlayerSP ply = Yiffcraft.minecraft.thePlayer;
				
				ply.setPositionAndRotation(ply.posX, ply.posY, ply.posZ, _yawBase, 0);
				MakeScreenShot("panorama0");
				
				ply.setPositionAndRotation(ply.posX, ply.posY, ply.posZ, _yawBase + rotInc, 0);
				MakeScreenShot("panorama1");
				
				ply.setPositionAndRotation(ply.posX, ply.posY, ply.posZ, _yawBase + (rotInc * 2), 0);
				MakeScreenShot("panorama2");
				
				ply.setPositionAndRotation(ply.posX, ply.posY, ply.posZ, _yawBase + (rotInc * 3), 0);
				MakeScreenShot("panorama3");
				
				
				ply.setPositionAndRotation(ply.posX, ply.posY, ply.posZ, _yawBase, -rotInc);
				MakeScreenShot("panorama4");
				
				ply.setPositionAndRotation(ply.posX, ply.posY, ply.posZ, _yawBase, rotInc);
				MakeScreenShot("panorama5");
				
				Yiffcraft.minecraft.gameSettings.hideGUI = hide;
				Yiffcraft.minecraft.mcCanvas = canvas;
				//Yiffcraft.minecraft.gameSettings.fovSetting = fov;
			}
		}.start();
	}
	
	private void MakeScreenShot(String name) {
		/*while(Yiffcraft.minecraft.isTakingScreenshot || Yiffcraft.minecraft.forceTakeScreenshot != null) {
			try {
				Thread.sleep(1);
			} catch(InterruptedException e) { }
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) { }
		
		Yiffcraft.minecraft.forceTakeScreenshot = name + ".png";
		
		while(Yiffcraft.minecraft.isTakingScreenshot || Yiffcraft.minecraft.forceTakeScreenshot != null) {
			try {
				Thread.sleep(1);
			}
			catch(Exception e) { }
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) { }*/
	}
	
	
	public String getHelp() {
		return "Makes a Cubemap";
	}
	public String getUsage() {
		return "[Base Yaw] [Size]";
	}
}