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

import net.minecraft.client.renderer.RenderEngine;
import cpw.mods.fml.client.FMLTextureFX;
import forestry.core.proxy.Proxies;

public class TextureLiquidsFX extends FMLTextureFX {

	private final String texture;
	private final int rMin, rMax, gMin, gMax, bMin, bMax;

	protected float[] red;
	protected float[] green;
	protected float[] blue;
	protected float[] alpha;

	public TextureLiquidsFX(int rMin, int rMax, int gMin, int gMax, int bMin, int bMax, int iconIndex, String textureFile) {
		super(iconIndex);
		texture = textureFile;

		this.rMin = rMin;
		this.rMax = rMax;
		this.gMin = gMin;
		this.gMax = gMax;
		this.bMin = bMin;
		this.bMax = bMax;

		setup();
	}

	@Override
	public void setup() {
		super.setup();

		red = new float[tileSizeSquare];
		green = new float[tileSizeSquare];
		blue = new float[tileSizeSquare];
		alpha = new float[tileSizeSquare];
	}

	@Override
	public void bindImage(RenderEngine renderengine) {
		Proxies.common.bindTexture(texture);
	}

	@Override
	public void onTick() {

		for (int i = 0; i < tileSizeBase; ++i) {
			for (int var2 = 0; var2 < tileSizeBase; ++var2) {
				float var3 = 0.0F;

				for (int j = i - 1; j <= i + 1; ++j) {
					int r = j & tileSizeMask;
					int g = var2 & tileSizeMask;
					var3 += this.red[r + g * tileSizeBase];
				}

				this.green[i + var2 * tileSizeBase] = var3 / 3.3F + this.blue[i + var2 * tileSizeBase] * 0.8F;
			}
		}

		for (int i = 0; i < tileSizeBase; ++i) {
			for (int j = 0; j < tileSizeBase; ++j) {
				this.blue[i + j * tileSizeBase] += this.alpha[i + j * tileSizeBase] * 0.05F;

				if (this.blue[i + j * tileSizeBase] < 0.0F) {
					this.blue[i + j * tileSizeBase] = 0.0F;
				}

				this.alpha[i + j * tileSizeBase] -= 0.1F;

				if (Math.random() < 0.05D) {
					this.alpha[i + j * tileSizeBase] = 0.5F;
				}
			}
		}

		float[] var12 = this.green;
		this.green = this.red;
		this.red = var12;

		for (int i = 0; i < tileSizeSquare; ++i) {
			float var3 = this.red[i];

			if (var3 > 1.0F) {
				var3 = 1.0F;
			}

			if (var3 < 0.0F) {
				var3 = 0.0F;
			}

			float var13 = var3 * var3;
			int r = (int) (rMin + var13 * (rMax - rMin));
			int g = (int) (gMin + var13 * (gMax - gMin));
			int b = (int) (bMin + var13 * (bMax - bMin));
			;
			int a = (int) (146.0F + var13 * 50.0F);

			if (this.anaglyphEnabled) {
				int var9 = (r * 30 + g * 59 + b * 11) / 100;
				int var10 = (r * 30 + g * 70) / 100;
				int var11 = (r * 30 + b * 70) / 100;
				r = var9;
				g = var10;
				b = var11;
			}

			this.imageData[i * 4 + 0] = (byte) r;
			this.imageData[i * 4 + 1] = (byte) g;
			this.imageData[i * 4 + 2] = (byte) b;
			this.imageData[i * 4 + 3] = (byte) a;

		}
	}

}
