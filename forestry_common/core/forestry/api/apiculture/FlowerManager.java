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

import net.minecraft.item.ItemStack;

public class FlowerManager {
	/**
	 * ItemStacks representing simple flower blocks. Meta-sensitive, processed by the basic {@link IFlowerProvider}.
	 */
	public static ArrayList<ItemStack> plainFlowers = new ArrayList<ItemStack>();
}
