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
package forestry.core.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import forestry.core.ForestryClient;
import forestry.core.gadgets.TileMachine;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumTankLevel;
import forestry.energy.gadgets.MachineGenerator;

public class RenderMachine extends TileEntitySpecialRenderer implements IBlockRenderer {

	private ModelBase model = new ModelBase() {
	};
	private String gfxBase;

	private boolean resourceLevel = true;
	private boolean productLevel = true;

	private ModelRenderer basefront;
	private ModelRenderer baseback;
	private ModelRenderer resourceTank;
	private ModelRenderer productTank;

	public RenderMachine() {

		basefront = new ModelRenderer(model, 0, 0);
		basefront.addBox(-8F, -8F, -8F, 16, 4, 16);
		basefront.rotationPointX = 8;
		basefront.rotationPointY = 8;
		basefront.rotationPointZ = 8;

		baseback = new ModelRenderer(model, 0, 0);
		baseback.addBox(-8F, 4F, -8F, 16, 4, 16);
		baseback.rotationPointX = 8;
		baseback.rotationPointY = 8;
		baseback.rotationPointZ = 8;

		resourceTank = new ModelRenderer(model, 0, 0);
		resourceTank.addBox(-6F, -8F, -6F, 12, 16, 6);
		resourceTank.rotationPointX = 8;
		resourceTank.rotationPointY = 8;
		resourceTank.rotationPointZ = 8;

		productTank = new ModelRenderer(model, 0, 0);
		productTank.addBox(-6F, -8F, 0F, 12, 16, 6);
		productTank.rotationPointX = 8;
		productTank.rotationPointY = 8;
		productTank.rotationPointZ = 8;

	}

	public RenderMachine(String baseTexture) {
		this();
		this.gfxBase = baseTexture;
	}

	public RenderMachine(String baseTexture, boolean resourceLevel, boolean productLevel) {
		this(baseTexture);
		this.resourceLevel = resourceLevel;
		this.productLevel = productLevel;
	}

	/**
	 * Used to preload textures for Minecraft Forge
	 * 
	 * @param gfxBase
	 */
	public void preloadTextures() {
		ForestryClient.preloadTexture(gfxBase + "base.png");

		ForestryClient.preloadTexture(gfxBase + "tank_resource_empty.png");
		if (resourceLevel) {
			ForestryClient.preloadTexture(gfxBase + "tank_resource_low.png");
			ForestryClient.preloadTexture(gfxBase + "tank_resource_medium.png");
			ForestryClient.preloadTexture(gfxBase + "tank_resource_high.png");
			ForestryClient.preloadTexture(gfxBase + "tank_resource_maximum.png");
		}

		ForestryClient.preloadTexture(gfxBase + "tank_product_empty.png");
		if (productLevel) {
			ForestryClient.preloadTexture(gfxBase + "tank_product_low.png");
			ForestryClient.preloadTexture(gfxBase + "tank_product_medium.png");
			ForestryClient.preloadTexture(gfxBase + "tank_product_high.png");
			ForestryClient.preloadTexture(gfxBase + "tank_product_maximum.png");
		}
	}

	public void inventoryRender(double x, double y, double z, float f, float f1) {
		render(EnumTankLevel.EMPTY, EnumTankLevel.EMPTY, ForgeDirection.UP, x, y, z);
	}

	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {

		if(tileentity instanceof TileMachine) {
			TileMachine processor = (TileMachine) tileentity;
			if (processor.machine == null)
				return;
			render(processor.machine.getPrimaryLevel(), processor.machine.getSecondaryLevel(), processor.getOrientation(), d, d1, d2);
		} else {
			MachineGenerator generator = (MachineGenerator)tileentity;
			render(generator.getPrimaryLevel(), EnumTankLevel.EMPTY, generator.getOrientation(), d, d1, d2);
		}

	}
	
	private void render(EnumTankLevel waterLevel, EnumTankLevel melangeLevel, ForgeDirection orientation, double x, double y, double z) {
		render(waterLevel.ordinal(), melangeLevel.ordinal(), orientation, x, y, z);
	}

	public void render(int waterLevelInt, int melangeLevelInt, ForgeDirection orientation, double x, double y, double z) {

		EnumTankLevel waterLevel = EnumTankLevel.values()[waterLevelInt];
		EnumTankLevel melangeLevel = EnumTankLevel.values()[melangeLevelInt];
		
		GL11.glPushMatrix();
		// GL11.glDisable(2896 /* GL_LIGHTING */);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glTranslatef((float) x, (float) y, (float) z);

		float[] angle = { 0, 0, 0 };
		float[] translate = { 0, 0, 0 };

		if (orientation == null) {
			orientation = ForgeDirection.WEST;
		}
		switch (orientation) {
		case EAST:
			// angle [2] = (float) Math.PI / 2;
			angle[1] = (float) Math.PI;
			angle[2] = (float) -Math.PI / 2;
			translate[0] = 1;
			break;
		case WEST:
			// 2, -PI/2
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
			angle[2] = (float) Math.PI / 2;
			translate[2] = 1;
			break;
		case NORTH:
		default:
			angle[0] = (float) -Math.PI / 2;
			angle[2] = (float) Math.PI / 2;
			translate[2] = -1;
			break;
		}

		basefront.rotateAngleX = angle[0];
		basefront.rotateAngleY = angle[1];
		basefront.rotateAngleZ = angle[2];

		baseback.rotateAngleX = angle[0];
		baseback.rotateAngleY = angle[1];
		baseback.rotateAngleZ = angle[2];

		resourceTank.rotateAngleX = angle[0];
		resourceTank.rotateAngleY = angle[1];
		resourceTank.rotateAngleZ = angle[2];

		productTank.rotateAngleX = angle[0];
		productTank.rotateAngleY = angle[1];
		productTank.rotateAngleZ = angle[2];

		float factor = (float) (1.0 / 16.0);

		Proxies.common.bindTexture(gfxBase + "base.png");
		basefront.render(factor);

		Proxies.common.bindTexture(gfxBase + "base.png");
		baseback.render(factor);

		String texture;

		switch (waterLevel) {
		case LOW:
			texture = gfxBase + "tank_resource_low.png";
			break;
		case MEDIUM:
			texture = gfxBase + "tank_resource_medium.png";
			break;
		case HIGH:
			texture = gfxBase + "tank_resource_high.png";
			break;
		case MAXIMUM:
			texture = gfxBase + "tank_resource_maximum.png";
			break;
		case EMPTY:
		default:
			texture = gfxBase + "tank_resource_empty.png";
			break;
		}
		Proxies.common.bindTexture(texture);
		resourceTank.render(factor);

		switch (melangeLevel) {
		case LOW:
			texture = gfxBase + "tank_product_low.png";
			break;
		case MEDIUM:
			texture = gfxBase + "tank_product_medium.png";
			break;
		case HIGH:
			texture = gfxBase + "tank_product_high.png";
			break;
		case MAXIMUM:
			texture = gfxBase + "tank_product_maximum.png";
			break;
		case EMPTY:
		default:
			texture = gfxBase + "tank_product_empty.png";
			break;
		}
		Proxies.common.bindTexture(texture);
		productTank.render(factor);

		// GL11.glEnable(2896 /* GL_LIGHTING */);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}
