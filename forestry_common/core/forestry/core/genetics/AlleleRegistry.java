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
package forestry.core.genetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.apiculture.BeeManager;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleHandler;
import forestry.api.genetics.IAlleleRegistry;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ILegacyHandler;
import forestry.core.utils.IDAllocator;

public class AlleleRegistry implements IAlleleRegistry, ILegacyHandler {

	public static final int ALLELE_ARRAY_SIZE = 2048;

	/* INDIVIDUALS */
	public boolean isIndividual(ItemStack stack) {
		if(BeeManager.beeInterface != null)
			if(BeeManager.beeInterface.isBee(stack))
				return true;
		if(TreeManager.treeInterface != null)
			if(TreeManager.treeInterface.isGermling(stack))
				return true;
		
		return false;
	}
	
	public IIndividual getIndividual(ItemStack stack) {
		IIndividual individual = null;
		if(BeeManager.beeInterface != null)
			individual = BeeManager.beeInterface.getBee(stack);
		if(individual == null && TreeManager.treeInterface != null)
			individual = TreeManager.treeInterface.getTree(stack);
		
		return individual;
	}
	
	/* ALLELES */
	private LinkedHashMap<String, IAllele> alleleMap = new LinkedHashMap<String, IAllele>(ALLELE_ARRAY_SIZE);
	private LinkedHashMap<String, IClassification> classificationMap = new LinkedHashMap<String, IClassification>(128);
	private LinkedHashMap<String, IFruitFamily> fruitMap = new LinkedHashMap<String, IFruitFamily>(64);
	
	private HashMap<Integer, String> metaMapToUID = new HashMap<Integer, String>();
	private HashMap<String, Integer> uidMapToMeta = new HashMap<String, Integer>();

	private HashMap<Integer, String> legacyMap = new HashMap<Integer, String>();

	/*
	 * Internal HashSet of all alleleHandlers, which trigger when an allele or branch is registered
	 */
	private HashSet<IAlleleHandler> alleleHandlers = new HashSet<IAlleleHandler>();

	public void initialize() {
		
		createAndRegisterClassification(EnumClassLevel.DOMAIN, "archaea", "Archaea");
		createAndRegisterClassification(EnumClassLevel.DOMAIN, "bacteria", "Bacteria");
		IClassification eukarya = createAndRegisterClassification(EnumClassLevel.DOMAIN, "eukarya", "Eukarya");
		
		eukarya.addMemberGroup(createAndRegisterClassification(EnumClassLevel.KINGDOM, "animalia", "Animalia"));
		eukarya.addMemberGroup(createAndRegisterClassification(EnumClassLevel.KINGDOM, "plantae", "Plantae"));
		eukarya.addMemberGroup(createAndRegisterClassification(EnumClassLevel.KINGDOM, "fungi", "Fungi"));
		eukarya.addMemberGroup(createAndRegisterClassification(EnumClassLevel.KINGDOM, "protista", "Protista"));
		
		getClassification("kingdom.animalia").addMemberGroup(
				createAndRegisterClassification(EnumClassLevel.PHYLUM, "arthropoda", "Arthropoda"));
		
		// Animalia
		getClassification("phylum.arthropoda").addMemberGroup(
				createAndRegisterClassification(EnumClassLevel.CLASS, "insecta", "Insecta"));
		
	}
	
	@Override
	public LinkedHashMap<String, IAllele> getRegisteredAlleles() {
		return alleleMap;
	}

	@Override
	public LinkedHashMap<String, IClassification> getRegisteredClassifications() {
		return classificationMap;
	}

	@Override
	public LinkedHashMap<String, IFruitFamily> getRegisteredFruitFamilies() {
		return fruitMap;
	}

	@Override
	public void registerAllele(IAllele allele) {
		alleleMap.put(allele.getUID(), allele);
		if (allele instanceof IAlleleSpecies) {
			IClassification branch = ((IAlleleSpecies) allele).getBranch();
			if (branch != null) {
				branch.addMemberSpecies((IAlleleSpecies) allele);
			}
		}
		for (IAlleleHandler handler : this.alleleHandlers) {
			handler.onRegisterAllele(allele);
		}
	}

	@Override
	public IAllele getAllele(String uid) {
		/*
		 * if(alleleMap.get(uid) == null) { System.out.println("Failed getting allele for " + uid); System.out.println("Retrying the hard way:"); Iterator it =
		 * alleleMap.entrySet().iterator(); while(it.hasNext()) { Entry<String, IAllele> entry = (Entry<String, IAllele>)it.next();
		 * System.out.println("Looping: " + entry.getKey() + " -> " + entry.getValue().getUID()); } }
		 */
		return alleleMap.get(uid);
	}

	@Override
	public void reloadMetaMap(World world) {
		metaMapToUID.clear();
		uidMapToMeta.clear();

		Iterator<Entry<String, IAllele>> it = alleleMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, IAllele> entry = it.next();
			if (!(entry.getValue() instanceof IAlleleSpecies)) {
				continue;
			}

			int meta = IDAllocator.getIDAllocator(world, "speciesMetaMap").getId(entry.getKey());
			metaMapToUID.put(meta, entry.getKey());
			uidMapToMeta.put(entry.getKey(), meta);

		}
	}

	@Override
	public IAllele getFromMetaMap(int meta) {
		if (!metaMapToUID.containsKey(meta))
			return null;

		return getAllele(metaMapToUID.get(meta));
	}

	@Override
	public int getFromUIDMap(String uid) {
		if (!uidMapToMeta.containsKey(uid))
			return 0;

		return uidMapToMeta.get(uid);
	}

	/* CLASSIFICATIONS */
	@Override
	public void registerClassification(IClassification branch) {
		
		if(classificationMap.containsKey(branch.getUID()))
			throw new RuntimeException(String.format("Could not add new classification '%s', because the key is already taken by %s.", branch.getUID(), classificationMap.get(branch.getUID())));
		
		classificationMap.put(branch.getUID(), branch);
		for (IAlleleHandler handler : this.alleleHandlers) {
			handler.onRegisterClassification(branch);
		}
	}

	@Override
	public IClassification createAndRegisterClassification(EnumClassLevel level, String uid, String scientific) {
		return new Classification(level, uid, scientific);
	}
	
	@Override
	public IClassification getClassification(String uid) {
		return classificationMap.get(uid);
	}

	/* FRUIT FAMILIES */
	@Override
	public void registerFruitFamily(IFruitFamily family) {
		fruitMap.put(family.getUID(), family);
		for (IAlleleHandler handler : this.alleleHandlers) {
			handler.onRegisterFruitFamily(family);
		}
	}

	@Override
	public IFruitFamily getFruitFamily(String uid) {
		return fruitMap.get(uid);
	}

	@Override
	public void registerLegacyMapping(int id, String uid) {
		this.legacyMap.put(id, uid);
	}

	@Override
	public IAllele getFromLegacyMap(int id) {
		if (!legacyMap.containsKey(id))
			return null;

		return getAllele(legacyMap.get(id));
	}

	@Override
	public void registerAlleleHandler(IAlleleHandler handler) {
		this.alleleHandlers.add(handler);
	}

	/* BLACKLIST */
	private ArrayList<String> blacklist = new ArrayList<String>();

	@Override
	public void blacklistAllele(String uid) {
		blacklist.add(uid);
	}

	@Override
	public ArrayList<String> getAlleleBlacklist() {
		return blacklist;
	}

	@Override
	public boolean isBlacklisted(String uid) {
		return blacklist.contains(uid);
	}
}
