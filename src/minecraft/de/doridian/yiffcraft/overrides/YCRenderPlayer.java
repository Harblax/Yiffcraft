package de.doridian.yiffcraft.overrides;

import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.src.*;

public class YCRenderPlayer extends RenderPlayer {
	public static YCRenderPlayer instance;

	protected Entity renderPlayerAs;
	private Render renderProcessor;
	protected EntityLiving renderPlayerAsLiving;

	private static float thirdPersonDistanceDefault = 0.0F;

	public double yOffset;
	public float yawOffset;
	public boolean reverseYaw;

	public Entity getRenderAs() {
		return renderPlayerAs;
	}

	public void setRenderAs(Entity otherEnt) {
		if(thirdPersonDistanceDefault <= 0.0F) {
			thirdPersonDistanceDefault = Yiffcraft.minecraft.entityRenderer.thirdPersonDistance;
		}
		if(otherEnt == null) {
			renderProcessor = null;
		} else {
			renderProcessor = RenderManager.instance.getEntityRenderObject(otherEnt);
		}
		if(renderProcessor == null) {
			renderPlayerAs = null;
			renderPlayerAsLiving = null;
			Yiffcraft.minecraft.entityRenderer.thirdPersonDistance = thirdPersonDistanceDefault;
		} else {
			renderPlayerAs = otherEnt;
			if(otherEnt instanceof EntityLiving) {
				renderPlayerAsLiving = (EntityLiving)renderPlayerAs;
				//renderPlayerAsLiving.isMultiplayerEntity = true;
				if(otherEnt instanceof EntityDragon) {
					Yiffcraft.minecraft.entityRenderer.thirdPersonDistance = 14.0F;
				} else if(otherEnt instanceof EntityGhast) {
					Yiffcraft.minecraft.entityRenderer.thirdPersonDistance = 12.0F;
				} else {
					Yiffcraft.minecraft.entityRenderer.thirdPersonDistance = thirdPersonDistanceDefault;
				}
				reverseYaw = false;
			} else {
				renderPlayerAsLiving = null;
				if(otherEnt instanceof EntityPainting) {
					reverseYaw = true;
				} else {
					reverseYaw = false;
				}
				Yiffcraft.minecraft.entityRenderer.thirdPersonDistance = thirdPersonDistanceDefault;
			}
		}
	}

	public void refreshVariablesStuff(EntityPlayer var1) {
		if(renderPlayerAsLiving != null) {
			renderPlayerAsLiving.field_705_Q = var1.field_705_Q;
			renderPlayerAsLiving.field_704_R = var1.field_704_R;
			renderPlayerAsLiving.field_703_S = var1.field_703_S;

			renderPlayerAsLiving.hurtTime = var1.hurtTime;
			renderPlayerAsLiving.attackedAtYaw = var1.attackedAtYaw;
			renderPlayerAsLiving.attackTime = var1.attackTime;

			renderPlayerAsLiving.prevRenderYawOffset = var1.prevRenderYawOffset;
			renderPlayerAsLiving.renderYawOffset = var1.renderYawOffset;
		}

		if(renderPlayerAs instanceof EntityWolf) {
			renderPlayerAs.setPositionAndRotation(var1.posX, var1.posY + var1.yOffset + yOffset, var1.posZ, var1.rotationPitch, var1.rotationYaw);
		} else {
			renderPlayerAs.setPositionAndRotation(var1.posX, (var1.posY - var1.yOffset) + yOffset, var1.posZ, var1.rotationPitch, var1.rotationYaw);
		}
		renderPlayerAs.noClip = true;

		renderPlayerAs.prevRotationPitch = var1.prevRotationPitch;
		renderPlayerAs.rotationPitch = var1.rotationPitch;
		renderPlayerAs.prevRotationYaw = var1.prevRotationYaw + yawOffset;
		renderPlayerAs.rotationYaw = var1.rotationYaw + yawOffset;

		if(reverseYaw) {
			renderPlayerAs.prevRotationYaw = -renderPlayerAs.prevRotationYaw;
			renderPlayerAs.rotationYaw = -renderPlayerAs.rotationYaw;
		}

		renderPlayerAs.fire = ((Entity)var1).fire;
	}
	
	public YCRenderPlayer() {
		super();
		instance = this;
	}

	@Override
	public void renderPlayer(EntityPlayer var1, double var2, double var4, double var6, float var8, float var9) {
		if(renderPlayerAs == null || renderProcessor == null) {
			super.renderPlayer(var1, var2, var4, var6, var8, var9);
			return;
		}

		refreshVariablesStuff(var1);

		var8 += yawOffset;
		if(reverseYaw) {
			var8 = -var8;
		}

		renderProcessor.doRender(renderPlayerAs, var2, (var4 - var1.yOffset) + yOffset, var6, var8, var9);
	}
}
