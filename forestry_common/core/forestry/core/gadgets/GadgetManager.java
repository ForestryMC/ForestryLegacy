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
package forestry.core.gadgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import forestry.core.interfaces.IEntityHandler;
import forestry.core.utils.StructureBlueprint;

public class GadgetManager {

	static HashMap<Integer, MachinePackage> growerPackagesByMeta = new HashMap<Integer, MachinePackage>();
	static HashMap<Integer, MachinePackage> machinePackagesByMeta = new HashMap<Integer, MachinePackage>();
	static HashMap<Integer, MachinePackage> harvesterPackagesByMeta = new HashMap<Integer, MachinePackage>();
	static HashMap<Integer, MachinePackage> planterPackagesByMeta = new HashMap<Integer, MachinePackage>();

	static ArrayList<IEntityHandler> entityHandler = new ArrayList<IEntityHandler>();

	/**
	 * You need to call this after you registered all your {@link MachinePackage}, {@link PlanterPackage}, {@link HarvesterPackage} and {@link GrowerPackage}
	 * objects. If you forget to call this function, your machines will be missing names and tags in game.
	 */
	public static void registerAllPackageNames() {
		for (IEntityHandler handler : entityHandler) {
			handler.registerAllPackageNames();
		}
	}

	/**
	 * Use to register a new {@link MachinePackage} with Forestry
	 * 
	 * @param meta
	 *            Metadata to use for the new machine. Valid values are 0-15 with 0-8 reserved for Forestry's own machines
	 * @param pack
	 *            {@link MachinePackage} containing the necessary information for the new machine.
	 * @return true if the package was successfully added, false otherwise.
	 */
	public static boolean registerMachinePackage(int meta, MachinePackage pack) {
		// Key already taken, skip adding this planter
		if (machinePackagesByMeta.containsKey(meta))
			return false;

		machinePackagesByMeta.put(meta, pack);
		for (IEntityHandler handler : entityHandler) {
			handler.registerMachine(meta, pack);
		}
		return true;
	}

	/**
	 * Use to register a new {@link MachinePackage} for harvesters with Forestry
	 * 
	 * @param meta
	 *            Metadata to use for the new machine. Valid values are 0-15 with 0-8 reserved for Forestry's own machines
	 * @param pack
	 *            {@link MachinePackage} containing the necessary information for the new harvester.
	 * @return true if the package was successfully added, false otherwise.
	 */
	public static boolean registerHarvesterPackage(int meta, MachinePackage pack) {
		// Key already taken, skip adding this planter
		if (harvesterPackagesByMeta.containsKey(meta))
			return false;

		harvesterPackagesByMeta.put(meta, pack);
		for (IEntityHandler handler : entityHandler) {
			handler.registerHarvester(meta, pack);
		}
		return true;
	}

	/**
	 * Use to register a new {@link MachinePackage} for planters with Forestry
	 * 
	 * @param meta
	 *            Metadata to use for the new machine. Valid values are 0-15 with 0-8 reserved for Forestry's own machines
	 * @param pack
	 *            {@link MachinePackage} containing the necessary information for the new planter.
	 * @return true if the package was successfully added, false otherwise.
	 */
	public static boolean registerPlanterPackage(int meta, MachinePackage pack) {
		// Key already taken, skip adding this planter
		if (planterPackagesByMeta.containsKey(meta))
			return false;

		// Register blueprints
		Iterator<StructureBlueprint> it = pack.blueprints.iterator();
		while (it.hasNext()) {
			StructureBlueprint blueprint = it.next();
			StructureBlueprint.index.put(blueprint.id, blueprint);
		}

		planterPackagesByMeta.put(meta, pack);
		for (IEntityHandler handler : entityHandler) {
			handler.registerPlanter(meta, pack);
		}
		return true;
	}

	/**
	 * Use to register a new {@link MachinePackage} for mills with Forestry
	 * 
	 * @param meta
	 *            Metadata to use for the new machine. Valid values are 0-15 with 0-8 reserved for Forestry's own machines
	 * @param pack
	 *            {@link MachinePackage} containing the necessary information for the new mill.
	 * @return true if the package was successfully added, false otherwise.
	 */
	public static boolean registerMillPackage(int meta, MachinePackage pack) {
		// Key already taken, skip adding this grower
		if (growerPackagesByMeta.containsKey(meta))
			return false;

		growerPackagesByMeta.put(meta, pack);
		for (IEntityHandler handler : entityHandler) {
			handler.registerMill(meta, pack);
		}
		return true;
	}

	public static void registerEntityHandler(IEntityHandler handler) {
		entityHandler.add(handler);
	}

	public static Iterator<Integer> getMachineKeyIterator() {
		return machinePackagesByMeta.keySet().iterator();
	}

	public static MachinePackage getMachinePackage(int meta) {
		return machinePackagesByMeta.get(meta);
	}

	public static boolean hasMachinePackage(int meta) {
		return machinePackagesByMeta.containsKey(meta);
	}

	public static Iterator<Integer> getHarvesterKeyIterator() {
		return harvesterPackagesByMeta.keySet().iterator();
	}

	public static MachinePackage getHarvesterPackage(int meta) {
		return harvesterPackagesByMeta.get(meta);
	}

	public static boolean hasHarvesterPackage(int meta) {
		return harvesterPackagesByMeta.containsKey(meta);
	}

	public static Iterator<Integer> getPlanterKeyIterator() {
		return planterPackagesByMeta.keySet().iterator();
	}

	public static MachinePackage getPlanterPackage(int meta) {
		return planterPackagesByMeta.get(meta);
	}

	public static boolean hasPlanterPackage(int meta) {
		return planterPackagesByMeta.containsKey(meta);
	}

	public static MachinePackage getMillPackage(int meta) {
		return growerPackagesByMeta.get(meta);
	}

	public static Iterator<Integer> getGrowerKeyIterator() {
		return growerPackagesByMeta.keySet().iterator();
	}

	public static boolean hasGrowerPackage(int meta) {
		return growerPackagesByMeta.containsKey(meta);
	}

}
