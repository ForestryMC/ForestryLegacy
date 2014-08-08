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
package forestry.core.utils;

import forestry.core.GameMode;

public class EnergyConfiguration {
	public int latency;
	public int minEnergyReceived;
	public int maxEnergyReceived;
	public int minActivationEnergy;
	public int maxEnergy;
	public int powerLoss = 1;
	public int powerLossRegularity = 100;

	public EnergyConfiguration(int latency, int minEnergyReceived, int maxEnergyReceived, int minActivationEnergy, int maxEnergy) {
		this.latency = latency;
		this.minEnergyReceived = minEnergyReceived;
		this.maxEnergyReceived = Math.round(maxEnergyReceived * GameMode.getGameMode().getEnergyDemandModifier());
		this.minActivationEnergy = Math.round(minActivationEnergy * GameMode.getGameMode().getEnergyDemandModifier());
		this.maxEnergy = Math.round(maxEnergy * GameMode.getGameMode().getEnergyDemandModifier());
	}
}
