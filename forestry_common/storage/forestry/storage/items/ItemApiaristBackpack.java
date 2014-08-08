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
package forestry.storage.items;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import forestry.api.apiculture.BeeManager;
import forestry.api.core.ForestryAPI;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.network.GuiId;
import forestry.storage.BackpackDefinition;

public class ItemApiaristBackpack extends ItemBackpack {

	public static class BackpackDefinitionApiarist extends BackpackDefinition {

		public BackpackDefinitionApiarist(int idT1, int idT2, String name, int primaryColor) {
			super(idT1, idT2, name, primaryColor);
		}
		
		@Override
		public boolean isValidItem(EntityPlayer player, ItemStack stack) {
			return BeeManager.beeInterface.isBee(stack);
		}

	}
	
	public ItemApiaristBackpack(int i) {
		super(i, new BackpackDefinitionApiarist(Defaults.ID_ITEM_APIARIST_BACKPACK, 0, "apiarist", 0xc4923d), 0);
	}

	@Override
	public void openGui(EntityPlayer entityplayer, ItemStack itemstack) {
		entityplayer.openGui(ForestryAPI.instance, GuiId.ApiaristBackpackGUI.ordinal(), entityplayer.worldObj, (int) entityplayer.posX,
				(int) entityplayer.posY, (int) entityplayer.posZ);
	}

	@Override
	public boolean isBackpack(ItemStack stack) {
		if (stack == null)
			return false;

		return stack.itemID == ForestryItem.apiaristBackpack.itemID;
	}

	@Override
	public ArrayList<ItemStack> getValidItems(EntityPlayer player) {
		return null;
	}
}
