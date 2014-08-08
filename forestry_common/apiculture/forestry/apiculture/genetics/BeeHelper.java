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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeInterface;
import forestry.api.genetics.IAllele;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.Chromosome;

public class BeeHelper implements IBeeInterface {

	@Override
	public boolean isBee(ItemStack stack) {
		if (stack == null)
			return false;

		return stack.itemID == ForestryItem.beeDroneGE.itemID || stack.itemID == ForestryItem.beePrincessGE.itemID
				|| stack.itemID == ForestryItem.beeQueenGE.itemID;
	}

	@Override
	public boolean isDrone(ItemStack stack) {
		if (stack == null)
			return false;

		return stack.itemID == ForestryItem.beeDroneGE.itemID;
	}

	@Override
	public boolean isMated(ItemStack stack) {
		if (stack == null)
			return false;

		return stack.itemID == ForestryItem.beeQueenGE.itemID;
	}

	@Override
	public IBee getBee(ItemStack stack) {
		if (stack.itemID != ForestryItem.beeQueenGE.itemID && stack.itemID != ForestryItem.beePrincessGE.itemID
				&& stack.itemID != ForestryItem.beeDroneGE.itemID)
			return null;

		return new Bee(stack.getTagCompound());
	}

	@Override
	public IBee getBee(World world, IBeeGenome genome) {
		return new Bee(world, genome);
	}

	@Override
	public IBee getBee(World world, IBeeGenome genome, IBee mate) {
		return new Bee(world, genome, mate);
	}

	@Override
	public ItemStack getBeeStack(IBee bee, EnumBeeType type) {
		Item beeItem = null;
		switch (type) {
		case QUEEN:
			beeItem = ForestryItem.beeQueenGE;
			break;
		case PRINCESS:
			beeItem = ForestryItem.beePrincessGE;
			break;
		case DRONE:
			beeItem = ForestryItem.beeDroneGE;
			break;
		}

		NBTTagCompound nbttagcompound = new NBTTagCompound("tag");
		bee.writeToNBT(nbttagcompound);
		ItemStack beeStack = new ItemStack(beeItem);
		beeStack.setTagCompound(nbttagcompound);
		return beeStack;
	}

	@Override
	public Chromosome[] templateAsChromosomes(IAllele[] template) {
		Chromosome[] chromosomes = new Chromosome[template.length];
		for (int i = 0; i < template.length; i++)
			if (template[i] != null) {
				chromosomes[i] = new Chromosome(template[i]);
			}

		return chromosomes;
	}

	@Override
	public Chromosome[] templateAsChromosomes(IAllele[] templateActive, IAllele[] templateInactive) {
		Chromosome[] chromosomes = new Chromosome[templateActive.length];
		for (int i = 0; i < templateActive.length; i++)
			if (templateActive[i] != null) {
				chromosomes[i] = new Chromosome(templateActive[i], templateInactive[i]);
			}

		return chromosomes;
	}

	@Override
	public IBeeGenome templateAsGenome(IAllele[] template) {
		return new BeeGenome(templateAsChromosomes(template));
	}

	@Override
	public IBeeGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive) {
		return new BeeGenome(templateAsChromosomes(templateActive, templateInactive));
	}

}
