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
package forestry.arboriculture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.IFruitFamily;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class FruitProviderRipening extends FruitProviderNone {

	String key;
	IFruitFamily family;

	HashMap<ItemStack, Float> products = new HashMap<ItemStack, Float>();
	int textureIndex;
	int ripeningPeriod = 10;
	
	int colourRipe = 0xffffff;
	int colourCallow = 0xffffff;
	
	int diffR, diffG, diffB = 0;

	public FruitProviderRipening(String key, IFruitFamily family, ItemStack product, float modifier, int textureIndex) {
		this.key = key;
		this.family = family;
		products.put(product, modifier);
		this.textureIndex = textureIndex;
	}
	
	public FruitProviderRipening setColours(int ripe, int callow) {
		colourRipe = ripe;
		colourCallow = callow;
		
        diffR = (ripe >> 16 & 255) - (callow >> 16 & 255);
        diffG = (ripe >> 8 & 255) - (callow >> 8 & 255);
        diffB = (ripe & 255) - (callow & 255);

		return this;
	}
	
	public FruitProviderRipening setRipeningPeriod(int period) {
		ripeningPeriod = period;
		return this;
	}
	
	@Override
	public IFruitFamily getFamily() {
		return family;
	}

	private float getRipeningStage(int ripeningTime) {
		if(ripeningTime >= ripeningPeriod)
			return 1.0f;
		
		return (float)ripeningTime / ripeningPeriod;
	}
	
	@Override
	public ItemStack[] getFruits(ITreeGenome genome, World world, int x, int y, int z, int ripeningTime) {
		ArrayList<ItemStack> product = new ArrayList<ItemStack>();
		
		float stage = getRipeningStage(ripeningTime);
		if(stage < 0.5f)
			return new ItemStack[0];
		
		for(Map.Entry<ItemStack, Float> entry : products.entrySet()) {
			if(world.rand.nextFloat() <= genome.getYield() * entry.getValue() * 5.0f * stage)
				product.add(entry.getKey().copy());
		}
		
		return product.toArray(new ItemStack[0]);
	}

	@Override
	public ItemStack[] getProducts() {
		return products.keySet().toArray(new ItemStack[0]);
	}

	@Override
	public ItemStack[] getSpecialty() {
		return new ItemStack[0];
	}

	@Override
	public int getRipeningPeriod() {
		return ripeningPeriod;
	}

	@Override
	public boolean markAsFruitLeaf(ITreeGenome genome, World world, int x, int y, int z) {
		return true;
	}

	@Override
	public int getColour(ITreeGenome genome, IBlockAccess world, int x, int y, int z, int ripeningTime) {
		float stage = getRipeningStage(ripeningTime);
		
        int r = (colourCallow >> 16 & 255) + (int)(diffR*stage);
        int g = (colourCallow >> 8 & 255) + (int)(diffG*stage);
        int b = (colourCallow & 255) + (int)(diffB*stage);

        //System.out.println(String.format("Calcultated rgb %s/%s/%s from %s and %s, resulting in %s",
        //		r, g, b, colourCallow, stage, (r & 255) << 16 | (g & 255) << 8 | b & 255));
		return (r & 255) << 16 | (g & 255) << 8 | b & 255;
	}

	@Override
	public String getDescription() {
		return "fruits." + key;
	}

	@Override
	public int getTextureIndex(ITreeGenome genome, IBlockAccess world, int x, int y, int z, int ripeningTime, boolean fancy) {
		return textureIndex;
	}

}
