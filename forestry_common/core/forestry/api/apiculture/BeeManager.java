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
package forestry.api.apiculture;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import forestry.api.genetics.IMutation;

public class BeeManager {

	/**
	 * See {@link IBeeInterface} for details
	 */
	public static IBeeInterface beeInterface;

	/**
	 * Species templates for bees that can drop from hives.
	 * 
	 * 0 - Forest 1 - Meadows 2 - Desert 3 - Jungle 4 - End 5 - Snow 6 - Swamp
	 * 
	 * see {@link IMutation} for template format
	 */
	public static ArrayList<IHiveDrop>[] hiveDrops;

	/**
	 * 0 - Common Village Bees 1 - Uncommon Village Bees (20 % of spawns)
	 */
	public static ArrayList<IBeeGenome>[] villageBees;

	/**
	 * Access to Forestry's breeding manager for breeding information.
	 */
	public static IBreedingManager breedingManager;

	/**
	 * List of items that can induce swarming. Integer denotes x in 1000 chance.
	 */
	public static HashMap<ItemStack, Integer> inducers = new HashMap<ItemStack, Integer>();
}
