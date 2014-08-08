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
package forestry.api.genetics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import forestry.api.apiculture.IBreedingManager;

/**
 * Can be used to garner information on bee breeding. See {@link IBreedingManager}
 * 
 * @author SirSengir
 */
public interface IApiaristTracker {

	void decodeFromNBT(NBTTagCompound nbttagcompound);

	void encodeToNBT(NBTTagCompound nbttagcompound);

	/**
	 * @return Name of the current {@link IBeekeepingMode}.
	 */
	String getModeName();

	/**
	 * @return Set the current {@link IBeekeepingMode}.
	 */
	void setModeName(String name);

	/**
	 * Register the birth of a queen. Will mark species as discovered.
	 * @param bee Created queen.
	 */
	void registerQueen(IIndividual queen);

	/**
	 * @return Amount of queens bred with this tracker.
	 */
	int getQueenCount();

	/**
	 * Register the birth of a princess. Will mark species as discovered.
	 * @param bee Created princess.
	 */
	void registerPrincess(IIndividual princess);

	/**
	 * @return Amount of princesses bred with this tracker.
	 */
	int getPrincessCount();

	/**
	 * Register the birth of a drone. Will mark species as discovered.
	 * @param bee Created drone.
	 */
	void registerDrone(IIndividual drone);

	/**
	 * @return Amount of drones bred with this tracker.
	 */
	int getDroneCount();

	/**
	 * @return Amount of species discovered.
	 */
	int getSpeciesBred();

	/**
	 * Register a successful mutation. Will mark it as discovered.
	 * @param mutation
	 */
	void registerMutation(IMutation mutation);

	/**
	 * Queries the tracker for discovered species.
	 * @param mutation Mutation to query for.
	 * @return true if the mutation has been discovered.
	 */
	boolean isDiscovered(IMutation mutation);

	/**
	 * Queries the tracker for discovered species.
	 * @param species Species to check.
	 * @return true if the species has been bred.
	 */
	boolean isDiscovered(IAlleleSpecies species);

	/**
	 * Synchronizes the tracker to the client side. Should be called before opening any gui needing that information.
	 * @param world
	 * @param player
	 */
	void synchToPlayer(EntityPlayer player);

}
