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

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IClassification;
import forestry.core.genetics.AlleleSpecies;

public class AlleleBeeSpecies extends AlleleSpecies implements IAlleleBeeSpecies {

	public IJubilanceProvider jubilanceProvider;

	private HashMap<ItemStack, Integer> products = new HashMap<ItemStack, Integer>();
	private HashMap<ItemStack, Integer> specialty = new HashMap<ItemStack, Integer>();

	public AlleleBeeSpecies(String uid, boolean dominant, String name, IClassification branch, int primaryColor, int secondaryColor) {
		this(uid, dominant, name, branch, null, primaryColor, secondaryColor);
	}

	public AlleleBeeSpecies(String uid, boolean dominant, String name, IClassification branch, String binomial, int primaryColor, int secondaryColor) {
		this(uid, dominant, name, branch, binomial, primaryColor, secondaryColor, new JubilanceDefault());
	}

	public AlleleBeeSpecies(String uid, boolean dominant, String name, IClassification branch, String binomial, int primaryColor, int secondaryColor,
			IJubilanceProvider jubilanceProvider) {
		super(uid, dominant, name, branch, binomial, primaryColor, secondaryColor);

		this.jubilanceProvider = jubilanceProvider;
	}

	public AlleleBeeSpecies addProduct(ItemStack product, int chance) {
		this.products.put(product, chance);
		return this;
	}

	public AlleleBeeSpecies addSpecialty(ItemStack specialty, int chance) {
		this.specialty.put(specialty, chance);
		return this;
	}

	public AlleleBeeSpecies setJubilanceProvider(IJubilanceProvider provider) {
		this.jubilanceProvider = provider;
		return this;
	}

	@Override
	public HashMap<ItemStack, Integer> getProducts() {
		return products;
	}

	@Override
	public HashMap<ItemStack, Integer> getSpecialty() {
		return specialty;
	}

	@Override
	public boolean isJubilant(IBeeGenome genome, IBeeHousing housing) {
		return jubilanceProvider.isJubilant(this, genome, housing);
	}

}
