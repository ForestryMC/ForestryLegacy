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
package forestry.energy.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import forestry.core.ForestryClient;
import forestry.core.TemperatureState;
import forestry.core.config.Defaults;
import forestry.core.gadgets.Engine;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.proxy.Proxies;

public class RenderEngine extends TileEntitySpecialRenderer implements IBlockRenderer {
	private ModelBase model = new ModelBase() {
	};
	private String gfxBase;

	private ModelRenderer boiler;
	private ModelRenderer trunk;
	private ModelRenderer piston;
	private ModelRenderer extension;

	public RenderEngine() {
		boiler = new ModelRenderer(model, 0, 0);
		boiler.addBox(-8F, -8F, -8F, 16, 6, 16);
		boiler.rotationPointX = 8;
		boiler.rotationPointY = 8;
		boiler.rotationPointZ = 8;

		trunk = new ModelRenderer(model, 0, 0);
		trunk.addBox(-4F, -4F, -4F, 8, 12, 8);
		trunk.rotationPointX = 8F;
		trunk.rotationPointY = 8F;
		trunk.rotationPointZ = 8F;

		piston = new ModelRenderer(model, 0, 0);
		piston.addBox(-6F, -2, -6F, 12, 4, 12);
		piston.rotationPointX = 8F;
		piston.rotationPointY = 8F;
		piston.rotationPointZ = 8F;

		extension = new ModelRenderer(model, 0, 0);
		extension.addBox(-5F, -3, -5F, 10, 2, 10);
		extension.rotationPointX = 8F;
		extension.rotationPointY = 8F;
		extension.rotationPointZ = 8F;
	}

	public RenderEngine(String baseTexture) {
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
		ForestryClient.preloadTexture(gfxBase + "extension.png");
		ForestryClient.preloadTexture(gfxBase + "piston.png");
		ForestryClient.preloadTexture(Defaults.TEXTURE_PATH_BLOCKS + "/engine_trunk_low.png");
		ForestryClient.preloadTexture(Defaults.TEXTURE_PATH_BLOCKS + "/engine_trunk_medium.png");
		ForestryClient.preloadTexture(Defaults.TEXTURE_PATH_BLOCKS + "/engine_trunk_high.png");
		ForestryClient.preloadTexture(Defaults.TEXTURE_PATH_BLOCKS + "/engine_trunk_higher.png");
		ForestryClient.preloadTexture(Defaults.TEXTURE_PATH_BLOCKS + "/engine_trunk_highest.png");
	}

	public void inventoryRender(double x, double y, double z, float f, float f1) {
		render(TemperatureState.COOL, 0.25F, ForgeDirection.UP, x, y, z);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double d, double d1, double d2, float f) {

		Engine engine = (Engine)tile;

		if (engine != null) {
			render(engine.getTemperatureState(), engine.progress, engine.getOrientation(), d, d1, d2);
		}
	}

	private void render(TemperatureState state, float progress, ForgeDirection orientation, double x, double y, double z) {

		GL11.glPushMatrix();
		GL11.glDisable(2896 /* GL_LIGHTING */);

		GL11.glTranslatef((float) x, (float) y, (float) z);

		float step;

		if (progress > 0.5) {
			step = 5.99F - (progress - 0.5F) * 2F * 5.99F;
		} else {
			step = progress * 2F * 5.99F;
		}

		float[] angle = { 0, 0, 0 };
		float[] translate = { 0, 0, 0 };
		float tfactor = step / 16;

		switch (orientation) {
		case EAST:
			angle[2] = (float) -Math.PI / 2;
			translate[0] = 1;
			break;
		case WEST:
			angle[2] = (float) Math.PI / 2;
			translate[0] = -1;
			break;
		case UP:
			translate[1] = 1;
			break;
		case DOWN:
			angle[2] = (float) Math.PI;
			translate[1] = -1;
			break;
		case SOUTH:
			angle[0] = (float) Math.PI / 2;
			translate[2] = 1;
			break;
		case NORTH:
		default:
			angle[0] = (float) -Math.PI / 2;
			translate[2] = -1;
			break;
		}

		boiler.rotateAngleX = angle[0];
		boiler.rotateAngleY = angle[1];
		boiler.rotateAngleZ = angle[2];

		trunk.rotateAngleX = angle[0];
		trunk.rotateAngleY = angle[1];
		trunk.rotateAngleZ = angle[2];

		piston.rotateAngleX = angle[0];
		piston.rotateAngleY = angle[1];
		piston.rotateAngleZ = angle[2];

		extension.rotateAngleX = angle[0];
		extension.rotateAngleY = angle[1];
		extension.rotateAngleZ = angle[2];

		float factor = (float) (1.0 / 16.0);

		Proxies.common.bindTexture(gfxBase + "base.png");
		boiler.render(factor);

		Proxies.common.bindTexture(gfxBase + "piston.png");
		GL11.glTranslatef(translate[0] * tfactor, translate[1] * tfactor, translate[2] * tfactor);
		piston.render(factor);

		GL11.glTranslatef(-translate[0] * tfactor, -translate[1] * tfactor, -translate[2] * tfactor);

		Proxies.common.bindTexture(gfxBase + "extension.png");

		float chamberf = 2F / 16F;

		for (int i = 0; i <= step + 2; i += 2) {
			extension.render(factor);
			GL11.glTranslatef(translate[0] * chamberf, translate[1] * chamberf, translate[2] * chamberf);
		}

		for (int i = 0; i <= step + 2; i += 2) {
			GL11.glTranslatef(-translate[0] * chamberf, -translate[1] * chamberf, -translate[2] * chamberf);
		}

		String texture = "";

		switch (state) {
		case OVERHEATING:
			texture = Defaults.TEXTURE_PATH_BLOCKS + "/engine_trunk_highest.png";
			break;
		case RUNNING_HOT:
			texture = Defaults.TEXTURE_PATH_BLOCKS + "/engine_trunk_higher.png";
			break;
		case OPERATING_TEMPERATURE:
			texture = Defaults.TEXTURE_PATH_BLOCKS + "/engine_trunk_high.png";
			break;
		case WARMED_UP:
			texture = Defaults.TEXTURE_PATH_BLOCKS + "/engine_trunk_medium.png";
			break;
		case COOL:
		default:
			texture = Defaults.TEXTURE_PATH_BLOCKS + "/engine_trunk_low.png";
			break;

		}
		Proxies.common.bindTexture(texture);
		trunk.render(factor);

		GL11.glEnable(2896 /* GL_LIGHTING */);
		GL11.glPopMatrix();
	}
}
