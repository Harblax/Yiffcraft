package de.doridian.yiffcraft.aura;

import de.doridian.yiffcraft.Location;
import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.src.Block;
import net.minecraft.src.Packet14BlockDig;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

public class SmashAura extends BaseAura {
	private Location lastHitPos = new Location();
	private final int RANGE = 6;
	public boolean enable;
	
	public float getRange() {
		return RANGE;
	}
	
	public ReadableColor getColor() {
		return Color.PURPLE;
	}
	
	public void run(Location localpos) {
		if(!enable) return;
		
		if(lastHitPos.distance(localpos) > 4F) {
			lastHitPos = localpos.clone();
			int minX = (int)localpos.posX - RANGE;
			int minY = (int)localpos.posY - RANGE;
			int minZ = (int)localpos.posZ - RANGE;
			int maxX = (int)localpos.posX + RANGE;
			int maxY = (int)localpos.posY + RANGE;
			int maxZ = (int)localpos.posZ + RANGE;
			
			Block block; int x; int y; int z;
			for(x=minX;x<=maxX;x++) {
				for(y=minY;y<=maxY;y++) {
					for(z=minZ;z<=maxZ;z++) {
						if(localpos.distance(new Location(x,y,z)) > RANGE) continue;
						block = Block.blocksList[Yiffcraft.minecraft.theWorld.getBlockId(x,y,z)];
						if(block == null || block.blockHardness > 0.0F) continue;
						Yiffcraft.SendPacket(new Packet14BlockDig(0, x, y, z, 0));
					}
				}
			}
		}
	}
}
