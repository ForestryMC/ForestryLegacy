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
package forestry.api.farming;

import java.util.Collection;
import java.util.HashMap;

public class Farmables {
	/**
	 * Can be used to add IFarmables to some of the vanilla farm logics.
	 * 
	 * Identifiers:
	 * farmArboreal
	 * farmWheat
	 * farmGourd
	 * farmInfernal
	 * farmPoales
	 * farmSucculentes
	 * farmVegetables
	 * farmShroom
	 */
	public static HashMap<String, Collection<IFarmable>> farmables = new HashMap<String, Collection<IFarmable>>();
	
	public static IFarmInterface farmInterface;
}
