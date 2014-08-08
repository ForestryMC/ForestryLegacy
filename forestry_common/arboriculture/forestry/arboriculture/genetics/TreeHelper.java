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
package forestry.arboriculture.genetics;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeInterface;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IIndividual;
import forestry.arboriculture.gadgets.TileLeaves;
import forestry.arboriculture.gadgets.TileSapling;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.Chromosome;

public class TreeHelper implements ITreeInterface {

	@Override
	public boolean isGermling(ItemStack itemstack) {
		if (itemstack == null)
			return false;

		return itemstack.itemID == ForestryItem.sapling.itemID;
	}

	@Override
	public boolean isPollen(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isPollinated(ItemStack itemstack) {
		return false;
	}

	@Override
	public ITree getTree(World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(!(tile instanceof TileSapling))
			return null;
		
		return ((TileSapling)tile).getTree();
	}
	
	@Override
	public ITree getTree(ItemStack itemstack) {
		if (!isGermling(itemstack))
			return null;
		if (itemstack.getTagCompound() == null)
			return null;

		return new Tree(itemstack.getTagCompound());
	}

	@Override
	public ITree getTree(World world, ITreeGenome genome) {
		return new Tree(genome);
	}

	@Override
	public ItemStack getGermlingStack(ITree tree, EnumGermlingType type) {

		Item germlingItem = null;
		switch (type) {
		case SAPLING:
			germlingItem = ForestryItem.sapling;
			break;
		default:
			return null;
		}

		NBTTagCompound nbttagcompound = new NBTTagCompound("tag");
		tree.writeToNBT(nbttagcompound);
		
		ItemStack treeStack = new ItemStack(germlingItem);
		treeStack.setTagCompound(nbttagcompound);
		
		return treeStack;

	}

	@Override
	public boolean plantSapling(World world, ITree tree, int x, int y, int z) {

		boolean placed = world.setBlockAndMetadataWithNotify(x, y, z, ForestryBlock.saplingGE.blockID, 0);
		if (!placed)
			return false;

		if (world.getBlockId(x, y, z) != ForestryBlock.saplingGE.blockID)
			return false;

		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (!(tile instanceof TileSapling)) {
			world.setBlockAndMetadataWithNotify(x, y, z, 0, 0);
			return false;
		}

		TileSapling sapling = (TileSapling) tile;
		sapling.setTree(tree.copy());
		world.markBlockForUpdate(x, y, z);

		return true;
	}

	@Override
	public boolean setLeaves(World world, IIndividual tree, int x, int y, int z) {

		boolean placed = world.setBlockAndMetadataWithNotify(x, y, z, ForestryBlock.leaves.blockID, 0);
		if (!placed)
			return false;

		if (world.getBlockId(x, y, z) != ForestryBlock.leaves.blockID)
			return false;

		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (!(tile instanceof TileLeaves)) {
			world.setBlockAndMetadataWithNotify(x, y, z, 0, 0);
			return false;
		}

		TileLeaves leaves = (TileLeaves) tile;
		leaves.setTree((ITree)tree.copy());
		world.markBlockForUpdate(x, y, z);

		return true;
	}

	/* GENOME CONVERSIONS */
	@Override
	public IChromosome[] templateAsChromosomes(IAllele[] template) {
		Chromosome[] chromosomes = new Chromosome[template.length];
		for (int i = 0; i < template.length; i++)
			if (template[i] != null) {
				chromosomes[i] = new Chromosome(template[i]);
			}

		return chromosomes;
	}

	@Override
	public IChromosome[] templateAsChromosomes(IAllele[] templateActive, IAllele[] templateInactive) {
		Chromosome[] chromosomes = new Chromosome[templateActive.length];
		for (int i = 0; i < templateActive.length; i++)
			if (templateActive[i] != null) {
				chromosomes[i] = new Chromosome(templateActive[i], templateInactive[i]);
			}

		return chromosomes;
	}

	@Override
	public ITreeGenome templateAsGenome(IAllele[] template) {
		return new TreeGenome(templateAsChromosomes(template));
	}

	@Override
	public ITreeGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive) {
		return new TreeGenome(templateAsChromosomes(templateActive, templateInactive));
	}
}
