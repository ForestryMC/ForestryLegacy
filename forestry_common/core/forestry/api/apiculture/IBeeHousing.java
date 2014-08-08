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

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IBeeHousing extends IBeeModifier, IBeeListener {

	int getXCoord();

	int getYCoord();

	int getZCoord();

	ItemStack getQueen();

	ItemStack getDrone();

	void setQueen(ItemStack itemstack);

	void setDrone(ItemStack itemstack);

	int getBiomeId();

	EnumTemperature getTemperature();

	EnumHumidity getHumidity();

	World getWorld();

	/**
	 * @return String containing the login of this housing's owner.
	 */
	String getOwnerName();

	void setErrorState(int state);

	int getErrorOrdinal();

	/**
	 * @return true if princesses and drones can (currently) mate in this housing to generate queens.
	 */
	boolean canBreed();

	/**
	 * Called by IBeekeepingLogic to add products to the housing's inventory.
	 * 
	 * @param product
	 *            ItemStack with the product to add.
	 * @param all
	 * @return Boolean indicating success or failure.
	 */
	boolean addProduct(ItemStack product, boolean all);

}
