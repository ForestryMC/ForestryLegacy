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
package forestry.apiculture.genetics;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;

public class MutationReqRes extends Mutation {

	private ItemStack blockRequired;

	public MutationReqRes(IAllele allele0, IAllele allele1, IAllele[] template, int chance, ItemStack blockRequired) {
		super(allele0, allele1, template, chance);
		this.blockRequired = blockRequired;
	}

	@Override
	public int getChance(IBeeHousing housing, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		int chance = super.getChance(housing, allele0, allele1, genome0, genome1);

		// If we have no chance anyway, we don't need to check.
		if (chance <= 0)
			return 0;

		World world = housing.getWorld();
		if (blockRequired == null)
			return chance;

		int blockid = world.getBlockId(housing.getXCoord(), housing.getYCoord() - 1, housing.getZCoord());
		int meta = world.getBlockMetadata(housing.getXCoord(), housing.getYCoord() - 1, housing.getZCoord());
		if (blockid == blockRequired.itemID && meta == blockRequired.getItemDamage())
			return chance;
		else
			return 0;
	}
}
