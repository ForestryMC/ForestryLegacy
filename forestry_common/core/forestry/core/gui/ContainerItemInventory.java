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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemInventory;

public abstract class ContainerItemInventory extends ContainerForestry {

	protected ItemInventory inventory;

	public ContainerItemInventory(ItemInventory inventory) {
		super(inventory);
		this.inventory = inventory;
	}

	protected abstract boolean isAcceptedItem(EntityPlayer player, ItemStack stack);

	public void purgeBag(EntityPlayer player) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null) {
				continue;
			}

			if (isAcceptedItem(player, stack)) {
				continue;
			}

			Proxies.common.dropItemPlayer(player, stack);
			inventory.setInventorySlotContents(i, null);
		}
	}

	/*
	@Override
	public ItemStack slotClick(int slot, int button, int par3, EntityPlayer entityplayer) {

		if (!inventory.isItemInventory)
			return super.slotClick(slot, button, par3, entityplayer);

		// Clicked outside of gui
		if (slot == -999 && (button == 0 || button == 1)) {

			ItemStack mouseStack = entityplayer.inventory.getItemStack();

			if (mouseStack != null && mouseStack.getItem() instanceof IInventoriedItem) {
				forceClose(entityplayer, mouseStack);
			}
		}

		// Clicked on a slot
		if (slot != -999 && (button == 0 || button == 1)) {
			ItemStack clicked = ((Slot) inventorySlots.get(slot)).getStack();
			if (clicked != null && clicked.getItem() instanceof IInventoriedItem) {
				forceClose(entityplayer, clicked);
			}

			ItemStack parent = inventory.determineParentInInventory(entityplayer);
			if (parent != null) {
				inventory.onGuiSaved(entityplayer);
			}
		}

		return super.slotClick(slot, button, par3, entityplayer);
	}

	private void forceClose(EntityPlayer entityplayer, ItemStack candidate) {

		boolean isParentItem = false;
		NBTTagCompound nbttagcompound = candidate.getTagCompound();
		if (nbttagcompound != null) {
			isParentItem = inventory.matchesUID(nbttagcompound.getInteger("UID"));
		}

		if (isParentItem) {
			// Server
			if (Proxies.common.isSimulating(entityplayer.worldObj)) {
				inventory.onGuiSaved(entityplayer);
				Proxies.common.closeGUI(entityplayer);
			}
		}

	}
	*/

	@Override
	public void onCraftGuiClosed(EntityPlayer entityplayer) {

		if (!Proxies.common.isSimulating(entityplayer.worldObj))
			return;

		purgeBag(entityplayer);

		if (inventory.isItemInventory) {
			inventory.onGuiSaved(entityplayer);
		}

	}

}
