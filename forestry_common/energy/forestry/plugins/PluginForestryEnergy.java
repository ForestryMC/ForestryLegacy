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
package forestry.plugins;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.api.core.PluginInfo;
import forestry.api.fuels.GeneratorFuel;
import forestry.core.circuits.Circuit;
import forestry.core.circuits.CircuitId;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.items.ItemForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.energy.GuiHandlerEnergy;
import forestry.energy.circuits.CircuitElectricBoost;
import forestry.energy.circuits.CircuitElectricChoke;
import forestry.energy.circuits.CircuitElectricEfficiency;
import forestry.energy.circuits.CircuitFireDampener;
import forestry.energy.gadgets.EngineDefinition;
import forestry.energy.gadgets.EngineTin;
import forestry.energy.gadgets.EngineCopper;
import forestry.energy.gadgets.EngineBronze;
import forestry.energy.gadgets.MachineGenerator;
import forestry.energy.proxy.ProxyEnergy;

@PluginInfo(pluginID = "Energy", name = "Energy", author = "SirSengir", url = Defaults.URL, description = "Adds several engines compatible with BuildCraft 3 as well as a generator for IC2.")
public class PluginForestryEnergy extends NativePlugin {

	@SidedProxy(clientSide = "forestry.energy.proxy.ClientProxyEnergy", serverSide = "forestry.energy.proxy.ProxyEnergy")
	public static ProxyEnergy proxy;

	public static MachineDefinition definitionEngineTin;
	public static MachineDefinition definitionEngineCopper;
	public static MachineDefinition definitionEngineBronze;
	public static MachineDefinition definitionGenerator;
	
	@Override
	public boolean isAvailable() {
		return !Config.disableEnergy;
	}

	@Override
	public String getDescription() {
		return "Energy";
	}

	@Override
	public void preInit() {
		super.preInit();

		int blockid = Config.getOrCreateBlockIdProperty("engine", Defaults.ID_BLOCK_ENGINE);
		
		definitionEngineTin = new EngineDefinition(blockid, Defaults.DEFINITION_ENGINETIN_META, "forestry.EngineTin", EngineTin.class,
				PluginForestryEnergy.proxy.getRenderDefaultEngine(Defaults.TEXTURE_PATH_BLOCKS + "/engine_tin_"),
				ShapedRecipeCustom.createShapedRecipe(new Object[] { "###", " X ", "YVY", Character.valueOf('#'), "ingotTin", Character.valueOf('X'),
				Block.glass, Character.valueOf('Y'), "gearTin", Character.valueOf('V'), Block.pistonBase },
				new ItemStack(blockid, 1, Defaults.DEFINITION_ENGINETIN_META))
				);
		definitionEngineCopper = new EngineDefinition(blockid, Defaults.DEFINITION_ENGINECOPPER_META, "forestry.EngineCopper", EngineCopper.class,
				PluginForestryEnergy.proxy.getRenderDefaultEngine(Defaults.TEXTURE_PATH_BLOCKS + "/engine_copper_"),
				ShapedRecipeCustom.createShapedRecipe(new Object[] { "###", " X ", "YVY", Character.valueOf('#'), "ingotCopper", Character.valueOf('X'),
				Block.glass, Character.valueOf('Y'), "gearCopper", Character.valueOf('V'), Block.pistonBase },
				new ItemStack(blockid, 1, Defaults.DEFINITION_ENGINECOPPER_META))
				);
		definitionEngineBronze = new EngineDefinition(blockid, Defaults.DEFINITION_ENGINEBRONZE_META, "forestry.EngineBronze", EngineBronze.class,
				PluginForestryEnergy.proxy.getRenderDefaultEngine(Defaults.TEXTURE_PATH_BLOCKS + "/engine_bronze_"),
				ShapedRecipeCustom.createShapedRecipe(new Object[] { "###", " X ", "YVY", Character.valueOf('#'), "ingotBronze", Character.valueOf('X'),
				Block.glass, Character.valueOf('Y'), "gearBronze", Character.valueOf('V'), Block.pistonBase },
				new ItemStack(blockid, 1, Defaults.DEFINITION_ENGINEBRONZE_META))
				);
		definitionGenerator = new MachineDefinition(blockid, Defaults.DEFINITION_GENERATOR_META, "forestry.Generator", MachineGenerator.class,
				Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS + "/generator_"),
				ShapedRecipeCustom.createShapedRecipe(new Object[] { "X#X", "XYX", "X#X", Character.valueOf('#'), Block.glass, Character.valueOf('X'),
						Item.ingotGold, Character.valueOf('Y'), ForestryItem.sturdyCasing },
				new ItemStack(blockid, 1, Defaults.DEFINITION_GENERATOR_META))
				);
		
		
		ForestryBlock.engine = new BlockBase(blockid,
				Material.iron, new MachineDefinition[] { definitionEngineTin, definitionEngineCopper, definitionEngineBronze, definitionGenerator }, true).setBlockName("for.engine");
		Item.itemsList[ForestryBlock.engine.blockID] = null;
		Item.itemsList[ForestryBlock.engine.blockID] = new ItemForestryBlock(ForestryBlock.engine.blockID - 256, "for.engine");

		ChipsetManager.circuitRegistry.registerLegacyMapping(CircuitId.ELECTRIC_CHOKE_I, "forestry.energyChoke1");
		ChipsetManager.circuitRegistry.registerLegacyMapping(CircuitId.FIRE_DAMPENER_I, "forestry.energyDampener1");
		ChipsetManager.circuitRegistry.registerLegacyMapping(CircuitId.ELECTRIC_EFFICIENCY_I, "forestry.energyEfficiency1");
		ChipsetManager.circuitRegistry.registerLegacyMapping(CircuitId.ELECTRIC_BOOST_I, "forestry.energyBoost1");
		ChipsetManager.circuitRegistry.registerLegacyMapping(CircuitId.ELECTRIC_BOOST_II, "forestry.energyBoost2");
	}

	@Override
	public void doInit() {
		super.doInit();

		definitionEngineTin.register();
		definitionEngineCopper.register();
		definitionEngineBronze.register();
		definitionGenerator.register();
		
		GeneratorFuel.fuels.put(ForestryItem.liquidBiofuel.itemID, new GeneratorFuel(new LiquidStack(ForestryItem.liquidBiofuel, 1), 32, 2));
		GeneratorFuel.fuels.put(ForestryItem.liquidBiomass.itemID, new GeneratorFuel(new LiquidStack(ForestryItem.liquidBiomass, 1), 8, 1));

		Circuit.energyElectricChoke1 = new CircuitElectricChoke("energyChoke1");
		Circuit.energyFireDampener1 = new CircuitFireDampener("energyDampener1");
		Circuit.energyElectricEfficiency1 = new CircuitElectricEfficiency("energyEfficiency1");
		Circuit.energyElectricBoost1 = new CircuitElectricBoost("energyBoost1", 2, 7, 2, "electric.boost.1", new String[] {
				"Increases output by 2 MJ/t", "Increases intake by 7 EU/t" });
		Circuit.energyElectricBoost2 = new CircuitElectricBoost("energyBoost2", 2, 15, 4, "electric.boost.2", new String[] {
				"Increases output by 4 MJ/t", "Increases intake by 15 EU/t" });
	}

	@Override
	protected void registerPackages() {
		// MACHINE BASED TILE ENTITIES
		//GadgetManager.registerMachinePackage(4, PackagesEnergy.getGeneratorPackage());
	}

	@Override
	protected void registerItems() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void registerBackpackItems() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void registerRecipes() {

		ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout("forestry.engine.tin");
		
		// / Solder Manager
		ChipsetManager.solderManager.addRecipe(layout, new ItemStack(ForestryItem.tubes, 1, 0), Circuit.energyElectricChoke1);
		ChipsetManager.solderManager.addRecipe(layout, new ItemStack(ForestryItem.tubes, 1, 1), Circuit.energyElectricBoost1);
		ChipsetManager.solderManager.addRecipe(layout, new ItemStack(ForestryItem.tubes, 1, 2), Circuit.energyElectricBoost2);
		ChipsetManager.solderManager.addRecipe(layout, new ItemStack(ForestryItem.tubes, 1, 3), Circuit.energyElectricEfficiency1);
	}

	@Override
	protected void registerCrates() {
		// TODO Auto-generated method stub

	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerEnergy();
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}

}
