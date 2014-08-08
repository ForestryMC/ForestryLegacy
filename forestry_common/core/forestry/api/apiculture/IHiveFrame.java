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

import net.minecraft.item.ItemStack;

public interface IHiveFrame extends IBeeModifier {

	/**
	 * Wears out a frame.
	 * 
	 * @param housing
	 *            IBeeHousing the frame is contained in.
	 * @param frame
	 *            ItemStack containing the actual frame.
	 * @param queen
	 *            Current queen in the caller.
	 * @param wear
	 *            Integer denoting the amount worn out. {@link IBeekeepingMode.getWearModifier()} has already been taken into account.
	 * @return ItemStack containing the actual frame with adjusted damage.
	 */
	ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear);

}
