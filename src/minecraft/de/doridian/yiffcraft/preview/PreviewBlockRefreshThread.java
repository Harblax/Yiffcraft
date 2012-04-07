package de.doridian.yiffcraft.preview;

import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.src.*;

import java.util.HashSet;

public class PreviewBlockRefreshThread extends Thread {
	ItemStack lastItem;

	private int lastX; private int lastY; private int lastZ;
	private int lastSide;

	protected boolean forceNextRefresh = false;

	protected long nextRefresh = 0;

	private HashSet<PreviewBlock> myPreviewBlocks = new HashSet<PreviewBlock>();

	private void removeMyPreviewBlocks() {
		worldProxy.getPreviewBlockWrite(null);
		worldProxy.removePreviewBlocks(myPreviewBlocks);
		worldProxy.dropPreviewBlockWrite();
		myPreviewBlocks.clear();
	}

	private YCWorldProxy worldProxy;

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(100);

				if(worldProxy == null || !worldProxy.isValid()) {
					worldProxy = YCWorldProxy.getWorldProxy(true, false, false);
					myPreviewBlocks.clear();
					if(worldProxy == null) {
						continue;
					}
				}

				MovingObjectPosition objectMouseOver = Yiffcraft.minecraft.objectMouseOver;
				if(objectMouseOver != null && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE) {
					if(lastX != objectMouseOver.blockX || lastY != objectMouseOver.blockY || lastZ != objectMouseOver.blockZ || lastSide != objectMouseOver.sideHit) {
						forceNextRefresh = true;
						lastX = objectMouseOver.blockX;
						lastY = objectMouseOver.blockY;
						lastZ = objectMouseOver.blockZ;
						lastSide = objectMouseOver.sideHit;
					}

					long curTime = System.currentTimeMillis();
					if(curTime >= nextRefresh) {
						forceNextRefresh = true;
					}

					EntityPlayer player = Yiffcraft.minecraft.thePlayer;
					if(player != null && Yiffcraft.minecraft.theWorld != null) {
						ItemStack currentItem = player.getCurrentEquippedItem();
						if(forceNextRefresh || currentItem == null || lastItem == null || currentItem.itemID != lastItem.itemID || currentItem.getItemDamage() != lastItem.getItemDamage()) {
							nextRefresh = curTime + 500;

							removeMyPreviewBlocks();

							worldProxy.getPreviewBlockWrite(myPreviewBlocks);

							forceNextRefresh = false;
							if(currentItem != null) {
								Item currentUsingItem = currentItem.getItem();

								if(!(currentUsingItem instanceof ItemTool || currentUsingItem instanceof ItemSword)) {
									int hitID = worldProxy.getBlockId(lastX, lastY, lastZ);
									if (hitID > 0 && Block.blocksList[hitID].blockActivated(worldProxy, lastX, lastY, lastZ, player)) {
										//Well, we hit a button....or something....
									} else {
										worldProxy.setCanWrite(true);
										int data = currentItem.getItemDamage();
										int count = currentItem.stackSize;

										boolean useSuccess = currentUsingItem.onItemUse(currentItem, Yiffcraft.minecraft.thePlayer, worldProxy, lastX, lastY, lastZ, lastSide);

										currentItem.setItemDamage(data);
										currentItem.stackSize = count;

										if(!useSuccess) {
											currentUsingItem.onItemRightClick(currentItem, worldProxy, player);
										}

										currentItem.setItemDamage(data);
										currentItem.stackSize = count;
										worldProxy.setCanWrite(false);
									}
								}

								lastItem = currentItem.copy();
							} else {
								lastItem = null;
							}

							worldProxy.dropPreviewBlockWrite();
						}
					}
				} else {
					removeMyPreviewBlocks();
				}
			} catch(Exception e) { e.printStackTrace(); }
		}
	}
}