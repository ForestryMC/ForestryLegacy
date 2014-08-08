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
package forestry.arboriculture.proxy;

import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.RenderingRegistry;
import forestry.arboriculture.render.FenceRenderingHandler;
import forestry.arboriculture.render.LeavesRenderingHandler;
import forestry.arboriculture.render.SaplingRenderHandler;
import forestry.arboriculture.render.StairItemRenderer;
import forestry.core.config.ForestryBlock;
import forestry.core.utils.Localization;
import forestry.plugins.PluginForestryArboriculture;

public class ClientProxyArboriculture extends ProxyArboriculture {
	@Override
	public void initializeRendering() {
		PluginForestryArboriculture.modelIdSaplings = RenderingRegistry.getNextAvailableRenderId();
		PluginForestryArboriculture.modelIdLeaves = RenderingRegistry.getNextAvailableRenderId();
		PluginForestryArboriculture.modelIdFences = RenderingRegistry.getNextAvailableRenderId();

		RenderingRegistry.registerBlockHandler(new SaplingRenderHandler());
		RenderingRegistry.registerBlockHandler(new LeavesRenderingHandler());
		RenderingRegistry.registerBlockHandler(new FenceRenderingHandler());
		
		MinecraftForgeClient.registerItemRenderer(ForestryBlock.stairs.blockID, new StairItemRenderer());
	}
	
	@Override
	public int getFoliageColorBasic() {
		return ColorizerFoliage.getFoliageColorBasic();
	}
	
	@Override
	public int getFoliageColorBirch() {
		return ColorizerFoliage.getFoliageColorBirch();
	}

	@Override
	public int getFoliageColorPine() {
		return ColorizerFoliage.getFoliageColorPine();
	}

	public int getBiomeFoliageColour(World world, int x, int z) {
		return world.getBiomeGenForCoords(x, z).getBiomeFoliageColor();
	}
	
	@Override
	public void addLocalizations() {
		Localization.instance.addLocalization("/lang/forestry/arboriculture/");
	}

}
