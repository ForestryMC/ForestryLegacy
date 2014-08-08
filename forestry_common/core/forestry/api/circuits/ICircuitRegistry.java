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
package forestry.api.circuits;

import java.util.HashMap;

import net.minecraft.world.World;

public interface ICircuitRegistry {

	/* CIRCUITS */
	HashMap<String, ICircuit> getRegisteredCircuits();

	void registerCircuit(ICircuit circuit);

	ICircuit getCircuit(String uid);

	ICircuitLibrary getCircuitLibrary(World world, String playername);

	void registerLegacyMapping(int id, String uid);

	ICircuit getFromLegacyMap(int id);

	/* LAYOUTS */
	HashMap<String, ICircuitLayout> getRegisteredLayouts();

	void registerLayout(ICircuitLayout layout);

	ICircuitLayout getLayout(String uid);

	ICircuitLayout getDefaultLayout();

}
