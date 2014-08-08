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
package forestry.api.apiculture;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IGenome;

/**
 * Only the default implementation is supported.
 * 
 * @author SirSengir
 * 
 */
public interface IBeeGenome extends IGenome {

	IAlleleBeeSpecies getPrimaryAsBee();

	IAlleleBeeSpecies getSecondaryAsBee();

	float getSpeed();

	int getLifespan();

	int getFertility();

	boolean getNocturnal();

	boolean getTolerantFlyer();

	boolean getCaveDwelling();

	IFlowerProvider getFlowerProvider();

	int getFlowering();

	int[] getTerritory();

	IAlleleBeeEffect getEffect();

	EnumTolerance getToleranceTemp();

	EnumTolerance getToleranceHumid();

}
