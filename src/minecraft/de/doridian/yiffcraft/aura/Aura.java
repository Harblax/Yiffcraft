package de.doridian.yiffcraft.aura;

import de.doridian.yiffcraft.Location;
import de.doridian.yiffcraft.Yiffcraft;

public class Aura extends Thread
{
	public SmashAura smash;
	public KillAura kill;
	public DestroyAura destroy;
	
	public Aura() {
		super();
		smash = new SmashAura();
		kill = new KillAura();
		destroy = new DestroyAura();
	}
	
	public void render(float f) {
		Location l = new Location(Yiffcraft.minecraft.thePlayer);
		kill.render(l, f);
		smash.render(l, f);
		destroy.render(l, f);
	}
	
	public void run()
	{
		while(true) {
			try {
				Thread.sleep(100);
			}
			catch(Exception e) { }
			if(Yiffcraft.minecraft.theWorld == null) continue;
			if(Yiffcraft.minecraft.thePlayer == null) continue;
		
			try {
				Location l = new Location(Yiffcraft.minecraft.thePlayer);
				kill.run(l);
				smash.run(l);
				destroy.run(l);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}