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
package forestry.core.genetics;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import forestry.api.genetics.IAlleleSpecies;

public abstract class ItemGE extends Item {

	protected ItemGE(int id) {
		super(id);
		// maxStackSize = 1;
		hasSubtypes = true;
	}

	protected abstract int getDefaultPrimaryColour();

	protected abstract int getDefaultSecondaryColour();

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over SMP.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	// Return true to enable color overlay
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 3;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {

		if (renderPass == 0)
			return getDefaultPrimaryColour();
		else if (renderPass == 1)
			return getDefaultSecondaryColour();
		else
			return 0xffffff;

	}

	public int getColourFromSpecies(IAlleleSpecies species, int renderPass) {
		if (renderPass == 0)
			return species.getPrimaryColor();
		else
			return species.getSecondaryColor();
	}

}
