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
package forestry.api.core;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLLog;

public class BlockInterface {
	/**
	 * Get yer blocks here!
	 * 
	 * @param ident
	 * @return
	 */
	public static ItemStack getBlock(String ident) {
		ItemStack item = null;

		try {
			String pack = ItemInterface.class.getPackage().getName();
			pack = pack.substring(0, pack.lastIndexOf('.'));
			String itemClass = pack.substring(0, pack.lastIndexOf('.')) + ".core.config.ForestryBlock";
			Object obj = Class.forName(itemClass).getField(ident).get(null);
			if (obj instanceof Block) {
				item = new ItemStack((Block) obj);
			} else if (obj instanceof ItemStack) {
				item = (ItemStack) obj;
			}
		} catch (Exception ex) {
			FMLLog.warning("Could not retrieve Forestry block identified by: " + ident);
		}

		return item;
	}

}
