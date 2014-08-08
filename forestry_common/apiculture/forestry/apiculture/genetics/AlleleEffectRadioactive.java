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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.core.utils.Vect;

public class AlleleEffectRadioactive extends AlleleEffectThrottled {

	public AlleleEffectRadioactive(String uid) {
		super(uid, "radioactive", true, 200, true);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorld();

		if (isThrottled(storedData))
			return storedData;

		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0] * 2, areaAr[1] * 2, areaAr[2] * 2);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));

		// Radioactivity hurts players and mobs
		Vect min = new Vect(housing.getXCoord() + offset.x, housing.getYCoord() + offset.y, housing.getZCoord() + offset.z);
		Vect max = new Vect(housing.getXCoord() + offset.x + area.x, housing.getYCoord() + offset.y + area.y, housing.getZCoord() + offset.z + area.z);

		AxisAlignedBB hurtBox = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(min.x, min.y, min.z, max.x, max.y, max.z);
		
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

		Random rand = housing.getWorld().rand;
		// Radioactivity destroys environment
		for (int i = 0; i < 20; i++) {

			Vect randomPos = new Vect(rand.nextInt(area.x), rand.nextInt(area.y), rand.nextInt(area.z));

			Vect posBlock = randomPos.add(new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord()));
			posBlock = posBlock.add(offset);

			// Don't destroy ourself and blocks below us.
			if (posBlock.x == housing.getXCoord() && posBlock.z == housing.getZCoord() && posBlock.y <= housing.getYCoord()) {
				continue;
			}

			// Remove targeted block
			int id = world.getBlockId(posBlock.x, posBlock.y, posBlock.z);
			if (!(Block.blocksList[id] != null && Block.blocksList[id].getBlockHardness(world, posBlock.x, posBlock.y, posBlock.z) < 0)
					&& !world.isAirBlock(posBlock.x, posBlock.y, posBlock.z)) {

				if (world.getBlockTileEntity(posBlock.x, posBlock.y, posBlock.z) != null) {
					world.removeBlockTileEntity(posBlock.x, posBlock.y, posBlock.z);
				}
				world.setBlockAndMetadataWithNotify(posBlock.x, posBlock.y, posBlock.z, 0, 0);
				break;

			}
		}

		return storedData;
	}

}
