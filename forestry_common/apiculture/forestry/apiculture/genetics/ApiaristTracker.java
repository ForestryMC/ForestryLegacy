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
package forestry.apiculture.genetics;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import forestry.api.apiculture.BeeManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IApiaristTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.core.config.Defaults;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketNBT;
import forestry.core.proxy.Proxies;

public class ApiaristTracker extends WorldSavedData implements IApiaristTracker {

	private ArrayList<String> discoveredSpecies = new ArrayList<String>(Defaults.SPECIES_BEE_LIMIT);
	private ArrayList<String> discoveredMutations = new ArrayList<String>();
	private String beekeepingModeName;

	private int queensTotal = 0;
	private int dronesTotal = 0;
	private int princessesTotal = 0;

	public ApiaristTracker(String s) {
		super(s);
	}

	@Override
	public String getModeName() {
		return beekeepingModeName;
	}

	@Override
	public void setModeName(String name) {
		this.beekeepingModeName = name;
		markDirty();
	}

	@Override
	public void synchToPlayer(EntityPlayer player) {
		setModeName(BeeManager.breedingManager.getApiaristTracker(player.worldObj, "__COMMON_").getModeName());
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		encodeToNBT(nbttagcompound);
		Proxies.net.sendToPlayer(new PacketNBT(PacketIds.GENOME_TRACKER_UPDATE, nbttagcompound), player);		
	}

	/* HELPER FUNCTIONS TO PREVENT OBFUSCATION OF INTERFACE METHODS */
	@Override
	public void decodeFromNBT(NBTTagCompound nbttagcompound) {
		readFromNBT(nbttagcompound);
	}

	@Override
	public void encodeToNBT(NBTTagCompound nbttagcompound) {
		writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		queensTotal = nbttagcompound.getInteger("QueensTotal");
		princessesTotal = nbttagcompound.getInteger("PrincessesTotal");
		dronesTotal = nbttagcompound.getInteger("DronesTotal");

		if (nbttagcompound.hasKey("BMS")) {
			beekeepingModeName = nbttagcompound.getString("BMS");
		}

		// / SPECIES
		discoveredSpecies = new ArrayList<String>(Defaults.SPECIES_BEE_LIMIT);
		int count = nbttagcompound.getInteger("SpeciesCount");
		for (int i = 0; i < count; i++) {
			discoveredSpecies.add(nbttagcompound.getString("SD" + i));
		}

		// / MUTATIONS
		discoveredMutations = new ArrayList<String>();
		count = nbttagcompound.getInteger("MutationsCount");
		for (int i = 0; i < count; i++) {
			discoveredMutations.add(nbttagcompound.getString("MD" + i));
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		nbttagcompound.setInteger("QueensTotal", queensTotal);
		nbttagcompound.setInteger("PrincessesTotal", princessesTotal);
		nbttagcompound.setInteger("DronesTotal", dronesTotal);
		if (beekeepingModeName != null && !beekeepingModeName.isEmpty()) {
			nbttagcompound.setString("BMS", beekeepingModeName);
		}

		// / SPECIES
		nbttagcompound.setInteger("SpeciesCount", discoveredSpecies.size());
		for (int i = 0; i < discoveredSpecies.size(); i++)
			if (discoveredSpecies.get(i) != null) {
				nbttagcompound.setString("SD" + i, discoveredSpecies.get(i));
			}

		// / MUTATIONS
		nbttagcompound.setInteger("MutationsCount", discoveredMutations.size());
		for (int i = 0; i < discoveredMutations.size(); i++)
			if (discoveredMutations.get(i) != null) {
				nbttagcompound.setString("MD" + i, discoveredMutations.get(i));
			}

	}

	@Override
	public void registerQueen(IIndividual bee) {
		queensTotal++;
	}

	@Override
	public int getQueenCount() {
		return queensTotal;
	}

	@Override
	public void registerPrincess(IIndividual bee) {

		princessesTotal++;
		IAlleleSpecies primary = bee.getGenome().getPrimary();
		IAlleleSpecies secondary = bee.getGenome().getSecondary();

		registerSpecies(primary);
		registerSpecies(secondary);
		markDirty();
	}

	@Override
	public int getPrincessCount() {
		return princessesTotal;
	}

	@Override
	public void registerDrone(IIndividual bee) {
		dronesTotal++;
		IAlleleSpecies primary = bee.getGenome().getPrimary();
		IAlleleSpecies secondary = bee.getGenome().getSecondary();

		registerSpecies(primary);
		registerSpecies(secondary);
		markDirty();
	}

	@Override
	public int getDroneCount() {
		return dronesTotal;
	}

	@Override
	public void registerMutation(IMutation mutation) {
		discoveredMutations.add(mutation.getAllele0().getUID() + "-" + mutation.getAllele1().getUID());
		markDirty();
	}

	private void registerSpecies(IAllele species) {
		if (!discoveredSpecies.contains(species.getUID())) {
			discoveredSpecies.add(species.getUID());
		}
	}

	@Override
	public boolean isDiscovered(IMutation mutation) {
		return discoveredMutations.contains(mutation.getAllele0().getUID() + "-" + mutation.getAllele1().getUID());
	}

	@Override
	public boolean isDiscovered(IAlleleSpecies species) {
		return discoveredSpecies.contains(species.getUID());
	}

	@Override
	public int getSpeciesBred() {
		return discoveredSpecies.size();
	}

}
