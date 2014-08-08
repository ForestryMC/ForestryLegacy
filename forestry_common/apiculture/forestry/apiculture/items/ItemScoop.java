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

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import forestry.api.core.Tabs;
import forestry.core.items.ItemForestry;

public class ItemScoop extends ItemForestry {

	private float efficiencyOnProperMaterial;

	public ItemScoop(int i) {
		super(i);
		this.maxStackSize = 1;
		efficiencyOnProperMaterial = 4.0F;
		setMaxDamage(10);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public float getStrVsBlock(ItemStack itemstack, Block block) {
		return 1.0F;
	}

	@Override
	public float getStrVsBlock(ItemStack itemstack, Block block, int md) {
		if (ForgeHooks.isToolEffective(itemstack, block, md))
			return efficiencyOnProperMaterial;

		return getStrVsBlock(itemstack, block);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemstack, World world, int i, int j, int k, int l, EntityLiving entityliving) {
		itemstack.damageItem(1, entityliving);
		return true;
	}

	@Override
	public int getDamageVsEntity(Entity entity) {
		return 1;
	}

	public boolean isFull3D() {
		return true;
	}

}
