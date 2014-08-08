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
package forestry.api.world;

import net.minecraft.world.World;

public interface ITreeGenData {
	
	int getGirth(World world, int x, int y, int z);
	
	float getHeightModifier();
	
	boolean canGrow(World world, int x, int y, int z, int expectedGirth, int expectedHeight);
	
	void setLeaves(World world, int x, int y, int z);
}
