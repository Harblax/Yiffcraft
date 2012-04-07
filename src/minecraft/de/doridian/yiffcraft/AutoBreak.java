package de.doridian.yiffcraft;

import net.minecraft.src.*;

import java.util.ArrayList;

public class AutoBreak {
	
	public static ArrayList<int[]> blocks;
	public static boolean enabled = false;
	
	public static void reinit()
	{
		enabled = false;
		breakingBlock = false;
		blocks = new ArrayList<int[]>();
	}
	
	public static void addBlock(int x, int y, int z)
	{
		if(!enabled) return;
		int[] pos = new int[]{x, y, z};
		for(int i = 0;i < blocks.size();i++)
		{
			int rx = blocks.get(i)[0];
			int ry = blocks.get(i)[1];
			int rz = blocks.get(i)[2];
			if(x == rx && y == ry && z == rz)
				return;
		}
		blocks.add(pos);
	}
	
	public static void remBlock(int x, int y, int z)
	{
		for(int i = 0;i < blocks.size();i++)
		{
			int rx = blocks.get(i)[0];
			int ry = blocks.get(i)[1];
			int rz = blocks.get(i)[2];
			if(x == rx && y == ry && z == rz)
			{
				blocks.remove(i);
				break;
			}
		}
	}
	
	public static void processBreakList()
	{
		if(!enabled || Yiffcraft.minecraft.theWorld == null || Yiffcraft.minecraft.thePlayer == null) return;
		for(int i = 0;i < blocks.size();i++)
		{
			int x = blocks.get(i)[0];
			int y = blocks.get(i)[1];
			int z = blocks.get(i)[2];
			if(!isInRange(x,y,z))
			{
				remBlock(x,y,z);
			}
			else
			{
				if(!breakingBlock)
				{
					remBlock(x,y,z);
					startBreakBlock(x,y,z);
				}
			}
		}
		processBreakBlock();
	}
	
	private static boolean isInRange(int x, int y, int z)
	{
		EntityPlayerSP player = Yiffcraft.minecraft.thePlayer;
		double dx = player.posX - ((double)x + 0.5D);
		double dy = player.posY - ((double)y + 0.5D);
		double dz = player.posZ - ((double)z + 0.5D);
		double d = dx * dx + dy * dy + dz * dz;
		if(d > 36D)
			return false;
		return true;
	}
	
	public static void activateStrongest(int x, int y, int z)
	{
		int id = Yiffcraft.minecraft.theWorld.getBlockId(x,y,z);
		if(id == 0) return;
		EntityPlayerSP ply = Yiffcraft.minecraft.thePlayer;
		
		float maxDmg = 1; float dmg = 0;
		int newItem = -1;
		for(int i=9;i<45;i++) {
			ItemStack stack = ply.inventorySlots.getSlot(i).getStack();
			if(stack == null) continue;
			dmg = stack.getStrVsBlock(Block.blocksList[id]);
			if(dmg > maxDmg) {
				newItem = i;
				maxDmg = dmg;
			}
		}
		
		if(newItem == -1 || newItem == 9) return;
		
		Yiffcraft.minecraft.playerController.windowClick(0,9,0,false,Yiffcraft.minecraft.thePlayer);
		Yiffcraft.minecraft.playerController.windowClick(0,newItem,0,false,Yiffcraft.minecraft.thePlayer);
		Yiffcraft.minecraft.playerController.windowClick(0,9,0,false,Yiffcraft.minecraft.thePlayer);
	}
	
	public static boolean breakingBlock = false;
	public static int blockX;
	public static int blockY;
	public static int blockZ;
	public static float blockDamage;
	public static void startBreakBlock(int x, int y, int z)
	{
		if(breakingBlock) return;
		activateStrongest(x, y, z);
		Yiffcraft.minecraft.thePlayer.inventory.currentItem = 9;
		Yiffcraft.SendPacket(new Packet16BlockItemSwitch(9));
		breakingBlock = true;
		blockDamage = 0.0F;
		blockX = x; blockY = y; blockZ = z;
	}
	
	public static void cancelBreakBlock()
	{
		breakingBlock = false;
	}
	
	public static void processBreakBlock()
	{
		if(breakingBlock)
		{
			int id = Yiffcraft.minecraft.theWorld.getBlockId(blockX, blockY, blockZ);
			if(id == 0)
			{
				cancelBreakBlock();
				return;
			}
			if(blockDamage == 0.0F)
			{
				if(isInRange(blockX,blockY,blockZ))
					Yiffcraft.SendPacket(new Packet14BlockDig(0, blockX, blockY, blockZ, 1));
				else
				{
					cancelBreakBlock();
					return;
				}
			}
			float damageToApply = Block.blocksList[id].blockStrength(Yiffcraft.minecraft.thePlayer);
			if(damageToApply <= 0.0F)
			{
				cancelBreakBlock();
				return;
			}
			blockDamage += damageToApply;
			if(blockDamage >= 1.0F)
			{
				if(isInRange(blockX,blockY,blockZ))
					Yiffcraft.SendPacket(new Packet14BlockDig(2, blockX, blockY, blockZ, 1));
				cancelBreakBlock();
				return;
			}
		}
	}
}
