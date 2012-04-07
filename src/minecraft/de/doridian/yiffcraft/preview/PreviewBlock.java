package de.doridian.yiffcraft.preview;

import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;
import org.spoutcraft.client.config.ConfigReader;

import java.lang.reflect.Field;

public class PreviewBlock {
	protected int x; protected int y; protected int z;
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	private int id; private int data;
	public boolean isValid = true;

	private final YCWorldProxy worldProxy;
	public PreviewBlock(YCWorldProxy worldProxy) {
		this.worldProxy = worldProxy;
	}

	private Block block = null;
	private TileEntity tileEntity = null;

	public void renderTileEntity(float f) {
		GL11.glPushMatrix();

		TileEntity tileEntity = this.tileEntity;
		if(tileEntity != null) {
			int var3 = worldProxy.getLightBrightnessForSkyBlocks(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 0);
			int var4 = var3 % 65536;
			int var5 = var3 / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var4 / 1.0F, (float)var5 / 1.0F);

			if(tileEntity instanceof TileEntityChest) {
				GL11.glColorMask(true, false, false, true);
			} else {
				GL11.glColorMask(true, true, true, true);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
			}

			TileEntitySpecialRenderer tileEntityRenderer = TileEntityRenderer.instance.getSpecialRendererForEntity(tileEntity);
			if (tileEntityRenderer != null) {
				tileEntityRenderer.renderTileEntityAt(tileEntity, (double) tileEntity.xCoord, (double) tileEntity.yCoord, (double) tileEntity.zCoord, f);
			}

			if(tileEntity instanceof TileEntityChest) {
				GL11.glColorMask(true, true, true, true);
			} else {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}

		GL11.glPopMatrix();
	}

	public void render(float f, RenderBlocks blockRenderer) {
		if(!isValid) return;

		Tessellator tessellator = Tessellator.instance;
		tessellator.opaqueAlpha = 128;

		Block block = this.block;
		if(block == null || this.id <= 0) {
			GL11.glColorMask(true, false, false, true);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			tessellator.setTranslation(0.0D, 0.0D, 0.0D);
			tessellator.startDrawingQuads();
			blockRenderer.renderBlockByRenderType(Block.stone, this.x, this.y, this.z);
			tessellator.draw();
			GL11.glColorMask(true, true, true, true);
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, Yiffcraft.minecraft.renderEngine.getTexture("/terrain.png"));
			tessellator.setTranslation(0.0D, 0.0D, 0.0D);
			tessellator.startDrawingQuads();
			blockRenderer.renderBlockByRenderType(block, this.x, this.y, this.z);
			tessellator.draw();
		}

		renderTileEntity(f);

		tessellator.opaqueAlpha = 255;
	}

	public boolean setPosition(int x, int y, int z) {
		if(x == this.x && y == this.y && z == this.z)
			return false;

		this.x = x; this.y = y; this.z = z;

		this.id = 0;
		this.data = 0;
		this.block = null;
		this.tileEntity = null;

		return true;
	}

	public void makeDirty() {
		worldProxy.previewBlockRenderDirty = true;
	}

	public void updateBlock() {
		if(this.id < 0 || (Yiffcraft.minecraft.theWorld.getBlockId(this.x, this.y, this.z) == this.id && (this.id == 0 || Yiffcraft.minecraft.theWorld.getBlockMetadata(this.x, this.y, this.z) == this.data))) {
			isValid = false;
			block = null;
			worldProxy.removePreviewBlock(this);
		} else if(this.id < 1) {
			block = null;
			tileEntity = null;
			isValid = true;
		} else {
			block = Block.blocksList[this.id];
			block.onBlockAdded(worldProxy, this.x, this.y, this.z);
			isValid = true;
		}
		makeDirty();
	}

	public void setTileEntity(TileEntity tileEntity) {
		tileEntity.xCoord = this.x;
		tileEntity.yCoord = this.y;
		tileEntity.zCoord = this.z;
		tileEntity.blockType = this.block;
		tileEntity.blockMetadata = this.data;
		tileEntity.worldObj = worldProxy;
		this.tileEntity = tileEntity;
		makeDirty();
	}

	public void setData(int data) {
		if(this.data == data) return;
		this.data = data;
		updateBlock();
	}

	public void setID(int id) {
		if(this.id == id) return;
		this.id = id;
		updateBlock();
	}

	public void setIDAndData(int id, int data) {
		if(this.id == id && this.data == data) return;
		this.id = id;
		this.data = data;
		updateBlock();
	}

	public Block getBlock() {
		return this.block;
	}

	public int getID() {
		return this.id;
	}

	public int getData() {
		return this.data;
	}

	public TileEntity getTileEntity() {
		return this.tileEntity;
	}

	@Override
	public String toString() {
		return x + "," + y + "," + z + " => " + id + ((block != null) ? "[" + block.translateBlockName() + "]" : "") + "," + data + " in " + worldProxy;
	}
}
