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
package forestry.factory;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.MachinePackage;
import forestry.core.proxy.Proxies;
import forestry.core.utils.CraftingIngredients;
import forestry.core.utils.EnergyConfiguration;
import forestry.factory.gadgets.MachineBottler;
import forestry.factory.gadgets.MachineCarpenter;
import forestry.factory.gadgets.MachineCentrifuge;
import forestry.factory.gadgets.MachineFabricator;
import forestry.factory.gadgets.MachineFermenter;
import forestry.factory.gadgets.MachineMoistener;
import forestry.factory.gadgets.MachineRaintank;
import forestry.factory.gadgets.MachineSqueezer;
import forestry.factory.gadgets.MachineStill;
import forestry.factory.gadgets.MillRainmaker;

public class PackagesFactory {

	public static final EnergyConfiguration energyConfigDefaultMachine = new EnergyConfiguration(Defaults.MACHINE_LATENCY,
			Defaults.MACHINE_MIN_ENERGY_RECEIVED, Defaults.MACHINE_MAX_ENERGY_RECEIVED, Defaults.MACHINE_MIN_ACTIVATION_ENERGY, Defaults.MACHINE_MAX_ENERGY);

	public static final EnergyConfiguration energyConfigDefaultBottler = new EnergyConfiguration(10, 5, 100, 60, 400);

	// /
	// / MACHINES
	// /
	public static MachinePackage getFermenterPackage() {
		MachinePackage pack;

		pack = new MachinePackage(new MachineFermenter.Factory(), "Fermenter", Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS
				+ "/fermenter_"), new CraftingIngredients(1, new Object[] { "X#X", "#Y#", "X#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
				ForestryItem.gearBronze, Character.valueOf('Y'), ForestryItem.sturdyCasing }));

		// Configure energy
		pack.energyConfig = new EnergyConfiguration(500, 5, 110, 80, 400);

		return pack;
	}

	public static MachinePackage getStillPackage() {
		MachinePackage pack;

		pack = new MachinePackage(new MachineStill.Factory(), "Still", Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS + "/still_"),
				new CraftingIngredients(1, new Object[] { "X#X", "#Y#", "X#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'), Item.redstone,
						Character.valueOf('Y'), ForestryItem.sturdyCasing }));

		// Configure energy
		pack.energyConfig = new EnergyConfiguration(100, 5, 110, 25, 540);

		return pack;
	}

	public static MachinePackage getBottlerPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		recipe = new CraftingIngredients(1, new Object[] { "X#X", "#Y#", "X#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
				ForestryItem.canEmpty, Character.valueOf('Y'), ForestryItem.sturdyCasing });

		pack = new MachinePackage(new MachineBottler.Factory(), "Bottler", Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS + "/bottler_",
				true, false), recipe);

		// Configure energy
		pack.energyConfig = PackagesFactory.energyConfigDefaultBottler;

		return pack;
	}

	public static MachinePackage getCarpenterPackage() {
		MachinePackage pack;

		pack = new MachinePackage(new MachineCarpenter.Factory(), "Carpenter", Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS
				+ "/carpenter_", true, false), new CraftingIngredients(1, new Object[] { "X#X", "XYX", "X#X", Character.valueOf('#'), Block.glass,
				Character.valueOf('X'), "ingotBronze", Character.valueOf('Y'), ForestryItem.sturdyCasing }));

		// Configure energy
		pack.energyConfig = new EnergyConfiguration(20, 1, 110, 25, 400);

		return pack;
	}

	public static MachinePackage getMoistenerPackage() {
		MachinePackage pack;

		pack = new MachinePackage(new MachineMoistener.Factory(), "Moistener", Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS
				+ "/moistener_", true, false), new CraftingIngredients(1, new Object[] { "X#X", "#Y#", "X#X", Character.valueOf('#'), Block.glass,
				Character.valueOf('X'), ForestryItem.gearCopper, Character.valueOf('Y'), new ItemStack(ForestryBlock.machine, 1, 0) }));

		// Configure energy
		pack.energyConfig = new EnergyConfiguration(0, 0, 0, 0, 0);

		return pack;
	}

	public static MachinePackage getCentrifugePackage() {
		MachinePackage pack;

		pack = new MachinePackage(new MachineCentrifuge.Factory(), "Centrifuge", Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS
				+ "/centrifuge_", false, false), new CraftingIngredients(1, new Object[] { "X#X", "XYX", "X#X", Character.valueOf('#'), Block.glass,
				Character.valueOf('X'), "ingotCopper", Character.valueOf('Y'), new ItemStack(ForestryItem.sturdyCasing) }));

		// Configure energy
		pack.energyConfig = new EnergyConfiguration(10, 1, 110, 25, 480);

		return pack;
	}

	public static MachinePackage getSqueezerPackage() {
		MachinePackage pack;

		pack = new MachinePackage(new MachineSqueezer.Factory(), "Squeezer", Proxies.render.getRenderDefaultMachine(
				Defaults.TEXTURE_PATH_BLOCKS + "/squeezer_", false, true), new CraftingIngredients(1, new Object[] { "X#X", "XYX", "X#X",
				Character.valueOf('#'), Block.glass, Character.valueOf('X'), "ingotTin", Character.valueOf('Y'), new ItemStack(ForestryItem.sturdyCasing) }));

		// Configure energy
		pack.energyConfig = new EnergyConfiguration(10, 5, 110, 25, 250);

		return pack;
	}

	public static MachinePackage getRaintankPackage() {
		MachinePackage pack;

		pack = new MachinePackage(new MachineRaintank.Factory(), "Raintank", Proxies.render.getRenderDefaultMachine(
				Defaults.TEXTURE_PATH_BLOCKS + "/raintank_", true, false), new CraftingIngredients(1, new Object[] { "X#X", "XYX", "X#X",
				Character.valueOf('#'), Block.glass, Character.valueOf('X'), Item.ingotIron, Character.valueOf('Y'), ForestryItem.sturdyCasing }));

		// Configure energy
		pack.energyConfig = PackagesFactory.energyConfigDefaultBottler;

		return pack;
	}

	public static MachinePackage getRainmakerPackage() {
		MachinePackage pack;

		byte charges = 8;
		pack = new MachinePackage(new MillRainmaker.Factory(), "Rainmaker",
				Proxies.render.getRenderMill(Defaults.TEXTURE_PATH_BLOCKS + "/rainmaker_", charges), new CraftingIngredients(1, new Object[] { "X#X", "#Y#",
						"X#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'), ForestryItem.gearTin, Character.valueOf('Y'),
						ForestryItem.hardenedCasing }));

		// Configure energy
		pack.energyConfig = new EnergyConfiguration(0, 0, 0, 0, 0);

		return pack;
	}

	public static MachinePackage getFabriactorPackage() {
		MachinePackage pack;

		CraftingIngredients recipe = null;
		recipe = new CraftingIngredients(1, new Object[] { "X#X", "#Y#", "XZX", Character.valueOf('#'), Block.glass, Character.valueOf('X'), Item.ingotGold,
				Character.valueOf('Y'), ForestryItem.sturdyCasing, Character.valueOf('Z'), Block.chest });
		pack = new MachinePackage(new MachineFabricator.Factory(), "Thermionic Fabricator", Proxies.render.getRenderBlock(Defaults.TEXTURE_PATH_BLOCKS
				+ "/fabricator_"), recipe);

		// Configure energy
		pack.energyConfig = new EnergyConfiguration(10, 5, 110, 25, 250);

		return pack;
	}

}
