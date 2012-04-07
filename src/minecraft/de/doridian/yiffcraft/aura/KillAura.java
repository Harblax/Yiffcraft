package de.doridian.yiffcraft.aura;

import de.doridian.yiffcraft.Location;
import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.src.*;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

import java.util.List;

public class KillAura extends BaseAura {
	public boolean enableKillMobs = false;
	public boolean enableKillAnimals = false;
	public boolean enableKillPlayers = false;
	public boolean enableKillLiving = false;
	
	private boolean lastSet = false;
	private int lastSlot = 0;
	private void pullStrongest()
	{
		if(lastSet) return;
		lastSet = true;
		
		EntityPlayerSP ply = Yiffcraft.minecraft.thePlayer;
		InventoryPlayer inv = ply.inventory;
		
		lastSlot = inv.currentItem;
		
		int maxDmg = 1; int dmg = 0;
		int newItem = -1;
		for(int i=0;i<9;i++) {
			ItemStack stack = inv.mainInventory[i];
			if(stack == null) continue;
			dmg = stack.getDamageVsEntity(ply);
			if(dmg > maxDmg) {
				newItem = i;
				maxDmg = dmg;
			}
		}
		if(newItem < 0) return;
		
		Yiffcraft.SendPacket(new Packet16BlockItemSwitch(newItem));
	}
	private void switchBack()
	{
		if(!lastSet) return;
		lastSet = false;
		Yiffcraft.SendPacket(new Packet16BlockItemSwitch(lastSlot));
	}
	
	@Override
	public float getRange() {
		return 6F;
	}

	@Override
	public ReadableColor getColor() {
		return new Color(enableKillMobs ? 255 : 0, enableKillAnimals ? 255 : 0, enableKillPlayers ? 255 : 0);
	}

	@Override
	public void run(Location localpos) {
		if(enableKillMobs || enableKillAnimals || enableKillPlayers) {				
			@SuppressWarnings("rawtypes")
			List list = Yiffcraft.minecraft.theWorld.loadedEntityList;
			int imax = list.size();
			
			for(int i=0;i<imax;i++) {
				Entity ent = (Entity)list.get(i);
				if(ent == Yiffcraft.minecraft.thePlayer || !(ent instanceof EntityLiving)) continue;
				EntityLiving living = (EntityLiving)ent;
				if(localpos.distance(new Location(living)) > 6.5F) continue;
				
				if(		(enableKillMobs 	&& living instanceof EntityMob) 
					||	(enableKillAnimals 	&& living instanceof EntityAnimal)
					||	(enableKillPlayers 	&& living instanceof EntityPlayer) 
					||	(enableKillLiving	&& living instanceof EntityLiving) )
				{
					pullStrongest();
					Yiffcraft.SendPacket(new Packet7UseEntity(Yiffcraft.minecraft.thePlayer.entityId, living.entityId, 1));
				}
			}
			
			switchBack();
		}
	}
}
