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

public class FermenterFuel {
	/**
	 * Item that is a valid fuel for the fermenter (i.e. fertilizer).
	 */
	public final ItemStack item;
	/**
	 * How much is fermeted per work cycle, i.e. how much biomass is produced per cycle.
	 */
	public final int fermentPerCycle;
	/**
	 * Amount of work cycles a single item of this fuel lasts before expiring.
	 */
	public final int burnDuration;

	public FermenterFuel(ItemStack item, int fermentPerCycle, int burnDuration) {
		this.item = item;
		this.fermentPerCycle = fermentPerCycle;
		this.burnDuration = burnDuration;
	}
}
