package de.doridian.yiffcraft.rendering;

import de.doridian.yiffcraft.Location;
import de.doridian.yiffcraft.Target;
import net.minecraft.src.Entity;
import net.minecraft.src.Tessellator;

public class Leash {
	private Entity entity;
	private Target target;
	private Catenary catenary;
	private double length;
	private int numpoints;

	public Leash(Entity ent, Target targ, double len) {
		length = len;
		numpoints = (int)(length * 5D);
		System.out.println(numpoints);
		entity = ent;
		target = targ;
		catenary = new Catenary(len);
		update();
	}
	
	private Location[] points = null;
	private int imax;
	public void update() {
		catenary.setBothNodes(new Location(entity), target.getPos());
		points = catenary.getPoints(numpoints);
		if(points == null) imax = 0;
		else imax = points.length;
	}
	
	public void render(Tessellator tessellator, float f) {
		/*if(true) return;
		update();
		if(points == null) return;
		
		EntityLiving entityliving = Yiffcraft.minecraft.renderViewEntity;
	
		double d = entityliving.lastTickPosX + (entityliving.posX - entityliving.lastTickPosX) * (double)f;
		double d1 = entityliving.lastTickPosY + (entityliving.posY - entityliving.lastTickPosY) * (double)f;
		double d2 = entityliving.lastTickPosZ + (entityliving.posZ - entityliving.lastTickPosZ) * (double)f;
		
		GL11.glBindTexture(3553 /*GL_TEXTURE_2D*//*,0);
		GL11.glLineWidth(1F);
		
		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		//tessellator.startDrawing(GL11.GL_LINE_STRIP);
		tessellator.setTranslationD(-d * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
		
		Location tmp;
		for(int i=0;i<imax;i++) {
			tmp = points[i];
			tessellator.addVertex(tmp.posX,tmp.posY,tmp.posZ);
		}
		
		tessellator.setTranslationD(0.0D, 0.0D, 0.0D);
		tessellator.draw();*/
	}
}