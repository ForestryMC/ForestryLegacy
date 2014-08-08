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
package forestry.core.gadgets;

import java.util.ArrayList;

import forestry.core.config.Defaults;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.utils.CraftingIngredients;
import forestry.core.utils.EnergyConfiguration;
import forestry.core.utils.StructureBlueprint;
import forestry.core.utils.TextureDescription;

public class MachinePackage extends EntityPackage {
	public final MachineFactory factory;
	public final String itemName;
	public final ArrayList<CraftingIngredients> recipes = new ArrayList<CraftingIngredients>();
	public final ArrayList<StructureBlueprint> blueprints = new ArrayList<StructureBlueprint>();
	public EnergyConfiguration energyConfig;

	public MachinePackage(MachineFactory factory, String itemName) {
		this.factory = factory;
		this.itemName = itemName;
		energyConfig = new EnergyConfiguration(Defaults.MACHINE_LATENCY, Defaults.MACHINE_MIN_ENERGY_RECEIVED, Defaults.MACHINE_MAX_ENERGY_RECEIVED,
				Defaults.MACHINE_MIN_ACTIVATION_ENERGY, Defaults.MACHINE_MAX_ENERGY);
	}

	public MachinePackage(MachineFactory factory, String itemName, IBlockRenderer renderer) {
		this(factory, itemName);
		this.renderer = renderer;
	}

	public MachinePackage(MachineFactory factory, String itemName, TextureDescription texture) {
		this(factory, itemName);
		this.textures = texture;
	}

	public MachinePackage(MachineFactory factory, String itemName, IBlockRenderer renderer, CraftingIngredients recipe) {
		this(factory, itemName, renderer);
		recipes.add(recipe);
	}

	public MachinePackage(MachineFactory factory, String itemName, IBlockRenderer renderer, CraftingIngredients[] recipes) {
		this(factory, itemName, renderer);
		for (CraftingIngredients recipe : recipes) {
			this.recipes.add(recipe);
		}
	}

	public MachinePackage(MachineFactory factory, String itemName, TextureDescription texture, CraftingIngredients recipe) {
		this(factory, itemName, texture);
		recipes.add(recipe);
	}

	public MachinePackage(MachineFactory factory, String itemName, TextureDescription texture, CraftingIngredients[] recipes) {
		this(factory, itemName, texture);
		for (CraftingIngredients recipe : recipes)
			if (recipe != null) {
				this.recipes.add(recipe);
			}
	}
}
