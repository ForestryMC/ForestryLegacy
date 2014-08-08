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

import net.minecraft.item.Item;

public interface IBackpackInterface {
	
	/**
	 * Adds a backpack with the given id, definition and type, returning the item.
	 * @param itemID Item id to use.
	 * @param definition Definition of backpack behaviour.
	 * @param type Type of backpack. (T1 or T2 (= Woven)
	 * @return Created backpack item.
	 */
	Item addBackpack(int itemID, IBackpackDefinition definition, EnumBackpackType type);
}
