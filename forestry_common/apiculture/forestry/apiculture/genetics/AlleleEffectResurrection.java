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
package forestry.apiculture.genetics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;

public class AlleleEffectResurrection extends AlleleEffectThrottled {

	private HashMap<ItemStack, Class<? extends EntityLiving>> resurrectables = new HashMap<ItemStack, Class<? extends EntityLiving>>();
	
	public AlleleEffectResurrection(String uid) {
		super(uid, "resurrection", true, 40, true);
		resurrectables.put(new ItemStack(Item.bone), EntitySkeleton.class);
		resurrectables.put(new ItemStack(Item.arrow), EntitySkeleton.class);
		resurrectables.put(new ItemStack(Item.gunpowder), EntityCreeper.class);
		resurrectables.put(new ItemStack(Item.enderPearl), EntityEnderman.class);
		resurrectables.put(new ItemStack(Item.silk), EntitySpider.class);
		resurrectables.put(new ItemStack(Item.spiderEye), EntitySpider.class);
		resurrectables.put(new ItemStack(Item.rottenFlesh), EntityZombie.class);
		resurrectables.put(new ItemStack(Item.blazeRod), EntityBlaze.class);
		resurrectables.put(new ItemStack(Item.ghastTear), EntityGhast.class);
		resurrectables.put(new ItemStack(Block.dragonEgg), EntityDragon.class);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (isThrottled(storedData))
			return storedData;

		AxisAlignedBB bounding = getBounding(genome, housing, 1.0f);
		List list = housing.getWorld().getEntitiesWithinAABB(EntityItem.class, bounding);
		
		for (Object obj : list) {
			EntityItem item = (EntityItem)obj;
			if(item.isDead)
				continue;
			
			ItemStack contained = item.getEntityItem();
			for(Map.Entry<ItemStack, Class<? extends EntityLiving>> entry : resurrectables.entrySet()) {
				if(StackUtils.isIdenticalItem(entry.getKey(), contained)) {
					Utils.spawnEntity(housing.getWorld(), entry.getValue(), item.posX, item.posY, item.posZ);
					contained.stackSize--;
					if(contained.stackSize <= 0)
						item.setDead();
					break;
				}
			}
		}
		
		return storedData;
	}

}
