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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.apiculture.IBreedingManager;
import forestry.api.core.IStructureLogic;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IApiaristTracker;
import forestry.apiculture.BeekeepingLogic;
import forestry.apiculture.gadgets.StructureLogicAlveary;
import forestry.plugins.PluginForestryApiculture;

public class BreedingManager implements IBreedingManager {

	public static int beeSpeciesCount = -1;

	@Override
	public int getBeeSpeciesCount() {
		if (beeSpeciesCount < 0) {
			beeSpeciesCount = 0;
			Iterator it = AlleleManager.alleleRegistry.getRegisteredAlleles().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, IAllele> entry = (Entry<String, IAllele>) it.next();
				if (entry.getValue() instanceof IAlleleBeeSpecies)
					if (((IAlleleBeeSpecies) entry.getValue()).isCounted()) {
						beeSpeciesCount++;
					}
			}
		}

		return beeSpeciesCount;
	}

	// / BREEDING MODES
	ArrayList<IBeekeepingMode> beekeepingModes = new ArrayList<IBeekeepingMode>();
	public static IBeekeepingMode activeBeekeepingMode;

	@Override
	public ArrayList<IBeekeepingMode> getBeekeepingModes() {
		return this.beekeepingModes;
	}

	@Override
	public IBeekeepingMode getBeekeepingMode(World world) {
		if (activeBeekeepingMode != null)
			return activeBeekeepingMode;

		// No beekeeping mode yet, get it.
		IApiaristTracker tracker = getApiaristTracker(world, "__COMMON_");
		String mode = tracker.getModeName();
		if (mode == null || mode.isEmpty()) {
			mode = PluginForestryApiculture.beekeepingMode;
		}

		setBeekeepingMode(world, mode);		
		FMLCommonHandler.instance().getFMLLogger().fine("Set beekeeping mode for a world to " + mode);
		
		return activeBeekeepingMode;
	}

	@Override
	public void registerBeekeepingMode(IBeekeepingMode mode) {
		beekeepingModes.add(mode);
	}

	@Override
	public void setBeekeepingMode(World world, String name) {
		activeBeekeepingMode = getBeekeepingMode(name);
		getApiaristTracker(world, "__COMMON_").setModeName(name);
	}

	@Override
	public IBeekeepingMode getBeekeepingMode(String name) {
		for (IBeekeepingMode mode : beekeepingModes)
			if (mode.getName().equals(name)
					|| mode.getName().equals(name.toLowerCase(Locale.ENGLISH)))
				return mode;

		FMLCommonHandler.instance().getFMLLogger().fine("Failed to find a beekeeping mode called '%s', reverting to fallback.");
		return beekeepingModes.get(0);
	}

	/* BLACKLISTING */
	@Override
	public void blacklistBeeSpecies(String uid) {
		AlleleManager.alleleRegistry.blacklistAllele(uid);
	}

	@Override
	public ArrayList<String> getBeeSpeciesBlacklist() {
		return AlleleManager.alleleRegistry.getAlleleBlacklist();
	}

	@Override
	public boolean isBlacklisted(String uid) {
		return AlleleManager.alleleRegistry.isBlacklisted(uid);
	}

	/* MUTATIONS */
	/**
	 * List of possible mutations on species alleles.
	 */
	private static ArrayList<IBeeMutation> beeMutations = new ArrayList<IBeeMutation>();

	@Override
	public ArrayList<IBeeMutation> getMutations(boolean shuffle) {
		if(shuffle)
			Collections.shuffle(beeMutations);
		return beeMutations;
	}
	
	@Override
	public void registerBeeMutation(IBeeMutation mutation) {
		if(AlleleManager.alleleRegistry.isBlacklisted(mutation.getTemplate()[0].getUID()))
			return;
		if(AlleleManager.alleleRegistry.isBlacklisted(mutation.getAllele0().getUID()))
			return;
		if(AlleleManager.alleleRegistry.isBlacklisted(mutation.getAllele1().getUID()))
			return;
		
		beeMutations.add(mutation);
	}
	
	// / TEMPLATES
	public static HashMap<String, IAllele[]> speciesTemplates = new HashMap<String, IAllele[]>();
	public static ArrayList<IBee> beeTemplates = new ArrayList<IBee>();

	@Override
	public void registerBeeTemplate(IAllele[] template) {
		registerBeeTemplate(template[0].getUID(), template);
	}

	@Override
	public void registerBeeTemplate(String identifier, IAllele[] template) {
		BreedingManager.beeTemplates.add(new Bee(BeeManager.beeInterface.templateAsGenome(template)));
		BreedingManager.speciesTemplates.put(identifier, template);
	}

	@Override
	public IAllele[] getBeeTemplate(String identifier) {
		return BreedingManager.speciesTemplates.get(identifier);
	}

	@Override
	public IAllele[] getDefaultBeeTemplate() {
		return BeeTemplates.getDefaultTemplate();
	}

	@Override
	public IApiaristTracker getApiaristTracker(World world, String player) {
		String filename = "ApiaristTracker." + player;
		ApiaristTracker tracker = (ApiaristTracker) world.loadItemData(ApiaristTracker.class, filename);

		// Create a tracker if there is none yet.
		if (tracker == null) {
			tracker = new ApiaristTracker(filename);
			world.setItemData(filename, tracker);
		}

		return tracker;
	}

	@Override
	public IBeekeepingLogic createBeekeepingLogic(IBeeHousing housing) {
		return new BeekeepingLogic(housing);
	}

	@Override
	public IStructureLogic createAlvearyStructureLogic(IAlvearyComponent structure) {
		return new StructureLogicAlveary(structure);
	}
}
