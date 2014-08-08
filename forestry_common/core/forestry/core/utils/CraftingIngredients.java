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

import forestry.core.gadgets.MachinePackage;

/**
 * A simple wrapper to store the information required by the CraftingManager to create a new recipe in {@link PlanterPackage} or {@link MachinePackage}
 */
public class CraftingIngredients {
	public final int stackSize;
	public final Object aobj[];

	public CraftingIngredients(int stackSize, Object aobj[]) {
		this.stackSize = stackSize;
		this.aobj = aobj;
	}
}
