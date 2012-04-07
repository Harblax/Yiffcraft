package de.doridian.yiffcraft.preview;

import de.doridian.yiffcraft.Yiffcraft;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import wecui.obfuscation.Obfuscation;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class YCWorldProxy extends World {
	private static final PreviewBlockRefreshThread threadRefreshPreviewBlocks;
	static {
		allProxies = new HashSet<YCWorldProxy>();
		renderableProxies = new YCWorldProxy[0];
		threadRefreshPreviewBlocks = new PreviewBlockRefreshThread();
		threadRefreshPreviewBlocks.start();
	}
	public static void requirePreviewUpdate() {
		threadRefreshPreviewBlocks.forceNextRefresh = true;
	}

	private final boolean canRender;
	private final boolean canMakeEffects;

	private static final HashSet<YCWorldProxy> allProxies;
	private static YCWorldProxy[] renderableProxies;

	private boolean isValid = true;
	public boolean isValid() {
		return isValid;
	}

	private static void rebuildRenderableProxies() {
		int i = 0;
		HashSet<YCWorldProxy> renderableTmp = new HashSet<YCWorldProxy>();
		synchronized (allProxies) {
			for(YCWorldProxy currentProxy : allProxies) {
				if(currentProxy.canRender) {
					renderableTmp.add(currentProxy);
					++i;
				}
			}
		}
		YCWorldProxy[] renderableArrayTmp = new YCWorldProxy[i];
		i = 0;
		for(YCWorldProxy currentProxy : renderableTmp) {
			renderableArrayTmp[i++] = currentProxy;
		}
		renderableProxies = renderableArrayTmp;
	}

	public static YCWorldProxy getWorldProxy(boolean canRender, boolean canMakeEffects, boolean canWrite) {
		if(Yiffcraft.minecraft.theWorld == null) return null;
		YCWorldProxy newProxy;
		try {
			newProxy = new YCWorldProxy(canRender, canMakeEffects);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		newProxy.setCanWrite(canWrite);
		synchronized (allProxies) {
			allProxies.add(newProxy);
		}
		rebuildRenderableProxies();

		return newProxy;
	}

	public void dropWorldProxy() {
		synchronized (allProxies) {
			allProxies.remove(this);
		}
		isValid = false;
		rebuildRenderableProxies();
	}

	public static void dropAllWorldProxies() {
		synchronized (allProxies) {
			for(YCWorldProxy currentProxy : allProxies) {
				currentProxy.isValid = false;
			}
			allProxies.clear();
		}
		rebuildRenderableProxies();
	}

	public static void renderWorldProxies(float f) {
		for(YCWorldProxy currentProxy : renderableProxies) {
			currentProxy.renderAllPreviewBlocks(f);
		}
	}

	//START CUSTOM METHODS
	public void clearEverything() {
		this.previewBlockRenderPaused = false;
		this.previewBlockRenderDirty = false;
		this.previewBlocks = new TLongObjectHashMap<PreviewBlock>();
		this.previewBlockArray = new PreviewBlock[0];
		this.previewBlockRenderDirty = true;
	}

	private TLongObjectHashMap<PreviewBlock> previewBlocks;
	private PreviewBlock[] previewBlockArray;

	private HashSet<PreviewBlock> writePreviewBlocks = null;
	private final Object writePreviewBlocksLock = new Object();
	private boolean writePreviewBlocksLocked = false;

	public boolean previewBlockRenderPaused = false;
	public boolean previewBlockRenderDirty = false;
	protected boolean seeRealWorld = true;

	private void refreshPreviewBlockArray() {
		synchronized (previewBlocks) {
			int count = previewBlocks.size();
			PreviewBlock[] retArray = new PreviewBlock[count];
			if(count > 0) {
				previewBlocks.values(retArray);
			}
			previewBlockArray = retArray;
		}
	}

	public void getPreviewBlockWrite(HashSet<PreviewBlock> writeTo) {
		while(true) {
			synchronized (writePreviewBlocksLock) {
				if(!writePreviewBlocksLocked) {
					writePreviewBlocks = writeTo;
					previewBlockRenderPaused = true;
					writePreviewBlocksLocked = true;
					return;
				}
			}
			try {
				Thread.sleep(10);
			} catch(Exception e) { }
		}
	}

	public void dropPreviewBlockWrite() {
		synchronized (writePreviewBlocksLock) {
			previewBlockRenderPaused = false;
			writePreviewBlocks = null;
			writePreviewBlocksLocked = false;
		}
	}

	public void renderAllPreviewBlocks(float f) {
		if(renderList < 0) {
			renderList = GLAllocation.generateDisplayLists(1);
		}

		if(previewBlockRenderDirty && !previewBlockRenderPaused) {
			boolean canRender;
			synchronized (writePreviewBlocksLock) {
				if(writePreviewBlocksLocked) {
					canRender = false;
				} else {
					writePreviewBlocksLocked = true;
					canRender = true;
				}
			}

			if(canRender) {
				previewBlockRenderDirty = false;

				seeRealWorld = false;

				RenderBlocks blockRenderer = new RenderBlocks(this);
				blockRenderer.blockAccess = this;

				previewBlockRenderDirty = false;

				synchronized (previewBlockArray) {
					for(PreviewBlock previewBlock : previewBlockArray) {
						previewBlock.renderTileEntity(f);
					}
				}

				GL11.glNewList(renderList, GL11.GL_COMPILE);
				GL11.glColorMask(true, true, true, true);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				synchronized (previewBlockArray) {
					for(PreviewBlock previewBlock : previewBlockArray) {
						previewBlock.render(f, blockRenderer);
					}
				}
				GL11.glEndList();

				seeRealWorld = true;

				dropPreviewBlockWrite();
			}
		}

		Obfuscation obf = Yiffcraft.wecui.getObfuscation();
		GL11.glPushMatrix();
		GL11.glTranslated(-obf.getPlayerXGuess(f), -obf.getPlayerYGuess(f), -obf.getPlayerZGuess(f));
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glCallList(renderList);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}

	public void removePreviewBlocks(Collection<PreviewBlock> toRemove) {
		for(PreviewBlock previewBlock : toRemove) {
			removePreviewBlock(previewBlock);
		}
	}

	protected static long vectorKey(int x, int y, int z) {
		return (((long)x) & 0x1FFFFF) << 42 | (((long)z) & 0x1FFFFF) << 21 | ((long)y) & 0x1FFFFF;
	}


	public void addPreviewBlock(PreviewBlock block) {
		if(block == null || !block.isValid) return;

		synchronized (writePreviewBlocksLock) {
			if(writePreviewBlocks != null) {
				writePreviewBlocks.add(block);
			}
		}

		long key = vectorKey(block.x, block.y, block.z);
		PreviewBlock oldBlock;
		synchronized (previewBlocks) {
			oldBlock = previewBlocks.get(key);
		}
		if(oldBlock != null) {
			removePreviewBlock(oldBlock);
		}
		synchronized (previewBlocks) {
			previewBlocks.put(key, block);
		}
		refreshPreviewBlockArray();

		block.makeDirty();
	}

	private PreviewBlock getPreviewBlockAt(int x, int y, int z, boolean generate) {
		long key = vectorKey(x, y, z);

		PreviewBlock block;
		synchronized (previewBlocks) {
			block = previewBlocks.get(key);
		}
		if(block == null) {
			if(!generate || !canWrite) return null;
			block = new PreviewBlock(this);
			block.setPosition(x, y, z);
			synchronized (writePreviewBlocksLock) {
				if(writePreviewBlocks != null) {
					writePreviewBlocks.add(block);
				}
			}
			synchronized (previewBlocks) {
				previewBlocks.put(key, block);
			}
			refreshPreviewBlockArray();

			block.makeDirty();
		}

		return block;
	}

	public void removePreviewBlock(PreviewBlock block) {
		removePreviewBlock(block.x, block.y, block.z);
	}

	public void removePreviewBlock(int x, int y, int z) {
		long key = vectorKey(x, y, z);

		PreviewBlock block;
		synchronized (previewBlocks) {
			block = previewBlocks.get(key);
		}
		if(block != null) {
			synchronized (writePreviewBlocksLock) {
				if(writePreviewBlocks != null) {
					writePreviewBlocks.remove(block);
				}
			}

			block.makeDirty();
		}

		synchronized (previewBlocks) {
			previewBlocks.remove(key);
		}
		refreshPreviewBlockArray();
	}

	private boolean canWrite = false;
	public void setCanWrite(boolean canWrite) {
		this.canWrite = canWrite;
	}

	//END CUSTOM METHODS

	private int renderList = -1;

	private YCWorldProxy(boolean canRender, boolean canMakeEffects) {
		super(Yiffcraft.minecraft.theWorld, new WorldProvider() {
			@Override
			protected void generateLightBrightnessTable() {

			}

			@Override
			protected void registerWorldChunkManager() {

			}

			@Override
			public IChunkProvider getChunkProvider() {
				return Yiffcraft.minecraft.theWorld.worldProvider.getChunkProvider();
			}

			@Override
			public boolean canCoordinateBeSpawn(int var1, int var2) {
				return Yiffcraft.minecraft.theWorld.worldProvider.canCoordinateBeSpawn(var1, var2);
			}

			@Override
			public float calculateCelestialAngle(long var1, float var3) {
				return Yiffcraft.minecraft.theWorld.worldProvider.calculateCelestialAngle(var1, var3);
			}

			@Override
			public float[] calcSunriseSunsetColors(float var1, float var2) {
				return Yiffcraft.minecraft.theWorld.worldProvider.calcSunriseSunsetColors(var1, var2);
			}

			@Override
			public Vec3D getFogColor(float var1, float var2) {
				return Yiffcraft.minecraft.theWorld.worldProvider.getFogColor(var1, var2);
			}

			@Override
			public boolean canRespawnHere() {
				return Yiffcraft.minecraft.theWorld.worldProvider.canRespawnHere();
			}

			@Override
			public float getCloudHeight() {
				return Yiffcraft.minecraft.theWorld.worldProvider.getCloudHeight();
			}

			@Override
			public ChunkCoordinates getEntrancePortalLocation() {
				return Yiffcraft.minecraft.theWorld.worldProvider.getEntrancePortalLocation();
			}
		});

		this.canRender = canRender;
		this.canMakeEffects = canMakeEffects;
		this.worldProvider.isHellWorld = false;
		this.isRemote = true;
		clearEverything();
	}


	@Override
	public WorldChunkManager getWorldChunkManager() {
		return Yiffcraft.minecraft.theWorld.getWorldChunkManager();
	}

	@Override
	public ChunkCoordinates getEntrancePortalLocation() {
		return Yiffcraft.minecraft.theWorld.getEntrancePortalLocation();
	}

	@Override
	public void setSpawnLocation() {
		Yiffcraft.minecraft.theWorld.setSpawnLocation();
	}

	@Override
	public int getFirstUncoveredBlock(int var1, int var2) {
		return Yiffcraft.minecraft.theWorld.getFirstUncoveredBlock(var1, var2);
	}

	@Override
	public void spawnPlayerWithLoadedChunks(EntityPlayer var1) {
		Yiffcraft.minecraft.theWorld.spawnPlayerWithLoadedChunks(var1);
	}

	@Override
	public void saveWorld(boolean var1, IProgressUpdate var2) {
		Yiffcraft.minecraft.theWorld.saveWorld(var1, var2);
	}

	@Override
	public boolean quickSaveWorld(int var1) {
		return Yiffcraft.minecraft.theWorld.quickSaveWorld(var1);
	}

	@Override
	public boolean isAirBlock(int var1, int var2, int var3) {
		return Yiffcraft.minecraft.theWorld.isAirBlock(var1, var2, var3);
	}

	@Override
	public boolean blockExists(int var1, int var2, int var3) {
		return Yiffcraft.minecraft.theWorld.blockExists(var1, var2, var3);
	}

	@Override
	public boolean doChunksNearChunkExist(int var1, int var2, int var3, int var4) {
		return Yiffcraft.minecraft.theWorld.doChunksNearChunkExist(var1, var2, var3, var4);
	}

	@Override
	public boolean checkChunksExist(int var1, int var2, int var3, int var4, int var5, int var6) {
		return Yiffcraft.minecraft.theWorld.checkChunksExist(var1, var2, var3, var4, var5, var6);
	}

	@Override
	public Chunk getChunkFromBlockCoords(int var1, int var2) {
		return Yiffcraft.minecraft.theWorld.getChunkFromBlockCoords(var1, var2);
	}

	@Override
	public Chunk getChunkFromChunkCoords(int var1, int var2) {
		return Yiffcraft.minecraft.theWorld.getChunkFromChunkCoords(var1, var2);
	}

	@Override
	public boolean setBlockAndMetadata(int var1, int var2, int var3, int var4, int var5) {
		PreviewBlock previewBlock = getPreviewBlockAt(var1, var2, var3, true);
		if(previewBlock == null) return true;
		previewBlock.setIDAndData(var4, var5);
		return true;
	}

	@Override
	public boolean setBlock(int var1, int var2, int var3, int var4) {
		PreviewBlock previewBlock = getPreviewBlockAt(var1, var2, var3, true);
		if(previewBlock == null) return true;
		previewBlock.setID(var4);
		return true;
	}

	@Override
	public boolean setBlockMetadata(int var1, int var2, int var3, int var4) {
		PreviewBlock previewBlock = getPreviewBlockAt(var1, var2, var3, true);
		if(previewBlock == null) return true;
		previewBlock.setData(var4);
		return true;
	}

	@Override
	public TileEntity getBlockTileEntity(int var1, int var2, int var3) {
		PreviewBlock block = getPreviewBlockAt(var1, var2, var3, false);
		if(block != null) {
			TileEntity tileEntity = block.getTileEntity();
			if(tileEntity instanceof TileEntitySign) {
				return null;
			} else {
				return tileEntity;
			}
		}
		if(!seeRealWorld) return null;

		return Yiffcraft.minecraft.theWorld.getBlockTileEntity(var1, var2, var3);
	}

	@Override
	public void setBlockTileEntity(int var1, int var2, int var3, TileEntity var4) {
		PreviewBlock previewBlock = getPreviewBlockAt(var1, var2, var3, true);
		if(previewBlock == null) return;
		previewBlock.setTileEntity(var4);
	}

	@Override
	public void removeBlockTileEntity(int var1, int var2, int var3) {
		PreviewBlock previewBlock = getPreviewBlockAt(var1, var2, var3, true);
		if(previewBlock == null) return;
		previewBlock.setTileEntity(null);
	}

	@Override
	public void setBlockMetadataWithNotify(int var1, int var2, int var3, int var4) {
		setBlockMetadata(var1, var2, var3, var4);
	}

	@Override
	public boolean setBlockWithNotify(int var1, int var2, int var3, int var4) {
		return setBlock(var1, var2, var3, var4);
	}

	@Override
	public boolean setBlockAndMetadataWithNotify(int var1, int var2, int var3, int var4, int var5) {
		return setBlockAndMetadata(var1, var2, var3, var4, var5);
	}

	@Override
	public Material getBlockMaterial(int var1, int var2, int var3) {
		PreviewBlock block = getPreviewBlockAt(var1, var2, var3, false);
		if(block != null) {
			Block intBlock = block.getBlock();
			if(intBlock == null) {
				return Material.air;
			} else {
				return intBlock.blockMaterial;
			}
		}

		if(!seeRealWorld) return Material.air;
		return Yiffcraft.minecraft.theWorld.getBlockMaterial(var1, var2, var3);
	}

	@Override
	public int getBlockMetadata(int var1, int var2, int var3) {
		PreviewBlock block = getPreviewBlockAt(var1, var2, var3, false);
		if(block != null) {
			return block.getData();
		}

		if(!seeRealWorld) return 0;
		return Yiffcraft.minecraft.theWorld.getBlockMetadata(var1, var2, var3);
	}

	@Override
	public int getBlockId(int var1, int var2, int var3) {
		PreviewBlock block = getPreviewBlockAt(var1, var2, var3, false);
		if(block != null) {
			return block.getID();
		}

		if(!seeRealWorld) return 0;
		return Yiffcraft.minecraft.theWorld.getBlockId(var1, var2, var3);
	}

	@Override
	public void markBlockNeedsUpdate(int var1, int var2, int var3) {
		Yiffcraft.minecraft.theWorld.markBlockNeedsUpdate(var1, var2, var3);
	}

	@Override
	public void markBlocksDirtyVertical(int var1, int var2, int var3, int var4) {
		Yiffcraft.minecraft.theWorld.markBlocksDirtyVertical(var1, var2, var3, var4);
	}

	@Override
	public void markBlockAsNeedsUpdate(int var1, int var2, int var3) {
		Yiffcraft.minecraft.theWorld.markBlockAsNeedsUpdate(var1, var2, var3);
	}

	@Override
	public void markBlocksDirty(int var1, int var2, int var3, int var4, int var5, int var6) {
		Yiffcraft.minecraft.theWorld.markBlocksDirty(var1, var2, var3, var4, var5, var6);
	}

	@Override
	public void notifyBlocksOfNeighborChange(int var1, int var2, int var3, int var4) {
		Yiffcraft.minecraft.theWorld.notifyBlocksOfNeighborChange(var1, var2, var3, var4);
	}

	@Override
	public boolean canBlockSeeTheSky(int var1, int var2, int var3) {
		return Yiffcraft.minecraft.theWorld.canBlockSeeTheSky(var1, var2, var3);
	}

	@Override
	public int getFullBlockLightValue(int var1, int var2, int var3) {
		return Yiffcraft.minecraft.theWorld.getFullBlockLightValue(var1, var2, var3);
	}

	@Override
	public int getBlockLightValue(int var1, int var2, int var3) {
		return Yiffcraft.minecraft.theWorld.getBlockLightValue(var1, var2, var3);
	}

	@Override
	public int getBlockLightValue_do(int var1, int var2, int var3, boolean var4) {
		return Yiffcraft.minecraft.theWorld.getBlockLightValue_do(var1, var2, var3, var4);
	}

	@Override
	public int getHeightValue(int var1, int var2) {
		return Yiffcraft.minecraft.theWorld.getHeightValue(var1, var2);
	}

	@Override
	public int getSkyBlockTypeBrightness(EnumSkyBlock var1, int var2, int var3, int var4) {
		return Yiffcraft.minecraft.theWorld.getSkyBlockTypeBrightness(var1, var2, var3, var4);
	}

	@Override
	public int getSavedLightValue(EnumSkyBlock var1, int var2, int var3, int var4) {
		return Yiffcraft.minecraft.theWorld.getSavedLightValue(var1, var2, var3, var4);
	}

	@Override
	public void setLightValue(EnumSkyBlock var1, int var2, int var3, int var4, int var5) {
		Yiffcraft.minecraft.theWorld.setLightValue(var1, var2, var3, var4, var5);
	}

	@Override
	public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
		return Yiffcraft.minecraft.theWorld.getLightBrightnessForSkyBlocks(var1, var2, var3, var4);
	}

	@Override
	public float getBrightness(int var1, int var2, int var3, int var4) {
		return Yiffcraft.minecraft.theWorld.getBrightness(var1, var2, var3, var4);
	}

	@Override
	public float getLightBrightness(int var1, int var2, int var3) {
		return Yiffcraft.minecraft.theWorld.getLightBrightness(var1, var2, var3);
	}

	@Override
	public boolean isDaytime() {
		return Yiffcraft.minecraft.theWorld.isDaytime();
	}

	@Override
	public MovingObjectPosition rayTraceBlocks(Vec3D var1, Vec3D var2) {
		return Yiffcraft.minecraft.theWorld.rayTraceBlocks(var1, var2);
	}

	@Override
	public MovingObjectPosition rayTraceBlocks_do(Vec3D var1, Vec3D var2, boolean var3) {
		return Yiffcraft.minecraft.theWorld.rayTraceBlocks_do(var1, var2, var3);
	}

	@Override
	public MovingObjectPosition rayTraceBlocks_do_do(Vec3D var1, Vec3D var2, boolean var3, boolean var4) {
		return Yiffcraft.minecraft.theWorld.rayTraceBlocks_do_do(var1, var2, var3, var4);
	}

	@Override
	public void playSoundAtEntity(Entity var1, String var2, float var3, float var4) {
		if(!canMakeEffects) return;
		Yiffcraft.minecraft.theWorld.playSoundAtEntity(var1, var2, var3, var4);
	}

	@Override
	public void playSoundEffect(double var1, double var3, double var5, String var7, float var8, float var9) {
		if(!canMakeEffects) return;
		Yiffcraft.minecraft.theWorld.playSoundEffect(var1, var3, var5, var7, var8, var9);
	}

	@Override
	public void playRecord(String var1, int var2, int var3, int var4) {
		if(!canMakeEffects) return;
		Yiffcraft.minecraft.theWorld.playRecord(var1, var2, var3, var4);
	}

	@Override
	public void spawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12) {
		if(!canMakeEffects) return;
		if(Yiffcraft.minecraft.gameSettings.thirdPersonView == 0) return;
		Yiffcraft.minecraft.theWorld.spawnParticle(var1, var2, var4, var6, var8, var10, var12);
	}

	@Override
	public boolean addWeatherEffect(Entity var1) {
		return Yiffcraft.minecraft.theWorld.addWeatherEffect(var1);
	}

	@Override
	public boolean spawnEntityInWorld(Entity var1) {
		return super.spawnEntityInWorld(var1);
	}

	@Override
	public void obtainEntitySkin(Entity var1) {
		Yiffcraft.minecraft.theWorld.obtainEntitySkin(var1);
	}

	@Override
	public void releaseEntitySkin(Entity var1) {
		Yiffcraft.minecraft.theWorld.releaseEntitySkin(var1);
	}

	@Override
	public void setEntityDead(Entity var1) {
		Yiffcraft.minecraft.theWorld.setEntityDead(var1);
	}

	@Override
	public void addWorldAccess(IWorldAccess var1) {
		Yiffcraft.minecraft.theWorld.addWorldAccess(var1);
	}

	@Override
	public void removeWorldAccess(IWorldAccess var1) {
		Yiffcraft.minecraft.theWorld.removeWorldAccess(var1);
	}

	@Override
	public List getCollidingBoundingBoxes(Entity var1, AxisAlignedBB var2) {
		return Yiffcraft.minecraft.theWorld.getCollidingBoundingBoxes(var1, var2);
	}

	@Override
	public int calculateSkylightSubtracted(float var1) {
		return Yiffcraft.minecraft.theWorld.calculateSkylightSubtracted(var1);
	}

	@Override
	public float func_35464_b(float var1) {
		return Yiffcraft.minecraft.theWorld.func_35464_b(var1);
	}

	@Override
	public Vec3D getSkyColor(Entity var1, float var2) {
		return Yiffcraft.minecraft.theWorld.getSkyColor(var1, var2);
	}

	@Override
	public float getCelestialAngle(float var1) {
		return Yiffcraft.minecraft.theWorld.getCelestialAngle(var1);
	}
	
	@Override
	public float getCelestialAngleRadians(float var1) {
		return Yiffcraft.minecraft.theWorld.getCelestialAngleRadians(var1);
	}

	@Override
	public Vec3D drawClouds(float var1) {
		return Yiffcraft.minecraft.theWorld.drawClouds(var1);
	}

	@Override
	public Vec3D getFogColor(float var1) {
		return Yiffcraft.minecraft.theWorld.getFogColor(var1);
	}

	@Override
	public int getPrecipitationHeight(int var1, int var2) {
		return super.getPrecipitationHeight(var1, var2);
	}

	@Override
	public int getTopSolidOrLiquidBlock(int var1, int var2) {
		return Yiffcraft.minecraft.theWorld.getTopSolidOrLiquidBlock(var1, var2);
	}

	@Override
	public float getStarBrightness(float var1) {
		return Yiffcraft.minecraft.theWorld.getStarBrightness(var1);
	}

	@Override
	public void scheduleBlockUpdate(int var1, int var2, int var3, int var4, int var5) {
		Yiffcraft.minecraft.theWorld.scheduleBlockUpdate(var1, var2, var3, var4, var5);
	}

	@Override
	public void checkEntityTile(TileEntity tileentity) {
		Yiffcraft.minecraft.theWorld.checkEntityTile(tileentity);
	}

	@Override
	public void updateEntities() {
		Yiffcraft.minecraft.theWorld.updateEntities();
	}

	@Override
	public void addTileEntity(Collection var1) {
		Yiffcraft.minecraft.theWorld.addTileEntity(var1);
	}

	@Override
	public void updateEntity(Entity var1) {
		Yiffcraft.minecraft.theWorld.updateEntity(var1);
	}

	@Override
	public void updateEntityWithOptionalForce(Entity var1, boolean var2) {
		Yiffcraft.minecraft.theWorld.updateEntityWithOptionalForce(var1, var2);
	}

	@Override
	public boolean checkIfAABBIsClear(AxisAlignedBB var1) {
		return Yiffcraft.minecraft.theWorld.checkIfAABBIsClear(var1);
	}

	@Override
	public boolean isBoundingBoxBurning(AxisAlignedBB var1) {
		return Yiffcraft.minecraft.theWorld.isBoundingBoxBurning(var1);
	}

	@Override
	public boolean handleMaterialAcceleration(AxisAlignedBB var1, Material var2, Entity var3) {
		return Yiffcraft.minecraft.theWorld.handleMaterialAcceleration(var1, var2, var3);
	}

	@Override
	public boolean isMaterialInBB(AxisAlignedBB var1, Material var2) {
		return Yiffcraft.minecraft.theWorld.isMaterialInBB(var1, var2);
	}

	@Override
	public boolean isAABBInMaterial(AxisAlignedBB var1, Material var2) {
		return Yiffcraft.minecraft.theWorld.isAABBInMaterial(var1, var2);
	}

	@Override
	public Explosion createExplosion(Entity var1, double var2, double var4, double var6, float var8) {
		return Yiffcraft.minecraft.theWorld.createExplosion(var1, var2, var4, var6, var8);
	}

	@Override
	public Explosion newExplosion(Entity var1, double var2, double var4, double var6, float var8, boolean var9) {
		return Yiffcraft.minecraft.theWorld.newExplosion(var1, var2, var4, var6, var8, var9);
	}

	@Override
	public Entity func_4085_a(Class var1) {
		return Yiffcraft.minecraft.theWorld.func_4085_a(var1);
	}

	@Override
	public String getDebugLoadedEntities() {
		return Yiffcraft.minecraft.theWorld.getDebugLoadedEntities();
	}

	@Override
	public boolean isBlockOpaqueCube(int var1, int var2, int var3) {
		PreviewBlock block = getPreviewBlockAt(var1, var2, var3, false);
		if(block != null) {
			Block intBlock = block.getBlock();
			if(intBlock != null) {
				return block.getBlock().isOpaqueCube();
			} else {
				return false;
			}
		}

		if(!seeRealWorld) return false;
		return Yiffcraft.minecraft.theWorld.isBlockOpaqueCube(var1, var2, var3);
	}

	@Override
	public boolean isBlockNormalCube(int var1, int var2, int var3) {
		return Yiffcraft.minecraft.theWorld.isBlockNormalCube(var1, var2, var3);
	}

	@Override
	public void saveWorldIndirectly(IProgressUpdate var1) {
		Yiffcraft.minecraft.theWorld.saveWorldIndirectly(var1);
	}

	@Override
	public void calculateInitialSkylight() {
		Yiffcraft.minecraft.theWorld.calculateInitialSkylight();
	}

	@Override
	public void tick() {
		Yiffcraft.minecraft.theWorld.tick();
	}

	@Override
	protected void updateWeather() {
		super.updateWeather();
	}

	@Override
	public boolean isBlockHydrated(int var1, int var2, int var3, boolean var4) {
		return Yiffcraft.minecraft.theWorld.isBlockHydrated(var1, var2, var3, var4);
	}

	@Override
	public boolean canSnowAt(int var1, int var2, int var3) {
		return Yiffcraft.minecraft.theWorld.canSnowAt(var1, var2, var3);
	}

	@Override
	public void updateAllLightTypes(int var1, int var2, int var3) {
		Yiffcraft.minecraft.theWorld.updateAllLightTypes(var1, var2, var3);
	}

	@Override
	public void updateLightByType(EnumSkyBlock var1, int var2, int var3, int var4) {
		Yiffcraft.minecraft.theWorld.updateLightByType(var1, var2, var3, var4);
	}

	@Override
	public boolean tickUpdates(boolean var1) {
		return Yiffcraft.minecraft.theWorld.tickUpdates(var1);
	}

	@Override
	public void randomDisplayUpdates(int var1, int var2, int var3) {
		Yiffcraft.minecraft.theWorld.randomDisplayUpdates(var1, var2, var3);
	}

	@Override
	public List getEntitiesWithinAABBExcludingEntity(Entity var1, AxisAlignedBB var2) {
		return Yiffcraft.minecraft.theWorld.getEntitiesWithinAABBExcludingEntity(var1, var2);
	}

	@Override
	public List getEntitiesWithinAABB(Class var1, AxisAlignedBB var2) {
		return Yiffcraft.minecraft.theWorld.getEntitiesWithinAABB(var1, var2);
	}

	@Override
	public List getLoadedEntityList() {
		return Yiffcraft.minecraft.theWorld.getLoadedEntityList();
	}

	@Override
	public void updateTileEntityChunkAndDoNothing(int var1, int var2, int var3, TileEntity var4) {
		Yiffcraft.minecraft.theWorld.updateTileEntityChunkAndDoNothing(var1, var2, var3, var4);
	}

	@Override
	public int countEntities(Class var1) {
		return Yiffcraft.minecraft.theWorld.countEntities(var1);
	}

	@Override
	public void addLoadedEntities(List var1) {
		Yiffcraft.minecraft.theWorld.addLoadedEntities(var1);
	}

	@Override
	public void unloadEntities(List var1) {
		Yiffcraft.minecraft.theWorld.unloadEntities(var1);
	}

	@Override
	public void dropOldChunks() {
		Yiffcraft.minecraft.theWorld.dropOldChunks();
	}

	@Override
	public boolean canBlockBePlacedAt(int var1, int var2, int var3, int var4, boolean var5, int var6) {
		return Yiffcraft.minecraft.theWorld.canBlockBePlacedAt(var1, var2, var3, var4, var5, var6);
	}

	@Override
	public boolean isBlockProvidingPowerTo(int var1, int var2, int var3, int var4) {
		return Yiffcraft.minecraft.theWorld.isBlockProvidingPowerTo(var1, var2, var3, var4);
	}

	@Override
	public boolean isBlockGettingPowered(int var1, int var2, int var3) {
		return Yiffcraft.minecraft.theWorld.isBlockGettingPowered(var1, var2, var3);
	}

	@Override
	public boolean isBlockIndirectlyProvidingPowerTo(int var1, int var2, int var3, int var4) {
		return Yiffcraft.minecraft.theWorld.isBlockIndirectlyProvidingPowerTo(var1, var2, var3, var4);
	}

	@Override
	public boolean isBlockIndirectlyGettingPowered(int var1, int var2, int var3) {
		return Yiffcraft.minecraft.theWorld.isBlockIndirectlyGettingPowered(var1, var2, var3);
	}

	@Override
	public EntityPlayer getClosestPlayerToEntity(Entity var1, double var2) {
		return Yiffcraft.minecraft.theWorld.getClosestPlayerToEntity(var1, var2);
	}

	@Override
	public EntityPlayer getClosestPlayer(double var1, double var3, double var5, double var7) {
		return Yiffcraft.minecraft.theWorld.getClosestPlayer(var1, var3, var5, var7);
	}

	@Override
	public EntityPlayer getClosestVulnerablePlayerToEntity(Entity var1, double var2) {
		return Yiffcraft.minecraft.theWorld.getClosestVulnerablePlayerToEntity(var1, var2);
	}

	@Override
	public EntityPlayer getClosestVulnerablePlayer(double var1, double var3, double var5, double var7) {
		return Yiffcraft.minecraft.theWorld.getClosestVulnerablePlayer(var1, var3, var5, var7);
	}

	@Override
	public EntityPlayer getPlayerEntityByName(String var1) {
		return Yiffcraft.minecraft.theWorld.getPlayerEntityByName(var1);
	}

	@Override
	public void sendQuittingDisconnectingPacket() {
		Yiffcraft.minecraft.theWorld.sendQuittingDisconnectingPacket();
	}

	@Override
	public void checkSessionLock() {
		Yiffcraft.minecraft.theWorld.checkSessionLock();
	}

	@Override
	public void setWorldTime(long var1) {
		Yiffcraft.minecraft.theWorld.setWorldTime(var1);
	}

	@Override
	public long getWorldTime() {
		return Yiffcraft.minecraft.theWorld.getWorldTime();
	}

	@Override
	public ChunkCoordinates getSpawnPoint() {
		return Yiffcraft.minecraft.theWorld.getSpawnPoint();
	}

	@Override
	public void setSpawnPoint(ChunkCoordinates var1) {
		Yiffcraft.minecraft.theWorld.setSpawnPoint(var1);
	}

	@Override
	public void joinEntityInSurroundings(Entity var1) {
		Yiffcraft.minecraft.theWorld.joinEntityInSurroundings(var1);
	}

	@Override
	public boolean canMineBlock(EntityPlayer var1, int var2, int var3, int var4) {
		return Yiffcraft.minecraft.theWorld.canMineBlock(var1, var2, var3, var4);
	}

	@Override
	public void setEntityState(Entity var1, byte var2) {
		Yiffcraft.minecraft.theWorld.setEntityState(var1, var2);
	}

	@Override
	public void updateEntityList() {
		Yiffcraft.minecraft.theWorld.updateEntityList();
	}

	@Override
	public void playNoteAt(int var1, int var2, int var3, int var4, int var5) {
		Yiffcraft.minecraft.theWorld.playNoteAt(var1, var2, var3, var4, var5);
	}

	@Override
	public ISaveHandler getSaveHandler() {
		return Yiffcraft.minecraft.theWorld.getSaveHandler();
	}

	@Override
	public WorldInfo getWorldInfo() {
		return Yiffcraft.minecraft.theWorld.getWorldInfo();
	}

	@Override
	public void updateAllPlayersSleepingFlag() {
		Yiffcraft.minecraft.theWorld.updateAllPlayersSleepingFlag();
	}

	@Override
	public boolean isAllPlayersFullyAsleep() {
		return Yiffcraft.minecraft.theWorld.isAllPlayersFullyAsleep();
	}

	@Override
	public float getWeightedThunderStrength(float var1) {
		return Yiffcraft.minecraft.theWorld.getWeightedThunderStrength(var1);
	}

	@Override
	public float getRainStrength(float var1) {
		return Yiffcraft.minecraft.theWorld.getRainStrength(var1);
	}

	@Override
	public void setRainStrength(float var1) {
		Yiffcraft.minecraft.theWorld.setRainStrength(var1);
	}

	@Override
	public boolean isRaining() {
		return Yiffcraft.minecraft.theWorld.isRaining();
	}

	@Override
	public boolean canLightningStrikeAt(int var1, int var2, int var3) {
		return Yiffcraft.minecraft.theWorld.canLightningStrikeAt(var1, var2, var3);
	}

	@Override
	public void setItemData(String var1, WorldSavedData var2) {
		Yiffcraft.minecraft.theWorld.setItemData(var1, var2);
	}

	@Override
	public WorldSavedData loadItemData(Class var1, String var2) {
		return Yiffcraft.minecraft.theWorld.loadItemData(var1, var2);
	}

	@Override
	public int getUniqueDataId(String var1) {
		return Yiffcraft.minecraft.theWorld.getUniqueDataId(var1);
	}

	@Override
	public void playAuxSFX(int var1, int var2, int var3, int var4, int var5) {
		Yiffcraft.minecraft.theWorld.playAuxSFX(var1, var2, var3, var4, var5);
	}

	@Override
	public void playAuxSFXAtEntity(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6) {
		if(!canMakeEffects) return;
		Yiffcraft.minecraft.theWorld.playAuxSFXAtEntity(var1, var2, var3, var4, var5, var6);
	}

	@Override
	public Random setRandomSeed(int var1, int var2, int var3) {
		return Yiffcraft.minecraft.theWorld.setRandomSeed(var1, var2, var3);
	}

	@Override
	public boolean updatingLighting() {
		return Yiffcraft.minecraft.theWorld.updatingLighting();
	}

	@Override
	public void doColorfulStuff() {
		Yiffcraft.minecraft.theWorld.doColorfulStuff();
	}

	@Override
	public int getGrassColorCache(int x, int y, int z) {
		return Yiffcraft.minecraft.theWorld.getGrassColorCache(x, y, z);
	}

	@Override
	public void setGrassColorCache(int x, int y, int z, int color) {
		Yiffcraft.minecraft.theWorld.setGrassColorCache(x, y, z, color);
	}

	@Override
	public int getWaterColorCache(int x, int y, int z) {
		return Yiffcraft.minecraft.theWorld.getWaterColorCache(x, y, z);
	}

	@Override
	public void setWaterColorCache(int x, int y, int z, int color) {
		Yiffcraft.minecraft.theWorld.setWaterColorCache(x, y, z, color);
	}
}
