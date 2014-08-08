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
package forestry.cultivation.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import forestry.core.ForestryClient;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.proxy.Proxies;
import forestry.cultivation.gadgets.TilePlanter;

public class RenderPlanter implements IBlockRenderer {
	private ModelBase model = new ModelBase() {
	};
	private String gfxBase;

	private ModelRenderer base;
	private ModelRenderer ceiling;
	private ModelRenderer terrarium;
	private ModelRenderer showcase;

	public RenderPlanter() {
		base = new ModelRenderer(model, 0, 0);
		base.addBox(-8F, 4F, -8F, 16, 4, 16);
		base.rotationPointX = 8;
		base.rotationPointY = 8;
		base.rotationPointZ = 8;

		ceiling = new ModelRenderer(model, 0, 0);
		ceiling.addBox(-8F, -8F, -8F, 16, 4, 16);
		ceiling.rotationPointX = 8;
		ceiling.rotationPointY = 8;
		ceiling.rotationPointZ = 8;

		terrarium = new ModelRenderer(model, 0, 0);
		terrarium.addBox(-6F, -7F, -6F, 12, 12, 12);
		terrarium.rotationPointX = 8;
		terrarium.rotationPointY = 8;
		terrarium.rotationPointZ = 8;

		showcase = new ModelRenderer(model, 0, 0);
		showcase.addBox(-4F, -4F, -4F, 8, 8, 8);
		showcase.rotationPointX = 8;
		showcase.rotationPointY = 8;
		showcase.rotationPointZ = 8;

	}

	public RenderPlanter(String baseTexture) {
		this();
		this.gfxBase = baseTexture;
	}

	/**
	 * Used to preload textures for Minecraft Forge
	 * 
	 * @param gfxBase
	 */
	public void preloadTextures() {
		ForestryClient.preloadTexture(gfxBase + "base.png");
		ForestryClient.preloadTexture(gfxBase + "ceiling.png");
		ForestryClient.preloadTexture(gfxBase + "terrarium.png");
		ForestryClient.preloadTexture(gfxBase + "showcase.png");
	}

	public void inventoryRender(double x, double y, double z, float f, float f1) {
		render(ForgeDirection.UP, gfxBase, x, y, z);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {

		TilePlanter planter = (TilePlanter) tileentity;
		if (planter.machine != null) {
			render(planter.getOrientation(), gfxBase, d, d1, d2);
		}
	}

	private void render(ForgeDirection orientation, String gfxBase, double x, double y, double z) {

		GL11.glPushMatrix();
		GL11.glDisable(2896 /* GL_LIGHTING */);

		GL11.glTranslatef((float) x, (float) y, (float) z);

		float[] angle = { 0, 0, 0 };
		float[] translate = { 0, 0, 0 };

		switch (orientation) {
		case EAST:
			translate[1] = 1;
			break;
		case WEST:
			angle[2] = (float) Math.PI;
			translate[1] = -1;
			break;
		case UP:
			angle[2] = (float) -Math.PI / 2;
			translate[0] = 1;
			break;
		case DOWN:
			angle[2] = (float) Math.PI / 2;
			translate[0] = -1;
			break;
		case SOUTH:
			angle[0] = (float) Math.PI / 2;
			translate[2] = 1;
			break;
		default:
		case NORTH:
			angle[0] = (float) -Math.PI / 2;
			translate[2] = -1;
			break;
		}

		base.rotateAngleX = angle[0];
		base.rotateAngleY = angle[1];
		base.rotateAngleZ = angle[2];

		ceiling.rotateAngleX = angle[0];
		ceiling.rotateAngleY = angle[1];
		ceiling.rotateAngleZ = angle[2];

		terrarium.rotateAngleX = angle[0];
		terrarium.rotateAngleY = angle[1];
		terrarium.rotateAngleZ = angle[2];

		showcase.rotateAngleX = angle[0];
		showcase.rotateAngleY = angle[1];
		showcase.rotateAngleZ = angle[2];

		float factor = (float) (1.0 / 16.0);

		Proxies.common.bindTexture(gfxBase + "base.png");
		base.render(factor);

		Proxies.common.bindTexture(gfxBase + "ceiling.png");
		ceiling.render(factor);

		Proxies.common.bindTexture(gfxBase + "terrarium.png");
		terrarium.render(factor);

		Proxies.common.bindTexture(gfxBase + "showcase.png");
		showcase.render(factor);

		GL11.glEnable(2896 /* GL_LIGHTING */);
		GL11.glPopMatrix();
	}
}
