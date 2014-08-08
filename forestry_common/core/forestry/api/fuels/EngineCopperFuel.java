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

import net.minecraft.item.ItemStack;

public class EngineCopperFuel {

	/**
	 * Item that is valid fuel for a peat-fired engine.
	 */
	public final ItemStack fuel;
	/**
	 * Power produced by this fuel per work cycle.
	 */
	public final int powerPerCycle;
	/**
	 * Amount of work cycles this item lasts before being consumed.
	 */
	public final int burnDuration;

	public EngineCopperFuel(ItemStack fuel, int powerPerCycle, int burnDuration) {
		this.fuel = fuel;
		this.powerPerCycle = powerPerCycle;
		this.burnDuration = burnDuration;
	}

}
