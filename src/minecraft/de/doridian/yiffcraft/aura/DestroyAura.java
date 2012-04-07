package de.doridian.yiffcraft.aura;

import de.doridian.yiffcraft.Location;
import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.Packet7UseEntity;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

import java.util.List;

public class DestroyAura extends BaseAura {
	public boolean enableDestroyItems = false;
	public boolean enableDestroyAll = false;
	
	@Override
	public float getRange() {
		return 6F;
	}

	@Override
	public void run(Location localpos) {
		if((enableDestroyItems || enableDestroyAll)) {				
			@SuppressWarnings("rawtypes")
			List list = Yiffcraft.minecraft.theWorld.loadedEntityList;
			int imax = list.size();
			
			for(int i=0;i<imax;i++) {
				Entity ent = (Entity)list.get(i);
				if(ent == Yiffcraft.minecraft.thePlayer) continue;
				if(localpos.distance(new Location(ent)) > 6F) continue;
				if(		(enableDestroyItems	&& ent instanceof EntityItem) 
					||	(enableDestroyAll 	&& !(ent instanceof EntityLiving)) )
					Yiffcraft.SendPacket(new Packet7UseEntity(Yiffcraft.minecraft.thePlayer.entityId, ent.entityId, 1));
			}
		}
	}

	@Override
	public ReadableColor getColor() {
		return Color.RED;
	}
}