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
package forestry.core.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLTextureFX;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import forestry.core.config.Config;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.render.BlockRenderingHandler;
import forestry.core.render.EntitySnowFX;
import forestry.core.render.RenderBlock;
import forestry.core.render.RenderMachine;
import forestry.core.render.RenderMill;
import forestry.core.render.TileRendererIndex;

public class ClientProxyRender extends ProxyRender {

	@Override
	public int getNextAvailableRenderId() {
		return RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public boolean fancyGraphicsEnabled() {
		return Proxies.common.getClientInstance().gameSettings.fancyGraphics;
	}

	@Override
	public boolean hasRendering() {
		return true;
	}

	@Override
	public void registerTESR(MachineDefinition definition) {
		BlockRenderingHandler.byBlockRenderer.put(new TileRendererIndex(Block.blocksList[definition.blockID], definition.meta), definition.renderer);
		ClientRegistry.bindTileEntitySpecialRenderer(definition.teClass, (TileEntitySpecialRenderer)definition.renderer);
	}

	@Override
	public IBlockRenderer getRenderBlock(String gfxBase) {
		return new RenderBlock(gfxBase);
	}

	@Override
	public IBlockRenderer getRenderDefaultMachine(String gfxBase) {
		return new RenderMachine(gfxBase);
	}

	@Override
	public IBlockRenderer getRenderDefaultMachine(String gfxBase, boolean resourceLevel, boolean productLevel) {
		return new RenderMachine(gfxBase, resourceLevel, productLevel);
	}

	@Override
	public IBlockRenderer getRenderMill(String gfxBase) {
		return new RenderMill(gfxBase);
	}

	@Override
	public IBlockRenderer getRenderMill(String gfxBase, byte charges) {
		return new RenderMill(gfxBase, charges);
	}

	@Override
	public void registerTextureFX(Object textureFX) {
		Proxies.common.getClientInstance().renderEngine.registerTextureFX((FMLTextureFX) textureFX);
	}

	@Override
	public void addSnowFX(World world, double xCoord, double yCoord, double zCoord, int color, int areaX, int areaY, int areaZ) {

		if (!Config.enableParticleFX)
			return;

		double spawnX = xCoord + world.rand.nextInt(areaX * 2) - areaX;
		double spawnY = yCoord + world.rand.nextInt(areaY);
		double spawnZ = zCoord + world.rand.nextInt(areaZ * 2) - areaZ;

		Proxies.common.getClientInstance().effectRenderer.addEffect(new EntitySnowFX(world, spawnX, spawnY, spawnZ, 0.0f, 0.0f, 0.0f));
	}

}
