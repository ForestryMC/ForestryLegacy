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

import java.util.Random;

import net.minecraft.command.ICommand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.IPacketHandler;
import forestry.api.core.IPickupHandler;
import forestry.api.core.IPlugin;
import forestry.api.core.IResupplyHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.api.core.PluginInfo;
import forestry.core.config.Configuration;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;
import forestry.pipes.GuiHandlerPipes;
import forestry.pipes.network.PacketHandlerPipes;
import forestry.pipes.proxy.ProxyPipes;

@PluginInfo(pluginID = "Pipes", name = "Pipes", author = "SirSengir", url = Defaults.URL, description = "Adds the apiarist's pipe for beekeeping if apiculture is enabled and BuildCraft 3 is present.")
public class PluginPropolisPipe implements IPlugin {

	public static ProxyPipes proxy;

	static String CONFIG_CATEGORY = "pipes";
	public static Configuration config;
	public static int propolisPipeItemId;

	public static String textureBees = Defaults.TEXTURE_PATH_GUI + "/analyzer_icons.png";
	/**
	 * Pipe used to sort bees from Forestry.
	 */
	public static Item pipeItemsPropolis;

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded("BuildCraft|Transport");
	}

	@Override
	public void preInit() {

		config = new Configuration();

		propolisPipeItemId = Integer.parseInt(PluginPropolisPipe.config.get("propolisPipe", CONFIG_CATEGORY, 14000).Value);

		config.save();

	}

	@Override
	public void doInit() {
	}

	@Override
	public void postInit() {
		String proxyClass = "forestry.pipes.proxy.ProxyPipes";
		if (FMLCommonHandler.instance().getSide().isClient()) {
			proxyClass = "forestry.pipes.proxy.ClientProxyPipes";
		}

		proxy = (ProxyPipes) Proxies.common.instantiateIfModLoaded("BuildCraft|Transport", proxyClass);

		if (proxy == null)
			return;

		proxy.initPropolisPipe();
		proxy.registerCraftingPropolis(new ItemStack(ForestryItem.propolis, 1, 0));
		proxy.addLocalizations();

	}

	@Override
	public String getDescription() {
		return "Apiarist's Pipe for BC3";
	}

	@Override
	public void generateSurface(World world, Random rand, int chunkX, int chunkZ) {
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerPipes();
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return new PacketHandlerPipes();
	}

	@Override
	public IPickupHandler getPickupHandler() {
		return null;
	}

	@Override
	public IResupplyHandler getResupplyHandler() {
		return null;
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}

	@Override
	public ICommand[] getConsoleCommands() {
		return null;
	}

}
