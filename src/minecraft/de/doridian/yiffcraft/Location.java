package de.doridian.yiffcraft;

import net.minecraft.src.Entity;

public class Location
{
	public double posX;
	public double posY;
	public double posZ;
	
	public float rotationYaw;
	public float rotationPitch;
	
	public String name;
	
	public Location clone()
	{
		return new Location(posX,posY,posZ,rotationYaw,rotationPitch,name);
	}
	
	public Location(Entity ent)
	{
		this(ent,"");
	}
	
	public Location(Entity ent, String s)
	{
		this(ent.posX,ent.posY,ent.posZ,ent.rotationYaw,ent.rotationPitch,s);
	}
	
	public Location()
	{
		this(0,0,0,0,0,"");
	}
	
	public Location(double x, double y, double z, String s)
	{
		this(x,y,z,0,0,s);
	}
	
	public Location(double x, double y, double z)
	{
		this(x,y,z,0,0,"");
	}
	
	public Location(double x, double y, double z, float yaw, float pitch)
	{
		this(x,y,z,yaw,pitch,"");
	}
	
	public Location(double x, double y, double z, float yaw, float pitch, String s)
	{
		posX = x; posY = y; posZ = z;
		rotationYaw = yaw; rotationPitch = pitch;
		name = s;
	}
	
	public double distance(Location pos)
	{
		return Math.sqrt(distanceSquare(pos));
	}
	
	public double distanceSquare(Location pos)
	{
		double x = (pos.posX - posX);
		double y = (pos.posY - posY);
		double z = (pos.posZ - posZ);
		return (x*x + y*y + z*z);
	}
	
	public double distance2D(Location pos)
	{
		return Math.sqrt(distance2DSquare(pos));
	}
	
	public double distance2DSquare(Location pos)
	{
		double x = (pos.posX - posX);
		double z = (pos.posZ - posZ);
		return (x*x + z*z);
	}
	
	public double distanceY(Location pos)
	{
		return (pos.posY - posY);
	}
	
	public Location(String line) throws Exception
	{
		String[] split = line.split(";",6);
		if(split.length != 6) {
			throw new Exception("Invalid line!");
		}
		
		name = split[5];
		
		posX = Double.parseDouble(split[0]);
		posY = Double.parseDouble(split[1]);
		posZ = Double.parseDouble(split[2]);
		
		rotationYaw = Float.parseFloat(split[3]);
		rotationPitch = Float.parseFloat(split[4]);
	}
	
	public String export()
	{
		return posX+";"+posY+";"+posZ+";"+rotationYaw+";"+rotationPitch+";"+name;
	}
}