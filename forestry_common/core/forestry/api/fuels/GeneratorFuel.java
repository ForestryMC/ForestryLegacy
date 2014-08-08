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
package forestry.api.fuels;

import java.util.HashMap;

import net.minecraftforge.liquids.LiquidStack;

public class GeneratorFuel {

	public static HashMap<Integer, GeneratorFuel> fuels = new HashMap<Integer, GeneratorFuel>();

	/**
	 * LiquidStack representing the fuel type and amount consumed per triggered cycle.
	 */
	public final LiquidStack fuelConsumed;
	/**
	 * EU emitted per tick while this fuel is being consumed in the generator (i.e. biofuel = 32, biomass = 8).
	 */
	public final int eu;
	/**
	 * Rate at which the fuel is consumed. 1 - Every tick 2 - Every second tick 3 - Every third tick etc.
	 */
	public final int rate;

	public GeneratorFuel(LiquidStack fuelConsumed, int eu, int rate) {
		this.fuelConsumed = fuelConsumed;
		this.eu = eu;
		this.rate = rate;
	}

}
