package de.doridian.yiffcraft.commands;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bags.BlockBag;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.regions.CuboidRegionSelector;
import com.sk89q.worldedit.regions.CylinderRegionSelector;
import com.sk89q.worldedit.regions.EllipsoidRegionSelector;
import com.sk89q.worldedit.regions.Polygonal2DRegionSelector;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import de.doridian.yiffcraft.Chat;
import de.doridian.yiffcraft.Yiffcraft;
import de.doridian.yiffcraft.preview.PreviewBlock;
import de.doridian.yiffcraft.preview.YCWorldProxy;
import net.minecraft.src.Packet250CustomPayload;
import org.spoutcraft.spoutcraftapi.Spoutcraft;
import wecui.render.points.PointCube;
import wecui.render.points.PointRectangle;
import wecui.render.region.BaseRegion;
import wecui.render.region.CuboidRegion;
import wecui.render.region.CylinderRegion;
import wecui.render.region.EllipsoidRegion;
import wecui.render.region.PolygonRegion;
import wecui.util.Vector2;
import wecui.util.Vector3;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashSet;
import java.util.List;

public class WorldEditCmd extends BaseCommand {
	private final HashSet<PreviewBlock> previewBlocks = new HashSet<PreviewBlock>();

	private WorldEdit worldEdit;
	private YCWorldProxyWEPlayer localPlayer;
	private YCWorldProxyWEWorld localWorld;

	private YCWorldProxy worldProxy;

	private final Object commandLock = new Object();

	private static WorldEditCmd instance;
	public static void loadSelectionFromCUI() {
		if(instance == null) return;
		instance._loadSelectionFromCUI();
	}

	private Vector _pointCubeToVector(PointCube point) {
		if(point == null) return null;
		Vector3 oldVector = point.getPoint();
		if(oldVector == null) return null;
		return new Vector(oldVector.getX(), oldVector.getY(), oldVector.getZ());
	}

	private Vector _pointRectangleToVector(PointRectangle point, int y) {
		if(point == null) return null;
		Vector2 oldVector = point.getPoint();
		if(oldVector == null) return null;
		return new Vector(oldVector.getX(), y, oldVector.getY());
	}

	private void _loadSelectionFromCUI() {
		if(!refreshInstances()) return;

		LocalSession localSession = worldEdit.getSession(localPlayer);
		BaseRegion region = Yiffcraft.wecui.getSelection();
		RegionSelector selector = localSession.getRegionSelector(localWorld);
		Vector newPoint;

		if(region instanceof CuboidRegion) {
			CuboidRegion cuboidRegion = (CuboidRegion)region;
			selector = new CuboidRegionSelector(localWorld);

			newPoint = _pointCubeToVector(cuboidRegion.firstPoint);
			if(newPoint != null) selector.selectPrimary(newPoint);

			newPoint = _pointCubeToVector(cuboidRegion.secondPoint);
			if(newPoint != null) selector.selectSecondary(newPoint);
		} else if(region instanceof CylinderRegion) {
			CylinderRegion cylinderRegion = (CylinderRegion)region;
			selector = new CylinderRegionSelector(localWorld);

			newPoint = _pointCubeToVector(cylinderRegion.center);
			if(newPoint != null) {
				newPoint.setY(cylinderRegion.minY);
				selector.selectPrimary(newPoint);
			}

			newPoint = _pointCubeToVector(cylinderRegion.center);
			if(newPoint != null) {
				newPoint.setY(cylinderRegion.maxY);
				newPoint.setX(newPoint.getX() + cylinderRegion.radX);
				newPoint.setZ(newPoint.getZ() + cylinderRegion.radZ);
				selector.selectSecondary(newPoint);
			}
		} else if(region instanceof PolygonRegion) {
			PolygonRegion polygonRegion = (PolygonRegion)region;
			selector = new Polygonal2DRegionSelector(localWorld);

			if(!polygonRegion.points.isEmpty()) {
				PointRectangle startPoint = polygonRegion.points.get(0);
				selector.selectPrimary(_pointRectangleToVector(startPoint, polygonRegion.min));

				if(polygonRegion.points.size() > 1) {
					List<PointRectangle> points = polygonRegion.points.subList(1, polygonRegion.points.size());
					for(PointRectangle point : points) {
						selector.selectSecondary(_pointRectangleToVector(point, polygonRegion.max));
					}
				}
			}
		} else if(region instanceof EllipsoidRegion) {
			EllipsoidRegion ellipsoidRegion = (EllipsoidRegion)region;
			selector = new EllipsoidRegionSelector(localWorld);

			Vector center = _pointCubeToVector(ellipsoidRegion.center);
			if(center != null) {
				selector.selectPrimary(center);
				if(ellipsoidRegion.radii != null) {
					Vector outer = new Vector(ellipsoidRegion.radii.getX(), ellipsoidRegion.radii.getY(), ellipsoidRegion.radii.getZ());
					outer.add(center);
					selector.selectSecondary(outer);
				}
			}
		}
		localSession.setRegionSelector(localWorld, selector);
	}

	public WorldEditCmd() {
		instance = this;
	}

	private boolean refreshInstances() {
		try {
			if(worldEdit == null) {
				worldEdit = Yiffcraft.wecui.getLocalPlugin().getPlugin();
				localWorld = new YCWorldProxyWEWorld();
				localPlayer = new YCWorldProxyWEPlayer(localWorld);
			}

			if(worldProxy == null || !worldProxy.isValid()) {
				worldProxy = YCWorldProxy.getWorldProxy(true, false, true);
				previewBlocks.clear();
				if(worldProxy == null) {
					return false;
				}
			}

			return true;
		} catch(Exception e) {
			return false;
		}
	}

	private void sendWECmd(byte[] cmd) {
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "WorldEdit";
		packet.data = cmd;
		packet.length = cmd.length;
		Yiffcraft.SendPacket(packet);
	}

	private final ByteArrayOutputStream packetByteOutput = new ByteArrayOutputStream();
	private final DataOutputStream packetFormattedOutput = new DataOutputStream(packetByteOutput);
	public void run(final String[] args) throws Exception {
		if(!refreshInstances()) return;

		final String subCommand = args[0].toLowerCase();

		if(subCommand.equals("commit")) {
			synchronized (commandLock) {
				packetByteOutput.reset();
				packetFormattedOutput.write('S');
				packetFormattedOutput.writeInt(previewBlocks.size());
				packetFormattedOutput.flush();
				sendWECmd(packetByteOutput.toByteArray());

				for(PreviewBlock previewBlock : previewBlocks) {
					if(Yiffcraft.minecraft.theWorld.isRemote) {
						packetByteOutput.reset();
						packetFormattedOutput.writeInt(previewBlock.getX());
						packetFormattedOutput.writeShort(previewBlock.getY());
						packetFormattedOutput.writeInt(previewBlock.getZ());
						packetFormattedOutput.writeShort(previewBlock.getID());
						packetFormattedOutput.writeShort(previewBlock.getData());
						packetFormattedOutput.flush();
						sendWECmd(packetByteOutput.toByteArray());
					} else {
						Yiffcraft.minecraft.theWorld.setBlockAndMetadataWithNotify(previewBlock.getX(), previewBlock.getY(), previewBlock.getZ(), previewBlock.getID(), previewBlock.getData());
					}
				}

				worldProxy.removePreviewBlocks(previewBlocks);
				
				Chat.addChat("Sent " + previewBlocks.size() + " blocks");
				previewBlocks.clear();
			}
			
		} else if(subCommand.equals("clear")) {
			new Thread() {
				public void run() {
					synchronized (commandLock) {
						worldProxy.removePreviewBlocks(previewBlocks);
						Chat.addChat("Cleared " + previewBlocks.size() + " blocks");
						previewBlocks.clear();
					}
				}
			}.start();
		} else if(subCommand.equals("refresh")) {
			Chat.addChat("Refreshing " + previewBlocks.size() + " blocks");
			refreshPreviewBlocks();
		} else {
			synchronized (commandLock) {
				worldProxy.getPreviewBlockWrite(previewBlocks);

				String cmd = args[0];
				String[] myArgs = new String[args.length];
				System.arraycopy(args, 0, myArgs, 0, args.length);

				try {
					int slashesAdded = 0;
					boolean commandHandled = false;
					do {
						if(worldEdit.handleCommand(localPlayer, myArgs)) {
							commandHandled = true;
							break;
						}
						cmd = "/" + cmd;
						myArgs[0] = cmd;
						slashesAdded++;
					} while(slashesAdded <= 2);
					if(!commandHandled) {
						Chat.addChat("Unknown WE command");
					}
				} catch(Throwable e) {
					Chat.addChat(e.getClass().getSimpleName() + " => " + e.getMessage());
					e.printStackTrace();
				}

				worldProxy.dropPreviewBlockWrite();

				refreshPreviewBlocks();
			}
		}
	}

	private void refreshPreviewBlocks() {
		new Thread() {
			public void run() {
				synchronized (commandLock) {
					for(PreviewBlock previewBlock : previewBlocks) {
						previewBlock.updateBlock();
					}
				}
			}
		}.start();
	}

	public String getHelp() {
		return "Loads WE schematic";
	}
	public String getUsage() {
		return "<subcommand> [parameters]";
	}

	private class YCWorldProxyWEPlayer extends LocalPlayer {
		private final YCWorldProxyWEWorld world;
		private final BlockBag myBag = new BlockBag() {
			@Override
			public void flushChanges() {

			}

			@Override
			public void addSourcePosition(WorldVector worldVector) {

			}

			@Override
			public void addSingleSourcePosition(WorldVector worldVector) {

			}
		};

		protected YCWorldProxyWEPlayer(YCWorldProxyWEWorld myWorld) {
			super(Yiffcraft.wecui.getLocalPlugin().getServerInterface());
			world = myWorld;
		}

		@Override
		public int getItemInHand() {
			return Yiffcraft.minecraft.thePlayer.getItemInUse().itemID;
		}

		@Override
		public String getName() {
			return Yiffcraft.minecraft.thePlayer.displayName;
		}

		@Override
		public WorldVector getPosition() {
			return new WorldVector(world, Yiffcraft.minecraft.thePlayer.posX, Yiffcraft.minecraft.thePlayer.posY, Yiffcraft.minecraft.thePlayer.posZ);
		}

		@Override
		public LocalWorld getWorld() {
			return world;
		}

		@Override
		public double getPitch() {
			return Yiffcraft.minecraft.thePlayer.rotationPitch;
		}

		@Override
		public double getYaw() {
			return Yiffcraft.minecraft.thePlayer.rotationYaw;
		}

		@Override
		public void giveItem(int i, int i1) {

		}

		@Override
		public void printRaw(String s) {
			System.out.println("[WE RAW] " + s);
		}

		@Override
		public void printDebug(String s) {
			print("[DEBUG] " + s);
		}

		@Override
		public void print(String s) {
			Chat.addChat("[WE] " + s);
		}

		@Override
		public void printError(String s) {
			print("[ERROR] " + s);
		}

		@Override
		public void setPosition(Vector vector, float v, float v1) {

		}

		@Override
		public String[] getGroups() {
			return new String[0];
		}

		@Override
		public BlockBag getInventoryBlockBag() {
			return myBag;
		}

		@Override
		public boolean hasPermission(String s) {
			return Spoutcraft.hasPermission(s);
		}
	}

	private class YCWorldProxyWEWorld extends LocalWorld {
		@Override
		public String getName() {
			return "YCWorldProxyWorld";
		}

		@Override
		public boolean setBlockType(Vector vector, int i) {
			worldProxy.setBlock(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), i);
			return true;
		}

		@Override
		public void setBlockData(Vector vector, int i) {
			worldProxy.setBlockMetadata(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), i);
		}

		@Override
		public int getBlockType(Vector vector) {
			return worldProxy.getBlockId(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
		}

		@Override
		public int getBlockData(Vector vector) {
			return worldProxy.getBlockMetadata(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
		}

		@Override
		public void setBlockDataFast(Vector vector, int i) {
			setBlockData(vector, i);
		}

		@Override
		public BiomeType getBiome(Vector2D vector2D) {
			return null;
		}

		@Override
		public void setBiome(Vector2D vector2D, BiomeType biomeType) {

		}

		@Override
		public int getBlockLightLevel(Vector vector) {
			return 15;
		}

		@Override
		public boolean regenerate(Region blockVectors, EditSession editSession) {
			return false;
		}

		@Override
		public boolean copyToWorld(Vector vector, BaseBlock baseBlock) {
			return false;
		}

		@Override
		public boolean copyFromWorld(Vector vector, BaseBlock baseBlock) {
			return false;
		}

		@Override
		public boolean clearContainerBlockContents(Vector vector) {
			return false;
		}

		@Override
		public void dropItem(Vector vector, BaseItemStack baseItemStack) {

		}

		@Override
		public int removeEntities(EntityType entityType, Vector vector, int i) {
			return 0;
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof YCWorldProxyWEWorld);
		}

		@Override
		public int hashCode() {
			return getName().hashCode();
		}
	}
}
