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
package forestry.arboriculture.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.arboriculture.genetics.BreedingManager;
import forestry.arboriculture.genetics.Tree;
import forestry.core.config.Defaults;
import forestry.core.genetics.ItemGE;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Utils;

public class ItemGermlingGE extends ItemGE {

	private EnumGermlingType type;

	public ItemGermlingGE(int id, EnumGermlingType type) {
		super(id);
		this.type = type;
		this.setTextureFile(Defaults.TEXTURE_GERMLINGS);
		setCreativeTab(Tabs.tabArboriculture);
	}

	public boolean hasEffect(ItemStack itemstack) {
		if(!itemstack.hasTagCompound())
			return false;
		
		IIndividual individual = new Tree(itemstack.getTagCompound());
		if (individual.hasEffect())
			return true;
		else
			return false;
	}

	@Override
	protected int getDefaultPrimaryColour() {
		return 0;
	}

	@Override
	protected int getDefaultSecondaryColour() {
		return 0;
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		if (!itemstack.hasTagCompound())
			return "Unknown";
		IIndividual individual = new Tree(itemstack.getTagCompound());
		return individual.getDisplayName() + " " + type.getName();
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		addCreativeItems(itemList, true);
	}

	public void addCreativeItems(List itemList, boolean hideSecrets) {
		for (IIndividual individual : BreedingManager.treeTemplates) {
			// Don't show secrets unless ordered to.
			if (hideSecrets && individual.isSecret() && !Defaults.DEBUG) {
				continue;
			}

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			ItemStack someStack = new ItemStack(this);
			individual.writeToNBT(nbttagcompound);
			someStack.setTagCompound(nbttagcompound);
			itemList.add(someStack);
		}
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		IIndividual individual = new Tree(itemstack.getTagCompound());
		individual.addTooltip(list);
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 1;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		/*
		if (!itemstack.hasTagCompound())
			return super.getColorFromItemStack(itemstack, renderPass);

		IAlleleSpecies species = TreeManager.treeInterface.getTree(itemstack).getGenome().getPrimary();
		if (species != null) {
			if (renderPass == 0)
				return species.getPrimaryColor();
			else if (renderPass == 1)
				return species.getSecondaryColor();
			else
				return 0xffffff;

		} else */
			return 0xffffff;

	}

	// Return texture index for color overlay
	@Override
	public int getIconIndex(ItemStack itemstack, int renderPass) {
		ITree tree = TreeManager.treeInterface.getTree(itemstack);
		if(tree == null)
			return 0;
		
		IAlleleSpecies species = tree.getGenome().getPrimary();
		return species.getBodyType();
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int par7, float facingX, float facingY,
			float facingZ) {

		if (!Proxies.common.isSimulating(world))
			return false;

		// x, y, z are the coordinates of the block "hit", can thus either be the soil or tall grass, etc.
		int yShift;
		if(!Utils.isReplaceableBlock(world, x, y, z)) {
			if(!world.isAirBlock(x, y + 1, z)) {
				return false;
			}
			yShift = 1;
		} else {
			yShift = 0;
		}
		
		ITree tree = TreeManager.treeInterface.getTree(itemstack);
		if (!tree.canStay(world, x, y + yShift, z))
			return false;

		if (TreeManager.treeInterface.plantSapling(world, tree, x, y + yShift, z)) {
			Proxies.common.addBlockPlaceEffects(world, x, y, z, world.getBlockId(x, y + yShift, z), 0);
			if(!player.capabilities.isCreativeMode)
				itemstack.stackSize--;
			return true;
		} else
			return false;
	}
}
