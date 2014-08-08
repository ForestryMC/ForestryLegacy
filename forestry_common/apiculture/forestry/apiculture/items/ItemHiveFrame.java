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
package forestry.apiculture.items;

import net.minecraft.item.ItemStack;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IHiveFrame;
import forestry.api.core.Tabs;
import forestry.core.items.ItemForestry;

public class ItemHiveFrame extends ItemForestry implements IHiveFrame {

	public ItemHiveFrame(int id, int maxDamage) {
		super(id);
		setMaxStackSize(1);
		setMaxDamage(maxDamage);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear) {
		frame.setItemDamage(frame.getItemDamage() + wear);
		if (frame.getItemDamage() >= frame.getMaxDamage())
			return null;
		else
			return frame;
	}

	@Override
	public float getTerritoryModifier(IBeeGenome genome) {
		return 1.0f;
	}

	@Override
	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate) {
		return 1.0f;
	}

	@Override
	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate) {
		return 1.0f;
	}

	@Override
	public float getProductionModifier(IBeeGenome genome) {
		return 2.0f;
	}

	@Override
	public float getFloweringModifier(IBeeGenome genome) {
		return 1.0f;
	}

	@Override
	public boolean isSealed() {
		return false;
	}

	@Override
	public boolean isSelfLighted() {
		return false;
	}

	@Override
	public boolean isSunlightSimulated() {
		return false;
	}

	@Override
	public boolean isHellish() {
		return false;
	}

}
