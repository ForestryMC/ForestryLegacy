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

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import forestry.core.circuits.ISolderingIron;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.interfaces.ISocketable;
import forestry.core.utils.StringUtil;

public class SocketSlot extends GfxSlot {

	ISocketable tile;
	int slot = 0;

	public SocketSlot(GfxSlotManager manager, int xPos, int yPos, ISocketable tile, int slot) {
		super(manager, xPos, yPos);
		this.tile = tile;
		this.slot = slot;
	}

	@Override
	public void draw(int startX, int startY) {
		ItemStack socketStack = tile.getSocket(slot);
		if (socketStack != null) {
			GuiForestry.itemRenderer.renderItemIntoGUI(manager.minecraft.fontRenderer, manager.minecraft.renderEngine, socketStack, startX + xPos, startY
					+ yPos);
		}
	}

	@Override
	protected String getTooltip(EntityPlayer player) {
		ItemStack stack = tile.getSocket(slot);
		if (stack != null)
			return (String) (stack.getTooltip(player, false).get(0));
		else
			return StringUtil.localize("gui.emptysocket");
	}

	@Override
	protected void addInformation(@SuppressWarnings("rawtypes") ArrayList list, EntityPlayer player) {
		if (tile.getSocket(slot) != null) {
			tile.getSocket(slot).getItem().addInformation(tile.getSocket(slot), player, list, false);
		}
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {

		ItemStack itemstack = manager.minecraft.thePlayer.inventory.getItemStack();
		if (itemstack == null)
			return;

		Item held = itemstack.getItem();

		// Insert chipsets
		if (held instanceof ItemCircuitBoard) {
			((ContainerSocketed) manager.gui.inventorySlots).handleChipsetClick(slot, manager.minecraft.thePlayer, itemstack);
		} else if (held instanceof ISolderingIron) {
			((ContainerSocketed) manager.gui.inventorySlots).handleSolderingIronClick(slot, manager.minecraft.thePlayer, itemstack);
		}
	}
}
