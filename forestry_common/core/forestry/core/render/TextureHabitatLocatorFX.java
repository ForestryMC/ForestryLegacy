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

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.util.ChunkCoordinates;
import cpw.mods.fml.client.FMLTextureFX;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;

public class TextureHabitatLocatorFX extends FMLTextureFX {

	public static TextureHabitatLocatorFX instance;

	/** A reference to the Minecraft object. */
	private Minecraft minecraft;

	/** Holds the image of the compass from items.png in rgb format. */
	private int[] compassIconImageData;
	private double field_4229_i;
	private double field_4228_j;

	private ChunkCoordinates targetBiome;

	public TextureHabitatLocatorFX(Minecraft minecraft) {

		super(ForestryItem.biomeFinder.getIconFromDamage(0));
		this.minecraft = minecraft;
		this.tileImage = 1;
		instance = this;

		setup();
	}

	@Override
	public void setup() {

		super.setup();
		compassIconImageData = new int[tileSizeSquare];

		try {

			BufferedImage bufferImage = ImageIO.read(Proxies.common.getSelectedTexturePack(minecraft).getResourceAsStream(Defaults.TEXTURE_ITEMS));
			int iconX = this.iconIndex % 16 * tileSizeBase;
			int iconY = this.iconIndex / 16 * tileSizeBase;
			bufferImage.getRGB(iconX, iconY, tileSizeBase, tileSizeBase, this.compassIconImageData, 0, tileSizeBase);

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void bindImage(RenderEngine renderengine) {
		Proxies.common.bindTexture(Defaults.TEXTURE_ITEMS);
	}

	@Override
	public void onTick() {

		for (int i = 0; i < tileSizeSquare; ++i) {
			int var2 = this.compassIconImageData[i] >> 24 & 255;
			int var3 = this.compassIconImageData[i] >> 16 & 255;
			int var4 = this.compassIconImageData[i] >> 8 & 255;
			int var5 = this.compassIconImageData[i] >> 0 & 255;

			if (this.anaglyphEnabled) {
				int var6 = (var3 * 30 + var4 * 59 + var5 * 11) / 100;
				int var7 = (var3 * 30 + var4 * 70) / 100;
				int var8 = (var3 * 30 + var5 * 70) / 100;
				var3 = var6;
				var4 = var7;
				var5 = var8;
			}

			this.imageData[i * 4 + 0] = (byte) var3;
			this.imageData[i * 4 + 1] = (byte) var4;
			this.imageData[i * 4 + 2] = (byte) var5;
			this.imageData[i * 4 + 3] = (byte) var2;
		}

		double direction = 0.0D;

		if (minecraft.theWorld != null && this.minecraft.thePlayer != null) {

			ChunkCoordinates coordinates;
			if (targetBiome != null) {
				coordinates = targetBiome;
				double var23 = coordinates.posX - this.minecraft.thePlayer.posX;
				double var25 = coordinates.posZ - this.minecraft.thePlayer.posZ;
				direction = (this.minecraft.thePlayer.rotationYaw - 90.0F) * Math.PI / 180.0D - Math.atan2(var25, var23);
			} else {
				// No target has the finder spinning wildly.
				direction = Math.random() * Math.PI * 2.0D;
			}

		}

		double var22;

		for (var22 = direction - this.field_4229_i; var22 < -Math.PI; var22 += (Math.PI * 2D)) {
			;
		}

		while (var22 >= Math.PI) {
			var22 -= (Math.PI * 2D);
		}

		if (var22 < -1.0D) {
			var22 = -1.0D;
		}

		if (var22 > 1.0D) {
			var22 = 1.0D;
		}

		this.field_4228_j += var22 * 0.1D;
		this.field_4228_j *= 0.8D;
		this.field_4229_i += this.field_4228_j;
		double var24 = Math.sin(this.field_4229_i);
		double var26 = Math.cos(this.field_4229_i);
		int var9;
		int var10;
		int var11;
		int var12;
		int var13;
		int var14;
		int var15;
		int var17;
		short var16;
		int var19;
		int var18;

		for (var9 = -4; var9 <= 4; ++var9) {
			var10 = (int) ((tileSizeBase >> 1) + 0.5D + var26 * var9 * 0.3D);
			var11 = (int) ((tileSizeBase >> 1) - 0.5D - var24 * var9 * 0.3D * 0.5D);
			var12 = var11 * tileSizeBase + var10;
			var13 = 100;
			var14 = 100;
			var15 = 100;
			var16 = 255;

			if (this.anaglyphEnabled) {
				var17 = (var13 * 30 + var14 * 59 + var15 * 11) / 100;
				var18 = (var13 * 30 + var14 * 70) / 100;
				var19 = (var13 * 30 + var15 * 70) / 100;
				var13 = var17;
				var14 = var18;
				var15 = var19;
			}

			this.imageData[var12 * 4 + 0] = (byte) var13;
			this.imageData[var12 * 4 + 1] = (byte) var14;
			this.imageData[var12 * 4 + 2] = (byte) var15;
			this.imageData[var12 * 4 + 3] = (byte) var16;
		}

		for (var9 = -(tileSizeBase >> 2); var9 <= tileSizeBase; ++var9) {
			var10 = (int) ((tileSizeBase >> 1) + 0.5D + var24 * var9 * 0.3D);
			var11 = (int) ((tileSizeBase >> 1) - 0.5D + var26 * var9 * 0.3D * 0.5D);
			var12 = var11 * tileSizeBase + var10;
			var13 = var9 >= 0 ? 255 : 100;
			var14 = var9 >= 0 ? 20 : 100;
			var15 = var9 >= 0 ? 20 : 100;
			var16 = 255;

			if (this.anaglyphEnabled) {
				var17 = (var13 * 30 + var14 * 59 + var15 * 11) / 100;
				var18 = (var13 * 30 + var14 * 70) / 100;
				var19 = (var13 * 30 + var15 * 70) / 100;
				var13 = var17;
				var14 = var18;
				var15 = var19;
			}

			this.imageData[var12 * 4 + 0] = (byte) var13;
			this.imageData[var12 * 4 + 1] = (byte) var14;
			this.imageData[var12 * 4 + 2] = (byte) var15;
			this.imageData[var12 * 4 + 3] = (byte) var16;
		}
	}

	public void setTargetCoordinates(ChunkCoordinates coordinates) {
		this.targetBiome = coordinates;
	}

}
