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
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import forestry.core.ForestryClient;
import forestry.core.gadgets.TileMachine;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.proxy.Proxies;

public class RenderBlock implements IBlockRenderer {

	private ModelBase model = new ModelBase() {
	};
	private String gfxBase;

	private ModelRenderer block;

	public RenderBlock() {
		block = new ModelRenderer(model, 0, 0);
		block.addBox(-8F, -8F, -8F, 16, 16, 16);
		block.rotationPointX = 8;
		block.rotationPointY = 8;
		block.rotationPointZ = 8;
	}

	public RenderBlock(String gfxBase) {
		this();
		this.gfxBase = gfxBase;
	}

	@Override
	public void preloadTextures() {
		ForestryClient.preloadTexture(gfxBase + "block.png");
	}

	@Override
	public void inventoryRender(double x, double y, double z, float f, float f1) {
		render(ForgeDirection.EAST, gfxBase, x, y, z);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {

		TileMachine machine = (TileMachine) tileentity;
		render(machine.getOrientation(), gfxBase, d, d1, d2);
	}

	private void render(ForgeDirection orientation, String gfxBase2, double x, double y, double z) {

		GL11.glPushMatrix();
		GL11.glDisable(2896 /* GL_LIGHTING */);

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

		block.rotateAngleX = angle[0];
		block.rotateAngleY = angle[1];
		block.rotateAngleZ = angle[2];

		float factor = (float) (1.0 / 16.0);

		Proxies.common.bindTexture(gfxBase + "block.png");
		block.render(factor);

		GL11.glEnable(2896 /* GL_LIGHTING */);
		GL11.glPopMatrix();
	}

}
