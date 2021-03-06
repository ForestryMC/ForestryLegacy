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
package forestry.api.genetics;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLLog;

public class AlleleManager {

	public static IAlleleRegistry alleleRegistry;

	/**
	 * Translates plain leaf blocks into genetic data. Used by bees to convert and pollinate foreign leaf blocks.
	 */
	public static HashMap<ItemStack, IIndividual> ersatzSpecimen = new HashMap<ItemStack, IIndividual>();
	/**
	 * Translates plain saplings into genetic data. Used by the treealyzer and the farm to convert foreign saplings.
	 */
	public static HashMap<ItemStack, IIndividual> ersatzSaplings = new HashMap<ItemStack, IIndividual>();
	
	/**
	 * @deprecated
	 * Use IAlleleRegistry.getAllele instead!
	 */
	@Deprecated
	public static IAllele getAllele(String ident) {
		IAllele allele = null;

		try {

			String alleleClass = "forestry.core.genetics.Allele";

			Object obj = Class.forName(alleleClass).getField(ident).get(null);
			if (obj instanceof IAllele) {
				allele = (IAllele) obj;
			}
		} catch (Exception ex) {
			FMLLog.warning("Could not retrieve bee allele identified by: " + ident);
		}

		return allele;
	}

}
