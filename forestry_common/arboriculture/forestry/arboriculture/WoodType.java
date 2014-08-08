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

import net.minecraft.nbt.NBTTagCompound;

public enum WoodType {
	LARCH(0), TEAK(1), ACACIA(2), LIME(3), CHESTNUT(4), WENGE(5), BAOBAB(6), SEQUOIA(7), KAPOK(8), EBONY(9), MAHOGANY(10), BALSA(11), WILLOW(12), WALNUT(13), GREENHEART(14, 7.5f), CHERRY(15);
	
	private int baseIndex = 0;
	private float hardness = 2.0f;
	
	private WoodType(int plankIndex, float hardness) {
		this(plankIndex);
		this.hardness = hardness; 
	}
	
	private WoodType(int plankIndex) {
		this.baseIndex = plankIndex;
	}
	
	public int getPlankIndex() {
		return this.baseIndex;
	}
	
	public float getHardness() {
		return hardness;
	}
	
	public void saveToCompound(NBTTagCompound compound) {
		compound.setInteger("WoodType", this.ordinal());
	}
	
	public static WoodType getFromCompound(NBTTagCompound compound) {
		
		if(compound != null) {
			int typeOrdinal = compound.getInteger("WoodType");
			if(typeOrdinal < WoodType.values().length)
				return WoodType.values()[typeOrdinal];
		}

		return WoodType.LARCH;
	}

}
