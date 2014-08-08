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
package forestry.core.items;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import forestry.core.proxy.Proxies;

public class ItemForestryTool extends ItemForestry {

	private ItemStack remnants;
	private float efficiencyOnProperMaterial;
	private Block[] blocksEffectiveAgainst;

	public ItemForestryTool(int i, Block[] blocksEffectiveAgainst, ItemStack remnants) {
		super(i);
		this.blocksEffectiveAgainst = blocksEffectiveAgainst;
		this.maxStackSize = 1;
		efficiencyOnProperMaterial = 6F;
		setMaxDamage(200);
		this.remnants = remnants;
	}

	@Override
	public float getStrVsBlock(ItemStack itemstack, Block block) {
		for (int i = 0; i < blocksEffectiveAgainst.length; i++)
			if (blocksEffectiveAgainst[i] == block)
				return efficiencyOnProperMaterial;
		return 1.0F;
	}

	@Override
	public float getStrVsBlock(ItemStack itemstack, Block block, int md) {
		if (ForgeHooks.isToolEffective(itemstack, block, md))
			return efficiencyOnProperMaterial;
		return getStrVsBlock(itemstack, block);
	}

	@ForgeSubscribe
	public void onDestroyCurrentItem(PlayerDestroyItemEvent event) {
		if (event.original == null || event.original.getItem() != this)
			return;

		if (Proxies.common.isSimulating(event.entityPlayer.worldObj)) {
			EntityItem entity = new EntityItem(event.entityPlayer.worldObj, event.entityPlayer.posX, event.entityPlayer.posY, event.entityPlayer.posZ,
					remnants.copy());
			event.entityPlayer.worldObj.spawnEntityInWorld(entity);
		}
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
