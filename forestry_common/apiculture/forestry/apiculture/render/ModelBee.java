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
/*
package forestry.apiculture.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;
public class ModelBee extends ModelBase {
	private ModelRenderer snout;
	private ModelRenderer body;
	private ModelRenderer wingRight;
	private ModelRenderer wingLeft;

	public ModelBee() {
		this.snout = new ModelRenderer(this, 0, 0);
		this.snout.addBox(0.5f, 0.5f, 3.0f, 1, 1, 1);
		this.snout.setRotationPoint(0.0f, 0.0f, 0.0f);

		this.body = new ModelRenderer(this, 0, 6);
		this.body.addBox(0.0f, 0.0f, 0.0f, 2, 2, 3);
		this.body.setRotationPoint(0.0f, 0.0f, 0.0f);

		this.wingRight = new ModelRenderer(this, 8, 0);
		this.wingRight.addBox(0.0f, 0.0f, 0.0f, 1, 2, 2);
		this.wingRight.setRotationPoint(0.0f, 0.0f, 0.0f);

		this.wingLeft = new ModelRenderer(this, 8, 0);
		this.wingLeft.addBox(-1.0f, 0.0f, 0.0f, 1, 2, 2);
		this.wingLeft.setRotationPoint(2.0f, 0.0f, 0.0f);
	}

	@Override
	public void render(Entity entity, float posX, float posY, float posZ, float par5, float par6, float par7) {

		this.setRotationAngles(posX, posY, posZ, par5, par6, par7);

		GL11.glPushMatrix();
		GL11.glDisable(2896 /* GL_LIGHTING *///);
/*
 GL11.glTranslatef(posX, posY, posZ);
 float factor = (float) (1.0 / 16.0);

 snout.render(factor);
 body.render(factor);
 wingRight.render(factor);
 wingLeft.render(factor);

 GL11.glEnable(2896 /* GL_LIGHTING *///);
/*
GL11.glPopMatrix();
}

@Override
public void setLivingAnimations(EntityLiving entity, float par2, float par3, float par4) {
this.wingRight.rotateAngleZ = 180f - RenderManager.instance.playerViewY;
this.wingLeft.rotateAngleZ = -1.0f * (180f - RenderManager.instance.playerViewY);
}

@Override
public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6) {
this.snout.setRotationPoint(par4, par5, par6);
this.body.setRotationPoint(par4, par5, par6);
this.wingRight.setRotationPoint(par4, par5, par6);
this.wingLeft.setRotationPoint(2.0f + par4, par5, par6);
}
}
 */
