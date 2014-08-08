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
package forestry.apiculture.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.core.Tabs;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.apiculture.genetics.Bee;
import forestry.apiculture.genetics.BreedingManager;
import forestry.core.config.Defaults;
import forestry.core.genetics.ItemGE;
import forestry.core.utils.StringUtil;

public class ItemBeeGE extends ItemGE {

	EnumBeeType type;

	public ItemBeeGE(int i, EnumBeeType type) {
		super(i);
		this.type = type;
		this.setTextureFile(Defaults.TEXTURE_BEES);
		setCreativeTab(Tabs.tabApiculture);
		if (type != EnumBeeType.DRONE) {
			setMaxStackSize(1);
		}
	}

	@Override
	protected int getDefaultPrimaryColour() {
		return 0xffffff;
	}

	@Override
	protected int getDefaultSecondaryColour() {
		return 0xffdc16;
	}

	@Override
	public boolean hasEffect(ItemStack itemstack) {
		IIndividual individual = new Bee(itemstack.getTagCompound());
		if (individual.hasEffect())
			return true;
		else
			return false;
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {

		if (itemstack.getTagCompound() == null)
			return StringUtil.localize(type.getName());

		IBee individual = new Bee(itemstack.getTagCompound());
		String name = individual.getDisplayName() + StringUtil.localize(type.getName() + ".adj.add") + " " + StringUtil.localize(type.getName());
		if (individual.isNatural())
			return name;
		else
			return name + " [-]";
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		if (type == EnumBeeType.QUEEN)
			return;

		addCreativeItems(itemList, true);
	}

	public void addCreativeItems(List itemList, boolean hideSecrets) {

		for (IIndividual individual : BreedingManager.beeTemplates) {
			// Don't show secret bees unless ordered to.
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

		if (itemstack.getTagCompound() == null)
			return;

		IIndividual individual = new Bee(itemstack.getTagCompound());
		individual.addTooltip(list);
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		if (!itemstack.hasTagCompound())
			return super.getColorFromItemStack(itemstack, renderPass);

		return getColourFromSpecies(BeeManager.beeInterface.getBee(itemstack).getGenome().getPrimary(), renderPass);
	}

	@Override
	public int getColourFromSpecies(IAlleleSpecies species, int renderPass) {
		
		if (species != null) {
			if (renderPass == 0)
				return species.getPrimaryColor();
			else if (renderPass == 1)
				return species.getSecondaryColor();
			else
				return 0xffffff;

		} else
			return 0xffffff;

	}
	
	// Return texture index for color overlay
	@Override
	public int getIconIndex(ItemStack itemstack, int renderPass) {
		return getIconIndexFromSpecies(BeeManager.beeInterface.getBee(itemstack).getGenome().getPrimary(), renderPass);
	}

	public int getIconIndexFromSpecies(IAlleleSpecies species, int renderPass) {
		int indexOffset = 0;
		if (species != null) {
			indexOffset = 16 * species.getBodyType();
		}

		if (renderPass == 0)
			return indexOffset + 0 + type.ordinal();
		else if (renderPass == 1)
			return indexOffset + 3 + type.ordinal();
		else
			return indexOffset + 6 + type.ordinal();

	}
}
