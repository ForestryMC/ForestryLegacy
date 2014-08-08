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

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.items.ItemArmorApiarist;

public class AlleleEffectAggressive extends AlleleEffectThrottled {

	public AlleleEffectAggressive(String uid) {
		super(uid, "aggressive", true, 40, false);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		if (isThrottled(storedData))
			return storedData;

		AxisAlignedBB hurtBox = getBounding(genome, housing, 1.0f);
		List list = housing.getWorld().getEntitiesWithinAABB(EntityLiving.class, hurtBox);

		for (Object obj : list) {
			EntityLiving entity = (EntityLiving) obj;

			int damage = 4;

			// Players are not attacked if they wear a full set of apiarist's
			// armor.
			if (entity instanceof EntityPlayer) {
				int count = ItemArmorApiarist.wearsItems((EntityPlayer) entity);
				// Full set, no damage/effect
				if (count > 3) {
					continue;
				} else if (count > 2) {
					damage = 1;
				} else if (count > 1) {
					damage = 2;
				} else if (count > 0) {
					damage = 3;
				}
			}

			entity.attackEntityFrom(DamageSource.generic, damage);
		}

		return storedData;
	}

}
