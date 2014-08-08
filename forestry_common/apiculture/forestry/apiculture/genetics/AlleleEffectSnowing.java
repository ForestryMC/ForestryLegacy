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

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.proxy.Proxies;

public class AlleleEffectSnowing extends AlleleEffectThrottled {

	public AlleleEffectSnowing(String uid) {
		super(uid, "snowing", false, 20, true);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		return storedData;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		
		int[] area = getModifiedArea(genome, housing);

		Proxies.render.addSnowFX(housing.getWorld(), housing.getXCoord(), housing.getYCoord(), housing.getZCoord(), genome.getPrimaryAsBee().getPrimaryColor(),
				area[0], area[1], area[2]);

		return storedData;
	}

}
