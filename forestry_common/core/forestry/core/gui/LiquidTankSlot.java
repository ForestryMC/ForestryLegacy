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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.opengl.GL11;

import buildcraft.api.tools.IToolPipette;
import forestry.core.config.Config;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

/**
 * Slot for liquid tanks
 */
public class LiquidTankSlot extends GfxSlot {

	protected int overlayTexX = 176;
	protected int overlayTexY = 0;
	protected ILiquidTank tank;
	protected int slot = 0;

	public LiquidTankSlot(GfxSlotManager manager, int xPos, int yPos, ILiquidTankContainer tile, int slot) {
		this(manager, xPos, yPos, tile.getTanks(ForgeDirection.UNKNOWN)[slot], slot);
	}
	public LiquidTankSlot(GfxSlotManager manager, int xPos, int yPos, ILiquidTank tank, int slot) {
		super(manager, xPos, yPos);
		this.tank = tank;
		this.slot = slot;
		this.height = 58;
	}

	public LiquidTankSlot setOverlayOrigin(int x, int y) {
		overlayTexX = x;
		overlayTexY = y;
		return this;
	}
	
	@Override
	public void draw(int startX, int startY) {

		if(tank == null)
			return;
		
		Object content = null;
		int liquidImgIndex = 0;
		int liquidId = tank.getLiquid().itemID;
		int liquidMeta = tank.getLiquid().itemMeta;
		int squaled = (tank.getLiquid().amount * height) / tank.getCapacity();

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

		GL11.glDisable(GL11.GL_LIGHTING);
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

			manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos + 58 - x - start, imgColumn * 16, imgLine * 16, 16, 16 - (16 - x));
			start = start + 16;

			if (x == 0 || squaled == 0) {
				break;
			}
		}

		int tex = manager.minecraft.renderEngine.getTexture(manager.gui.textureFile);
		manager.minecraft.renderEngine.bindTexture(tex);
		manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, overlayTexX, overlayTexY, 16, 60);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public String getTooltip(EntityPlayer player) {
		LiquidStack liquidStack = tank.getLiquid();
		if (liquidStack != null && liquidStack.itemID > 0) {
			String tooltip = Item.itemsList[liquidStack.itemID].getItemDisplayName(liquidStack.asItemStack());
			if (Config.tooltipLiquidAmount)
				tooltip += " (" + tank.getLiquid().amount + ")";
			return tooltip;
		} else
			return StringUtil.localize("gui.empty");
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {

		ItemStack itemstack = manager.minecraft.thePlayer.inventory.getItemStack();
		if (itemstack == null)
			return;

		Item held = itemstack.getItem();
		if (held instanceof IToolPipette
				&& manager.gui.inventorySlots instanceof ContainerLiquidTanks) {
			((ContainerLiquidTanks) manager.gui.inventorySlots).handlePipetteClick(slot, manager.minecraft.thePlayer);
		}

	}
}
