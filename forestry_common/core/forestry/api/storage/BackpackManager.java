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
package forestry.api.storage;

import java.util.ArrayList;
import java.util.HashMap;


import net.minecraft.item.ItemStack;

public class BackpackManager {
	/**
	 * 0 - Miner's Backpack 1 - Digger's Backpack 2 - Forester's Backpack 3 - Hunter's Backpack 4 - Adventurer's Backpack
	 * 
	 * Use IMC messages to achieve the same effect!
	 */
	public static ArrayList<ItemStack>[] backpackItems;
	
	public static IBackpackInterface backpackInterface;
	
	/**
	 * Only use this if you know what you are doing. Prefer backpackInterface. 
	 */
	public static HashMap<String, IBackpackDefinition> definitions = new HashMap<String, IBackpackDefinition>();
}
