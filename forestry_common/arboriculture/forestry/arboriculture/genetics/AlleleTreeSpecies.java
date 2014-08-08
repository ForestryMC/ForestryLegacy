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
package forestry.arboriculture.genetics;

import java.util.ArrayList;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.EnumPlantType;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IFruitFamily;
import forestry.api.world.ITreeGenData;
import forestry.arboriculture.worldgen.WorldGenArboriculture;
import forestry.arboriculture.worldgen.WorldGenBalsa;
import forestry.core.genetics.AlleleSpecies;

public class AlleleTreeSpecies extends AlleleSpecies implements IAlleleTreeSpecies {

	private Class<? extends WorldGenArboriculture> generatorClass = WorldGenBalsa.class;
	
	private int iconIndexFancy = 48;
	private int iconIndexPlain = 64;
	private int iconIndexChanged = 80;
	
	private int girth = 1;
	private EnumPlantType nativeType = EnumPlantType.Plains;
	private ArrayList<IFruitFamily> fruits = new ArrayList<IFruitFamily>();

	public AlleleTreeSpecies(String uid, boolean isDominant, String name, IClassification branch, String binomial, int primaryColor, int secondaryColor) {
		super(uid, isDominant, name, branch, binomial, primaryColor, secondaryColor);
	}

	public AlleleTreeSpecies(String uid, boolean isDominant, String name, IClassification branch, String binomial, int primaryColor, int secondaryColor,
			Class<? extends WorldGenArboriculture> generator) {
		super(uid, isDominant, name, branch, binomial, primaryColor, secondaryColor);
		this.generatorClass = generator;
	}
	
	public AlleleTreeSpecies setPlantType(EnumPlantType type) {
		this.nativeType = type;
		return this;
	}
	
	public AlleleTreeSpecies setGirth(int girth) {
		this.girth = girth;
		return this;
	}
	
	public AlleleTreeSpecies addFruitFamily(IFruitFamily family) {
		fruits.add(family);
		return this;
	}
	
	public AlleleTreeSpecies setLeafIndices(int fancy, int plain, int changed) {
		this.iconIndexFancy = fancy;
		this.iconIndexPlain = plain;
		this.iconIndexChanged = changed;
		return this;
	}
	
	@Override
	public EnumPlantType getPlantType() {
		return nativeType;
	}
	
	@Override
	public ArrayList<IFruitFamily> getSuitableFruit() {
		return fruits;
	}
	
	@Override
	public int getGirth() {
		return girth;
	}

	@Override
	public WorldGenerator getGenerator(ITree tree, World world, int x, int y, int z) {
		try {
			return generatorClass.getConstructor(new Class[] { ITreeGenData.class }).newInstance(new Object[] { tree });
		} catch (Exception ex) {
			throw new RuntimeException("Failed to instantiate generator of class " + generatorClass.getName());
		}
	}

	@Override
	public Class<? extends WorldGenerator>[] getGeneratorClasses() {
		return new Class[] { generatorClass };
	}

	@Override
	public int getLeafTextureIndex(ITree tree, boolean fancy) {
		
		if(!fancy)
			return iconIndexPlain;
		
		if(tree.getMate() != null)
			return iconIndexChanged;
		
		return iconIndexFancy;
	}
	
	@Override
	public int getGermlingIconIndex(EnumGermlingType type) {
		return getBodyType();
	}

	public AlleleTreeSpecies setGenerator(Class<? extends WorldGenArboriculture> generatorClass) {
		this.generatorClass = generatorClass;
		return this;
	}

}
