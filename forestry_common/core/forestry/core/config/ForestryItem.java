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
package forestry.core.config;

import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;
import forestry.core.items.ItemCrated;

/**
 * Allows direct access to Forestry's items. Will be populated during BaseMod.load().
 * 
 * Most items with the exception of bees do not care for damage values.
 * 
 * Make sure to only reference it in ModsLoaded() or later.
 * 
 * @author SirSengir
 * 
 */
public class ForestryItem {

	EnumArmorMaterial CLOTH_LATTICE = EnumHelper.addArmorMaterial("CLOTH_LATTICE", 5, new int[] { 1, 1, 1, 1 }, 0);

	public static Item fertilizerBio;
	public static Item fertilizerCompound;
	public static Item apatite;

	// Ingots
	public static ItemStack ingotCopper;
	public static ItemStack ingotTin;
	public static ItemStack ingotBronze;

	// Tools
	public static Item wrench;
	public static Item pipette;

	public static Item bucketBiomass;
	public static Item vialEmpty;
	public static Item vialCatalyst;
	public static Item bucketBiofuel;
	public static Item liquidMilk;

	// Crafting
	public static Item impregnatedCasing;
	public static Item sturdyCasing;
	public static Item hardenedCasing;
	public static Item craftingMaterial;

	// Rainmaker
	public static Item iodineCharge;

	// Gears
	public static Item gearBronze;
	public static Item gearCopper;
	public static Item gearTin;

	// Chipsets
	public static Item circuitboards;
	public static Item solderingIron;
	public static Item tubes;

	// Mail
	public static Item stamps;
	public static Item letters;
	// public static Item mailIndicator;

	// Carpenter
	public static Item stickImpregnated;
	public static Item woodPulp;
	public static Item carton;
	public static Item crate;

	// Tools
	public static Item bronzePickaxe;
	public static Item brokenBronzePickaxe;
	public static Item kitPickaxe;
	public static Item bronzeShovel;
	public static Item brokenBronzeShovel;
	public static Item kitShovel;

	// Do not touch - contagious!
	public static Item tent;
	public static Item biomeFinder;

	// Moistener
	public static Item mouldyWheat;
	public static Item decayingWheat;
	public static Item mulch;

	// Peat
	public static Item peat;
	public static Item bituminousPeat;
	public static Item ash;

	// Bees
	public static Item beeQueenGE;
	public static Item beeDroneGE;
	public static Item beePrincessGE;

	public static Item beealyzer;
	public static Item imprinter;

	public static Item honeyDrop;
	public static Item scoop;
	public static Item beeswax;
	public static Item pollen;
	public static Item propolis;
	public static Item honeydew;
	public static Item royalJelly;
	public static Item honeyedSlice;
	public static Item ambrosia;
	public static Item honeyPot;
	public static Item phosphor;
	public static Item refractoryWax;
	public static Item waxCast;

	public static Item frameUntreated;
	public static Item frameImpregnated;
	public static Item frameProven;

	// Trees
	public static Item sapling;
	public static Item treealyzer;
	public static Item grafter;

	// Beverages
	public static Item beverage;
	public static Item infuser;

	// Apiarist's Armor
	public static Item apiaristHat;
	public static Item apiaristChest;
	public static Item apiaristLegs;
	public static Item apiaristBoots;

	// Combs
	public static Item beeComb;

	// Fruits
	public static Item fruits;
	
	// Backpacks
	public static Item apiaristBackpack;
	public static Item minerBackpack;
	public static Item diggerBackpack;
	public static Item foresterBackpack;
	public static Item hunterBackpack;
	public static Item builderBackpack; // unused/null
	public static Item dyerBackpack; // unused/null
	public static Item railroaderBackpack; // unused/null
	public static Item tinkererBackpack; // unused/null
	public static Item adventurerBackpack;
	// T2
	public static Item minerBackpackT2;
	public static Item diggerBackpackT2;
	public static Item foresterBackpackT2;
	public static Item hunterBackpackT2;
	public static Item builderBackpackT2; // unused/null
	public static Item dyerBackpackT2; // unused/null
	public static Item railroaderBackpackT2; // unused/null
	public static Item tinkererBackpackT2; // unused/null
	public static Item adventurerBackpackT2;

	// Liquids
	public static Item liquidBiomass;
	public static Item liquidBiofuel;
	public static Item liquidSeedOil;
	public static Item liquidJuice;
	public static Item liquidHoney;
	public static Item liquidMead;
	public static Item liquidGlass;
	public static Item liquidIce;

	// Capsules
	public static Item waxCapsule;
	public static Item waxCapsuleWater;
	public static Item waxCapsuleBiomass;
	public static Item waxCapsuleBiofuel;
	public static Item waxCapsuleOil;
	public static Item waxCapsuleFuel;
	public static Item waxCapsuleSeedOil;
	public static Item waxCapsuleHoney;
	public static Item waxCapsuleJuice;
	public static Item waxCapsuleIce;

	// Refractory Capsules
	public static Item refractoryEmpty;
	public static Item refractoryWater;
	public static Item refractoryBiomass;
	public static Item refractoryBiofuel;
	public static Item refractoryOil;
	public static Item refractoryFuel;
	public static Item refractoryLava;
	public static Item refractorySeedOil;
	public static Item refractoryHoney;
	public static Item refractoryJuice;
	public static Item refractoryIce;

	// Cans
	public static Item canWater;
	public static Item canEmpty;
	public static Item canBiomass;
	public static Item canBiofuel;
	public static Item canOil;
	public static Item canFuel;
	public static Item canLava;
	public static Item canSeedOil;
	public static Item canHoney;
	public static Item canJuice;
	public static Item canIce;

	// Crating
	public static ItemCrated cratedWood;
	public static ItemCrated cratedCobblestone;
	public static ItemCrated cratedDirt;
	public static ItemCrated cratedStone;
	public static ItemCrated cratedBrick;
	public static ItemCrated cratedCacti;
	public static ItemCrated cratedSand;
	public static ItemCrated cratedObsidian;
	public static ItemCrated cratedNetherrack;
	public static ItemCrated cratedSoulsand;
	public static ItemCrated cratedSandstone;
	public static ItemCrated cratedBogearth;
	public static ItemCrated cratedHumus;
	public static ItemCrated cratedNetherbrick;
	public static ItemCrated cratedPeat;
	public static ItemCrated cratedApatite;
	public static ItemCrated cratedFertilizer;
	public static ItemCrated cratedTin;
	public static ItemCrated cratedCopper;
	public static ItemCrated cratedBronze;
	public static ItemCrated cratedWheat;
	public static ItemCrated cratedMycelium;
	public static ItemCrated cratedMulch;
	public static ItemCrated cratedSilver;
	public static ItemCrated cratedBrass;
	public static ItemCrated cratedNikolite;
	public static ItemCrated cratedCookies;
	public static ItemCrated cratedHoneycombs;
	public static ItemCrated cratedBeeswax;
	public static ItemCrated cratedPollen;
	public static ItemCrated cratedPropolis;
	public static ItemCrated cratedHoneydew;
	public static ItemCrated cratedRoyalJelly;
	public static ItemCrated cratedCocoaComb;
	public static ItemCrated cratedRedstone;
	public static ItemCrated cratedLapis;
	public static ItemCrated cratedReeds;
	public static ItemCrated cratedClay;
	public static ItemCrated cratedGlowstone;
	public static ItemCrated cratedApples;
	public static ItemCrated cratedNetherwart;
	public static ItemCrated cratedResin;
	public static ItemCrated cratedRubber;
	public static ItemCrated cratedScrap;
	public static ItemCrated cratedUUM;
	public static ItemCrated cratedSimmeringCombs;
	public static ItemCrated cratedStringyCombs;
	public static ItemCrated cratedFrozenCombs;
	public static ItemCrated cratedDrippingCombs;
	public static ItemCrated cratedRefractoryWax;
	public static ItemCrated cratedPhosphor;
	public static ItemCrated cratedAsh;
	public static ItemCrated cratedCharcoal;
	public static ItemCrated cratedGravel;
	public static ItemCrated cratedCoal;
	public static ItemCrated cratedSeeds;
	public static ItemCrated cratedSaplings;

}
