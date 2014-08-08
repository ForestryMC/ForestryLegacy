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
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import forestry.core.proxy.Proxies;

public class ModelDisplaybox extends ModelBase {

	private String gfxBase;

	private ModelRenderer pedestal;
	private ModelRenderer box;

	public ModelDisplaybox(String gfxBase) {

		this.gfxBase = gfxBase;

		pedestal = new ModelRenderer(this, 0, 0);
		pedestal.addBox(-8F, -8F, -8F, 16, 4, 16);
		pedestal.setRotationPoint(8, 8, 8);

		box = new ModelRenderer(this, 0, 0);
		box.addBox(-4F, -4F, -4F, 16, 12, 16);
		box.setRotationPoint(8, 8, 8);
	}

	public void render(ForgeDirection orientation, float posX, float posY, float posZ) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glTranslatef(posX, posY, posZ);
		float[] angle = { 0, 0, 0 };

		float factor = (float) (1.0 / 16.0);

		pedestal.rotateAngleX = angle[0];
		pedestal.rotateAngleY = angle[1];
		pedestal.rotateAngleZ = angle[2];
		Proxies.common.bindTexture(gfxBase + "pedestal.png");
		pedestal.render(factor);

		box.rotateAngleX = angle[0];
		box.rotateAngleY = angle[1];
		box.rotateAngleZ = angle[2];
		Proxies.common.bindTexture(gfxBase + "box.png");
		box.render(factor);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();

	}
}
