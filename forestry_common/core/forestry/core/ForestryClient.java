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
package forestry.core;

import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.TileMachine;
import forestry.core.proxy.Proxies;
import forestry.core.render.BlockRenderingHandler;
import forestry.core.render.RenderForestryTile;
import forestry.core.render.TextureBiofuelFX;
import forestry.core.render.TextureBiomassFX;
import forestry.core.render.TextureGlassFX;
import forestry.core.render.TextureHabitatLocatorFX;
import forestry.core.render.TextureHoneyFX;
import forestry.core.render.TextureIceFX;
import forestry.core.render.TextureJuiceFX;
import forestry.core.render.TextureMeadFX;
import forestry.core.render.TextureMilkFX;
import forestry.core.render.TextureSeedOilFX;

public class ForestryClient extends ForestryCore {

	public static int byBlockModelId;
	public static int blockModelIdEngine;

	@Override
	public void init(Object basemod) {

		super.init(basemod);

		byBlockModelId = Proxies.render.getNextAvailableRenderId();
		blockModelIdEngine = Proxies.render.getNextAvailableRenderId();

		preloadTexture(Defaults.TEXTURE_BLOCKS);
		preloadTexture(Defaults.TEXTURE_ITEMS);
		preloadTexture(Defaults.TEXTURE_CRATED);
		preloadTexture(Defaults.TEXTURE_BEES);
		preloadTexture(Defaults.TEXTURE_ARBORICULTURE);
		preloadTexture(Defaults.TEXTURE_FARM);
		preloadTexture(Defaults.TEXTURE_GERMLINGS);
		preloadTexture(Defaults.TEXTURE_ERRORS);
		preloadTexture(Defaults.TEXTURE_TRIGGERS);
		preloadTexture(Defaults.TEXTURE_PATH_BLOCKS + "/pipes.png");

		ClientRegistry.bindTileEntitySpecialRenderer(TileMachine.class, new RenderForestryTile());

		RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());
	}

	@Override
	public void postInit() {

		super.postInit();

		Proxies.render.registerTextureFX(new TextureBiomassFX());
		Proxies.render.registerTextureFX(new TextureBiofuelFX());
		Proxies.render.registerTextureFX(new TextureMilkFX());
		Proxies.render.registerTextureFX(new TextureHoneyFX());
		Proxies.render.registerTextureFX(new TextureSeedOilFX());
		Proxies.render.registerTextureFX(new TextureJuiceFX());
		Proxies.render.registerTextureFX(new TextureMeadFX());
		Proxies.render.registerTextureFX(new TextureGlassFX());
		Proxies.render.registerTextureFX(new TextureIceFX());

		if (ForestryItem.biomeFinder != null) {
			Proxies.render.registerTextureFX(new TextureHabitatLocatorFX(Proxies.common.getClientInstance()));
		}

	}

	public static void preloadTexture(String filename) {
		MinecraftForgeClient.preloadTexture(filename);
	}

}
