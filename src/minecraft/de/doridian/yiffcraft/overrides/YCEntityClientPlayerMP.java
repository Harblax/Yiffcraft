package de.doridian.yiffcraft.overrides;

import de.doridian.yiffcraft.PlayerData;
import de.doridian.yiffcraft.Yiffcraft;
import de.doridian.yiffcraft.gui.ingame.Radar;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public class YCEntityClientPlayerMP extends EntityClientPlayerMP {

	public YCEntityClientPlayerMP(Minecraft var1, World var2, Session var3, NetClientHandler var4) {
		super(var1, var2, var3, var4);
	}

	@Override
	public boolean handleLavaMovement() {
		if(Yiffcraft.enableWaterwalk) return false;
		return super.handleLavaMovement();
	}

	@Override
	public boolean handleWaterMovement() {
		if(Yiffcraft.enableWaterwalk) return false;
		return super.handleWaterMovement();
	}
	
	@Override
	public boolean canBePushed() {
		return !Yiffcraft.enableUnpushablePlayer;
	}

	public void setCamRoll(float roll) {
		if(Yiffcraft.minecraft.entityRenderer.camRoll == roll) return;
		
		while(roll > 180F)
			roll -= 360F;

		while(roll < -180F)
			roll += 360F;

		if(roll > 90F)
			roll = 90F;
		else if(roll < -90F)
			roll = -90F;

		Yiffcraft.minecraft.entityRenderer.camRoll = roll;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		Entity renderAs = YCRenderPlayer.instance.renderPlayerAs;
		if(renderAs != null) {
			YCRenderPlayer.instance.refreshVariablesStuff(this);
			if(YCRenderPlayer.instance.renderPlayerAsLiving != null && YCRenderPlayer.instance.renderPlayerAsLiving instanceof EntityDragon) {
				EntityDragon dragonLiving = (EntityDragon)YCRenderPlayer.instance.renderPlayerAsLiving;
				setCamRoll((float)(dragonLiving.func_40160_a(1, 0)[0] - dragonLiving.func_40160_a(10, 0)[0]));
			} else {
				setCamRoll(0.0F);
			}
			renderAs.onUpdate();
		}
	}
	
	@Override
	public void sendMotionUpdates() {
		this.capabilities.isFlying = false;
		this.capabilities.allowFlying = false;

		Radar.updateLocalPlayerPosition();

		if(Yiffcraft.enableOutOfBody) {
			this.noClip = Yiffcraft.enableFly;
			this.sendQueue.addToSendQueue(new Packet10Flying(Yiffcraft.realOnGround));
			return;
		}

		this.noClip = false;
		super.sendMotionUpdates();
	}
}
