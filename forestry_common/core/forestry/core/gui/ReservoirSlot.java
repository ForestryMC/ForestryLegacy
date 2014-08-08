/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gui;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import org.lwjgl.opengl.GL11;

import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.proxy.Proxies;

public class ReservoirSlot extends LiquidTankSlot {

	public ReservoirSlot(GfxSlotManager manager, int xPos, int yPos, ILiquidTankContainer tile, int slot) {
		super(manager, xPos, yPos, tile, slot);
		this.height = 16;
	}

	@Override
	public void draw(int startX, int startY) {

		Object content = null;
		int liquidImgIndex = 0;
		int liquidId = tank.getLiquid().itemID;
		int liquidMeta = tank.getLiquid().itemMeta;

		int squaled = (tank.getLiquid().amount * height) / tank.getCapacity();
		if (squaled > height) {
			squaled = height;
		}

		if (liquidId <= 0)
			return;

		if (liquidId < Block.blocksList.length && Block.blocksList[liquidId] != null) {
			content = Block.blocksList[liquidId];
			liquidImgIndex = Block.blocksList[liquidId].blockIndexInTexture;
			Proxies.common.bindTexture(((Block) content).getTextureFile());
		} else {
			content = Item.itemsList[liquidId];
			liquidImgIndex = Item.itemsList[liquidId].getIconFromDamage(liquidMeta);
			Proxies.common.bindTexture(((Item) content).getTextureFile());
		}

		int imgLine = liquidImgIndex / 16;
		int imgColumn = liquidImgIndex - imgLine * 16;

		int start = 0;

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
		while (true) {
			int x = 0;

			if (squaled > 16) {
				x = 16;
				squaled -= 16;
			} else {
				x = squaled;
				squaled = 0;
			}

			manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos + height - x - start, imgColumn * 16, imgLine * 16, 16, 16 - (16 - x));
			start = start + 16;

			if (x == 0 || squaled == 0) {
				break;
			}
		}

		int tex = manager.minecraft.renderEngine.getTexture(manager.gui.textureFile);
		manager.minecraft.renderEngine.bindTexture(tex);

	}
}
