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

import net.minecraft.block.Block;
import net.minecraft.world.World;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IEffectData;
import forestry.core.utils.Vect;

public class AlleleEffectGlacial extends AlleleEffectThrottled {

	public AlleleEffectGlacial(String uid) {
		super(uid, "glacial", false, 200, false);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorld();

		if (isThrottled(storedData))
			return storedData;

		if (EnumTemperature.hellishBiomeIds.contains(housing.getBiomeId()))
			return storedData;
		if (EnumTemperature.hotBiomeIds.contains(housing.getBiomeId()))
			return storedData;
		if (EnumTemperature.warmBiomeIds.contains(housing.getBiomeId()))
			return storedData;

		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));

		for (int i = 0; i < 10; i++) {

			Vect randomPos = new Vect(world.rand.nextInt(area.x), world.rand.nextInt(area.y), world.rand.nextInt(area.z));

			Vect posBlock = randomPos.add(new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord()));
			posBlock = posBlock.add(offset);

			// Freeze water
			int blockid = world.getBlockId(posBlock.x, posBlock.y, posBlock.z);
			if (blockid != Block.waterStill.blockID) {
				continue;
			}

			if (!world.isAirBlock(posBlock.x, posBlock.y + 1, posBlock.z)) {
				continue;
			}

			world.setBlockWithNotify(posBlock.x, posBlock.y, posBlock.z, Block.ice.blockID);
		}

		return storedData;
	}

}
