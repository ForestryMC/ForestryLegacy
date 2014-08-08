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
package forestry.arboriculture.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.gadgets.BlockSapling;
import forestry.arboriculture.gadgets.TileSapling;
import forestry.plugins.PluginForestryArboriculture;

public class SaplingRenderHandler implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		if (modelId != this.getRenderId())
			return false;

		TileSapling tile = BlockSapling.getSaplingTile(world, x, y, z);

		IAlleleTreeSpecies species = (IAlleleTreeSpecies) TreeManager.breedingManager.getDefaultTreeTemplate()[0];
		if (tile == null || tile.getTree() == null)
			return true;

		return renderCrossedSquares(species, world, block, x, y, z);
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return false;
	}

	@Override
	public int getRenderId() {
		return PluginForestryArboriculture.modelIdSaplings;
	}

	protected boolean renderCrossedSquares(IAlleleTreeSpecies species, IBlockAccess world, Block block, int x, int y, int z) {

		Tessellator tess = Tessellator.instance;
		tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		int colourMultiplier = block.colorMultiplier(world, x, y, z);
		float r = (colourMultiplier >> 16 & 255) / 255.0F;
		float g = (colourMultiplier >> 8 & 255) / 255.0F;
		float b = (colourMultiplier & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable) {

			r = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
			g = (r * 30.0F + g * 70.0F) / 100.0F;
			b = (r * 30.0F + b * 70.0F) / 100.0F;
		}

		tess.setColorOpaque_F(r, g, b);
		drawCrossedSquares(world, block, x, y, z, x, y, z);
		return true;
	}

	protected void drawCrossedSquares(IBlockAccess world, Block block, int x, int y, int z, double par3, double par5, double par7) {

		Tessellator tess = Tessellator.instance;
		int iconIndex = block.getBlockTexture(world, x, y, z, 0);

		int line = (iconIndex & 15) << 4;
		int var12 = iconIndex & 240;
		double var13 = line / 256.0F;
		double var15 = (line + 15.99F) / 256.0F;
		double var17 = var12 / 256.0F;
		double var19 = (var12 + 15.99F) / 256.0F;
		double var21 = par3 + 0.5D - 0.45D;
		double var23 = par3 + 0.5D + 0.45D;
		double var25 = par7 + 0.5D - 0.45D;
		double var27 = par7 + 0.5D + 0.45D;
		tess.addVertexWithUV(var21, par5 + 1.0D, var25, var13, var17);
		tess.addVertexWithUV(var21, par5 + 0.0D, var25, var13, var19);
		tess.addVertexWithUV(var23, par5 + 0.0D, var27, var15, var19);
		tess.addVertexWithUV(var23, par5 + 1.0D, var27, var15, var17);
		tess.addVertexWithUV(var23, par5 + 1.0D, var27, var13, var17);
		tess.addVertexWithUV(var23, par5 + 0.0D, var27, var13, var19);
		tess.addVertexWithUV(var21, par5 + 0.0D, var25, var15, var19);
		tess.addVertexWithUV(var21, par5 + 1.0D, var25, var15, var17);
		tess.addVertexWithUV(var21, par5 + 1.0D, var27, var13, var17);
		tess.addVertexWithUV(var21, par5 + 0.0D, var27, var13, var19);
		tess.addVertexWithUV(var23, par5 + 0.0D, var25, var15, var19);
		tess.addVertexWithUV(var23, par5 + 1.0D, var25, var15, var17);
		tess.addVertexWithUV(var23, par5 + 1.0D, var25, var13, var17);
		tess.addVertexWithUV(var23, par5 + 0.0D, var25, var13, var19);
		tess.addVertexWithUV(var21, par5 + 0.0D, var27, var15, var19);
		tess.addVertexWithUV(var21, par5 + 1.0D, var27, var15, var17);
	}

}
