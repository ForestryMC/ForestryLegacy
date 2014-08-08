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
package forestry.storage;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.utils.StringUtil;

public class BackpackDefinition implements IBackpackDefinition {

	public final int idT1;
	public final int idT2;
	
	final String name;

	final int primaryColor; // - c03384
	final int secondaryColor;

	ArrayList<ItemStack> validItems = new ArrayList<ItemStack>();
	
	public BackpackDefinition(int idT1, int idT2, String name, int primaryColor) {
		this(idT1, idT2, name, primaryColor, 0xffffff);
	}

	public BackpackDefinition(int idT1, int idT2, String name, int primaryColor, int secondaryColor) {
		
		this.idT1 = idT1;
		this.idT2 = idT2;
		
		this.name = name;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
	}

	@Override
	public String getKey() {
		return name;
	}
	
	@Override
	public String getName() {
		return StringUtil.localize("storage.backpack." + name);
	}
	
	@Override
	public int getPrimaryColour() {
		return primaryColor;
	}
	
	@Override
	public int getSecondaryColour() {
		return secondaryColor;
	}
	
	public BackpackDefinition setValidItems(ArrayList<ItemStack> validItems) {
		this.validItems = validItems;
		return this;
	}
	
	@Override
	public void addValidItem(ItemStack validItem) {
		this.validItems.add(validItem);
	}
	
	@Override
	public ArrayList<ItemStack> getValidItems(EntityPlayer player) {
		return validItems;
	}

	@Override
	public boolean isValidItem(EntityPlayer player, ItemStack itemstack) {
		for (ItemStack stack : getValidItems(player)) {
			if (stack.getItemDamage() < 0) {
				if (stack.itemID == itemstack.itemID)
					return true;
			} else {
				if (stack.isItemEqual(itemstack))
					return true;
			}
		}

		return false;
	}

}
