package de.doridian.yiffcraft.rendering.particles;

import de.doridian.yiffcraft.Location;
import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityFX;
import net.minecraft.src.PathEntity;
import net.minecraft.src.Vec3D;

public class PathTracer extends EntityFX
{
	private static final float MAXDIST = 512F;
	private static final float SPEED = -0.2F;
	private static final float SPEEDSQ = SPEED * SPEED;
	
	public static PathTracer addNew(Entity from, Entity to) {
		//return addNew(from, Yiffcraft.minecraft.theWorld.getPathToEntity(from,to,MAXDIST));
		return null;
	}
	
	public static PathTracer addNew(Entity from, Location to) {
		//return addNew(from, Yiffcraft.minecraft.theWorld.getEntityPathToXYZ(from,(int)(to.posX),(int)(to.posY),(int)(to.posZ),MAXDIST));
		return null;
	}
	
	private static PathTracer addNew(Entity from, PathEntity finder) {
		//if(finder == null) return null;
		PathTracer tracer = new PathTracer(from, finder);
		Yiffcraft.minecraft.effectRenderer.addEffect(tracer);
		return tracer;
	}
	
	private PathEntity finder;
	private Entity from;
	private double targetX = 0D;
	private double targetY = 0D;
	private double targetZ = 0D;
	
	public PathTracer(Entity fromx, PathEntity finderx) {
		super(Yiffcraft.minecraft.theWorld, fromx.posX, fromx.posY, fromx.posZ, fromx.posX, fromx.posY, fromx.posZ);
		
		particleRed = 1.0F;
		particleGreen = 1.0F;
		particleBlue = 1.0F;
		setSize(0.5F, 0.5F);
		particleScale = 0.5F;
		noClip = true;
		
		finder = finderx;
		from = fromx;
		
		targetX = posX; targetY = posY; targetZ = posZ;
		
		nextPoint();
	}
	
	private void nextPoint() {
		if(finder.isFinished()) {
			setDead();
			return;
		}
	
		setPosition(targetX,targetY,targetZ);
		
		//Vec3D point = finder.getPosition(from);
		//targetX = point.xCoord; targetY = point.yCoord + 1F; targetZ = point.zCoord;
		
		double diffX = posX - targetX; double diffY = posY - targetY; double diffZ = posZ - targetZ;
		double div = Math.sqrt((diffX * diffX) + (diffY * diffY) + (diffZ * diffZ));
		diffX /= div; diffY /= div; diffZ /= div;
		
		motionX = diffX * SPEED; motionY = diffY * SPEED; motionZ = diffZ * SPEED;
		
		finder.incrementPathIndex();
	}
	
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
	
		double diffX = Math.abs(posX - targetX);
		double diffY = Math.abs(posY - targetY);
		double diffZ = Math.abs(posZ - targetZ);
		
		if(((diffX * diffX) + (diffY * diffY) + (diffZ * diffZ)) <= SPEEDSQ) {
			nextPoint();
		}

		moveEntity(motionX, motionY, motionZ);
	}
}