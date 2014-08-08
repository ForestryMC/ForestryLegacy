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
package forestry.food.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import forestry.api.food.BeverageManager;
import forestry.api.food.IBeverageEffect;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class ItemBeverage extends Item {

	public static class BeverageInfo {

		public final String name;
		public final int iconType;
		public final int primaryColor;
		public final int secondaryColor;

		public final int heal;
		public final float saturation;
		public final boolean isAlwaysEdible;

		public boolean isSecret = false;

		public BeverageInfo(String name, int iconType, int primaryColor, int secondaryColor, int heal, float saturation, boolean isAlwaysEdible) {
			this.name = name;
			this.iconType = iconType;
			this.primaryColor = primaryColor;
			this.secondaryColor = secondaryColor;
			this.heal = heal;
			this.saturation = saturation;
			this.isAlwaysEdible = isAlwaysEdible;
		}

		public IBeverageEffect[] loadEffects(ItemStack stack) {
			IBeverageEffect[] effects = new IBeverageEffect[0];

			NBTTagCompound nbttagcompound = stack.getTagCompound();
			if (nbttagcompound == null)
				return effects;

			if (nbttagcompound.hasKey("E")) {
				int effectLength = nbttagcompound.getInteger("L");
				NBTTagList nbttaglist = nbttagcompound.getTagList("E");
				effects = new IBeverageEffect[effectLength];
				for (int i = 0; i < nbttaglist.tagCount(); i++) {
					NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
					byte byte0 = nbttagcompound1.getByte("S");
					if (byte0 >= 0 && byte0 < effects.length) {
						effects[byte0] = BeverageManager.effectList[nbttagcompound1.getInteger("ID")];
					}
				}
			}

			return effects;
		}

		public void saveEffects(ItemStack stack, IBeverageEffect[] effects) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();

			NBTTagList nbttaglist = new NBTTagList();
			nbttagcompound.setInteger("L", effects.length);
			for (int i = 0; i < effects.length; i++)
				if (effects[i] != null) {
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setByte("S", (byte) i);
					nbttagcompound1.setInteger("ID", effects[i].getId());
					nbttaglist.appendTag(nbttagcompound1);
				}
			nbttagcompound.setTag("E", nbttaglist);

			stack.setTagCompound(nbttagcompound);
		}

	}

	public BeverageInfo[] beverages;

	public ItemBeverage(int index, BeverageInfo[] beverages) {
		super(index);
		setMaxStackSize(1);
		this.beverages = beverages;
		setTextureFile(Defaults.TEXTURE_LIQUIDS);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over SMP.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public ItemStack onFoodEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		int meta = itemstack.getItemDamage();
		BeverageInfo beverage = beverages[meta];
		IBeverageEffect[] effects = beverage.loadEffects(itemstack);

		itemstack.stackSize--;
		entityplayer.getFoodStats().addStats(beverage.heal, beverage.saturation);
		world.playSoundAtEntity(entityplayer, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);

		if (!Proxies.common.isSimulating(world))
			return itemstack;

		for (IBeverageEffect effect : effects) {
			effect.doEffect(world, entityplayer);
		}

		return itemstack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.drink;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		int meta = itemstack.getItemDamage();
		BeverageInfo beverage = beverages[meta];

		if (entityplayer.canEat(beverage.isAlwaysEdible)) {
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return itemstack;
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < beverages.length; i++)
			if (Defaults.DEBUG || !beverages[i].isSecret) {
				itemList.add(new ItemStack(this, 1, i));
			}
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		int meta = itemstack.getItemDamage();
		BeverageInfo beverage = beverages[meta];
		IBeverageEffect[] effects = beverage.loadEffects(itemstack);

		for (IBeverageEffect effect : effects)
			if (effect.getDescription() != null) {
				list.add(effect.getDescription());
			}
	}

	// Return true to enable color overlay
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return StringUtil.localize("item.beverage." + beverages[itemstack.getItemDamage()].name);
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {

		if (j == 0 || beverages[itemstack.getItemDamage()].secondaryColor == 0)
			return beverages[itemstack.getItemDamage()].primaryColor;
		else
			return beverages[itemstack.getItemDamage()].secondaryColor;
	}

	@Override
	public int getIconFromDamageForRenderPass(int i, int j) {
		if (j > 0 && beverages[i].secondaryColor != 0)
			return beverages[i].iconType;
		else
			return beverages[i].iconType + 15 * 16;
	}

}
