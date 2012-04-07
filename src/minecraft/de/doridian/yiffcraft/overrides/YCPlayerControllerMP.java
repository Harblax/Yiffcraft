package de.doridian.yiffcraft.overrides;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.PlayerControllerMP;
import net.minecraft.src.World;

public class YCPlayerControllerMP extends PlayerControllerMP {
	private NetClientHandler netClient;
	
	public YCPlayerControllerMP(Minecraft var1, NetClientHandler var2) {
		super(var1, var2);
		netClient = var2;
	}

	@Override
	public EntityPlayer createPlayer(World var1) {
		return new YCEntityClientPlayerMP(this.mc, var1, this.mc.session, this.netClient);
	}
}
