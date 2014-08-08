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

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import forestry.core.proxy.Proxies;

public class EntitySnowFX extends EntityFX {

	public int blendmode = 1;
	private String texture = "/gfx/forestry/particles/snow.png";
	private int colour = 0xffffff;

	public EntitySnowFX(World world, double x, double y, double z, float motionScaleX, float motionScaleY, float motionScaleZ) {

		super(world, x, y, z, 0.0D, 0.0D, 0.0D);

		particleRed = (colour >> 16 & 255) / 255.0F;
		particleGreen = (colour >> 8 & 255) / 255.0F;
		particleBlue = (colour & 255) / 255.0F;

		this.setParticleTextureIndex(1);
		this.setSize(0.1F, 0.1F);
		this.particleScale *= 0.2F;
		this.particleMaxAge = (int) (20.0D / (Math.random() * 0.8D + 0.2D));
		this.noClip = true;

		this.motionX *= 0.009999999552965164D;
		this.motionY *= -0.09999999552965164D;
		this.motionZ *= 0.009999999552965164D;

	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 1.02D;
		this.motionY *= 1.08D;
		this.motionZ *= 1.02D;

		if (this.particleMaxAge-- <= 0) {
			this.setDead();
		}

	}

	public EntitySnowFX setTexture(String texture) {
		this.texture = texture;
		return this;
	}

	public void renderParticle(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5) {

		tessellator.draw();
		GL11.glPushMatrix();

		GL11.glDepthMask(false);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, blendmode);

		Proxies.common.bindTexture(texture);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		float f10 = 0.1F * particleScale;
		float f11 = (float) ((prevPosX + (posX - prevPosX) * f) - interpPosX);
		float f12 = (float) ((prevPosY + (posY - prevPosY) * f) - interpPosY);
		float f13 = (float) ((prevPosZ + (posZ - prevPosZ) * f) - interpPosZ);

		GL11.glRotatef((float) motionY, 0.0F, 1.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.setBrightness(0x0000f0);

		tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, 1.0F);
		tessellator.addVertexWithUV(f11 - f1 * f10 - f4 * f10, f12 - f2 * f10, f13 - f3 * f10 - f5 * f10, 0, 1);
		tessellator.addVertexWithUV((f11 - f1 * f10) + f4 * f10, f12 + f2 * f10, (f13 - f3 * f10) + f5 * f10, 1, 1);
		tessellator.addVertexWithUV(f11 + f1 * f10 + f4 * f10, f12 + f2 * f10, f13 + f3 * f10 + f5 * f10, 1, 0);
		tessellator.addVertexWithUV((f11 + f1 * f10) - f4 * f10, f12 - f2 * f10, (f13 + f3 * f10) - f5 * f10, 0, 0);

		tessellator.draw();

		GL11.glDisable(3042);
		GL11.glDepthMask(true);

		GL11.glPopMatrix();
		GL11.glBindTexture(3553 /* GL_TEXTURE_2D *//* GL_TEXTURE_2D */, Proxies.common.getClientInstance().renderEngine.getTexture("/particles.png"));
		tessellator.startDrawingQuads();
	}

}
