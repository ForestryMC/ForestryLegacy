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
package forestry.core;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.PowerProvider;

public class BioPowerProvider extends PowerProvider {

	@Override
	public void configure(int latency, int minEnergyReceived, int maxEnergyReceived, int minActivationEnergy, int maxStoredEnergy) {
		super.configure(latency, minEnergyReceived, maxEnergyReceived, minActivationEnergy, maxStoredEnergy);

		this.latency = 0;
	}

	@Override
	public float useEnergy(float min, float max, boolean doUse) {
		float used = super.useEnergy(min, max, doUse);

		return used;
	}

	@Override
	public void receiveEnergy(float quantity, ForgeDirection from) {

		super.receiveEnergy(quantity, from);
	}
}
