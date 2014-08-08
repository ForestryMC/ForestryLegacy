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
import forestry.arboriculture.gadgets.BlockLeaves;
import forestry.arboriculture.gadgets.TileLeaves;
import forestry.plugins.PluginForestryArboriculture;

/**
 * Ugly but serviceable renderer for leaves, taking fruits into account. 
 */
public class LeavesRenderingHandler implements ISimpleBlockRenderingHandler {

	private static final double OVERLAY_SHIFT = 0.01;
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		// Render the plain leaf block.
		renderer.renderStandardBlock(block, x, y, z);
		
		// Render overlay for fruit leaves.
		TileLeaves tile = BlockLeaves.getLeafTile(world, x, y, z);
		int fruitIndex = -1;
		int fruitColor = 0xffffff;
		if (tile != null) {
			fruitIndex = tile.getTextureFruits();
			fruitColor = tile.getFruitColour();
		}
		
		if(fruitIndex >= 0) {
			renderFruitOverlay(world, block, x, y, z, renderer, fruitIndex, fruitColor);
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return false;
	}

	@Override
	public int getRenderId() {
		return PluginForestryArboriculture.modelIdLeaves;
	}

    private boolean renderFruitOverlay(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, int textureIndex, int multiplier) {
    	
        float mR = (float)(multiplier >> 16 & 255) / 255.0F;
        float mG = (float)(multiplier >> 8 & 255) / 255.0F;
        float mB = (float)(multiplier & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable) {
        	mR = (mR * 30.0F + mG * 59.0F + mB * 11.0F) / 100.0F;
        	mG = (mR * 30.0F + mG * 70.0F) / 100.0F;
        	mB = (mR * 30.0F + mB * 70.0F) / 100.0F;
        }

        return renderFruitOverlayWithColorMultiplier(world, block, x, y, z, mR, mG, mB, renderer, textureIndex);
    }

    private boolean renderFruitOverlayWithColorMultiplier(IBlockAccess world, Block block, int x, int y, int z, float r, float g, float b, RenderBlocks renderer, int textureIndex) {

    	int mixedBrightness = block.getMixedBrightnessForBlock(world, x, y, z);

        float adjR = 0.5f*r;
        float adjG = 0.5f*g;
        float adjB = 0.5f*b;

    	// Bottom
   		renderBottomFace(world, block, x, y, z, renderer, textureIndex, mixedBrightness, adjR, adjG, adjB);
   		renderTopFace(world, block, x, y, z, renderer, textureIndex, mixedBrightness, adjR, adjG, adjB);
   		renderEastFace(world, block, x, y, z, renderer, textureIndex, mixedBrightness, adjR, adjG, adjB);
   		renderWestFace(world, block, x, y, z, renderer, textureIndex, mixedBrightness, adjR, adjG, adjB);
   		renderNorthFace(world, block, x, y, z, renderer, textureIndex, mixedBrightness, adjR, adjG, adjB);
   		renderSouthFace(world, block, x, y, z, renderer, textureIndex, mixedBrightness, adjR, adjG, adjB);
    	
    	return true;
    }
    
    private int determineMixedBrightness(IBlockAccess world, Block par1Block, int x, int y, int z, RenderBlocks renderer, int mixedBrightness) {
    	return renderer.renderMinY > 0.0D ? mixedBrightness : par1Block.getMixedBrightnessForBlock(world, x, y - 1, z);
    }
    
	private void renderBottomFace(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, int textureIndex, int mixedBrightness, float r, float g, float b) {
		
		if (!renderer.renderAllFaces && !block.shouldSideBeRendered(world, x, y - 1, z, 0))
			return;
					
        Tessellator tesselator = Tessellator.instance;
        
        tesselator.setBrightness(determineMixedBrightness(world, block, x, y - 1, z, renderer, mixedBrightness));
        tesselator.setColorOpaque_F(r, g, b);
        renderer.renderBottomFace(block, (double)x, (double)y - OVERLAY_SHIFT, (double)z, textureIndex);

	}
	
	private void renderTopFace(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, int textureIndex, int mixedBrightness, float r, float g, float b) {
		
		if (!renderer.renderAllFaces && !block.shouldSideBeRendered(world, x, y + 1, z, 1))
			return;
					
        Tessellator tesselator = Tessellator.instance;
        
        tesselator.setBrightness(determineMixedBrightness(world, block, x, y + 1, z, renderer, mixedBrightness));
        tesselator.setColorOpaque_F(r, g, b);
        renderer.renderTopFace(block, (double)x, (double)y + OVERLAY_SHIFT, (double)z, textureIndex);

	}

	private void renderEastFace(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, int textureIndex, int mixedBrightness, float r, float g, float b) {
		
		if (!renderer.renderAllFaces && !block.shouldSideBeRendered(world, x, y, z - 1, 2))
			return;
					
        Tessellator tesselator = Tessellator.instance;
        
        tesselator.setBrightness(determineMixedBrightness(world, block, x, y, z - 1, renderer, mixedBrightness));
        tesselator.setColorOpaque_F(r, g, b);
        renderer.renderEastFace(block, (double)x, (double)y, (double)z - OVERLAY_SHIFT, textureIndex);

	}

	private void renderWestFace(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, int textureIndex, int mixedBrightness, float r, float g, float b) {
		
		if (!renderer.renderAllFaces && !block.shouldSideBeRendered(world, x, y, z + 1, 3))
			return;
					
        Tessellator tesselator = Tessellator.instance;
        
        tesselator.setBrightness(determineMixedBrightness(world, block, x, y, z + 1, renderer, mixedBrightness));
        tesselator.setColorOpaque_F(r, g, b);
        renderer.renderWestFace(block, (double)x, (double)y, (double)z + OVERLAY_SHIFT, textureIndex);

	}

	private void renderNorthFace(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, int textureIndex, int mixedBrightness, float r, float g, float b) {
		
		if (!renderer.renderAllFaces && !block.shouldSideBeRendered(world, x - 1, y, z, 4))
			return;
					
        Tessellator tesselator = Tessellator.instance;
        
        tesselator.setBrightness(determineMixedBrightness(world, block, x - 1, y, z, renderer, mixedBrightness));
        tesselator.setColorOpaque_F(r, g, b);
        renderer.renderNorthFace(block, (double)x - OVERLAY_SHIFT, (double)y, (double)z, textureIndex);

	}

	private void renderSouthFace(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, int textureIndex, int mixedBrightness, float r, float g, float b) {
		
		if (!renderer.renderAllFaces && !block.shouldSideBeRendered(world, x + 1, y, z, 5))
			return;
					
        Tessellator tesselator = Tessellator.instance;
        
        tesselator.setBrightness(determineMixedBrightness(world, block, x + 1, y, z, renderer, mixedBrightness));
        tesselator.setColorOpaque_F(r, g, b);
        renderer.renderSouthFace(block, (double)x + OVERLAY_SHIFT, (double)y, (double)z, textureIndex);

	}

}
