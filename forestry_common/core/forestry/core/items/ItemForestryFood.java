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
package forestry.core.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Defaults;
import forestry.core.utils.StringUtil;

public class ItemForestryFood extends Item {

	private boolean isAlwaysEdible = false;
	private boolean isDrink = false;

	private int healAmount = 0;
	private float saturationModifier;

	private int potionId;
	private int potionDuration;
	private int potionAmplifier;
	private float potionEffectProbability;

	public ItemForestryFood(int index, int heal) {
		this(index, heal, 0.6f);
	}

	public ItemForestryFood(int index, int heal, float saturation) {
		super(index);
		healAmount = heal;
		saturationModifier = saturation;
		setTextureFile(Defaults.TEXTURE_ITEMS);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public ItemStack onFoodEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		itemstack.stackSize--;
		entityplayer.getFoodStats().addStats(healAmount, saturationModifier);
		world.playSoundAtEntity(entityplayer, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		if (!world.isRemote && potionId > 0 && world.rand.nextFloat() < potionEffectProbability) {
			entityplayer.addPotionEffect(new PotionEffect(potionId, potionDuration * 20, potionAmplifier));
		}

		return itemstack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		if (isDrink)
			return EnumAction.drink;
		else
			return EnumAction.eat;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (entityplayer.canEat(isAlwaysEdible)) {
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return itemstack;
	}

	public ItemForestryFood setIsDrink() {
		isDrink = true;
		return this;
	}

	public ItemForestryFood setPotionEffect(int i, int j, int k, float f) {
		potionId = i;
		potionDuration = j;
		potionAmplifier = k;
		potionEffectProbability = f;
		return this;
	}

	public ItemForestryFood setAlwaysEdible() {
		isAlwaysEdible = true;
		return this;
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return StringUtil.localize(getItemNameIS(itemstack));
	}

}
