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
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidStack;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StringUtil;

public class ItemLiquidContainer extends Item {

	private boolean isDrink = false;
	private boolean isAlwaysEdible = false;

	private int healAmount = 0;
	private float saturationModifier = 0.0f;

	public ItemLiquidContainer(int i, int iconIndex) {
		super(i);
		this.iconIndex = iconIndex;
		this.setTextureFile(Defaults.TEXTURE_LIQUIDS);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	private int getMatchingSlot(EntityPlayer player, ItemStack stack) {

		for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
			ItemStack slotStack = player.inventory.getStackInSlot(slot);

			if (slotStack == null)
				return slot;

			if (!slotStack.isItemEqual(stack)) {
				continue;
			}

			int space = slotStack.getMaxStackSize() - slotStack.stackSize;
			if (space >= stack.stackSize)
				return slot;
		}

		return -1;
	}

	@Override
	public ItemStack onFoodEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!isDrink)
			return itemstack;

		itemstack.stackSize--;
		entityplayer.getFoodStats().addStats(this.getHealAmount(), this.getSaturationModifier());
		world.playSoundAtEntity(entityplayer, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		/*
		 * if (!world.isRemote && potionId > 0 && world.rand.nextFloat() < potionEffectProbability) entityplayer.addPotionEffect(new PotionEffect(potionId,
		 * potionDuration * 20, potionAmplifier));
		 */

		return itemstack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		if (isDrink)
			return 32;
		else
			return super.getMaxItemUseDuration(itemstack);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		if (isDrink)
			return EnumAction.drink;
		else
			return EnumAction.none;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		if (!Proxies.common.isSimulating(world))
			return itemstack;

		// / DRINKS can be drunk
		if (isDrink) {
			if (entityplayer.canEat(isAlwaysEdible)) {
				entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
			}
			return itemstack;
		}

		// / Otherwise check empty container
		MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, entityplayer, true);
		if (movingobjectposition != null && movingobjectposition.typeOfHit == EnumMovingObjectType.TILE) {

			int i = movingobjectposition.blockX;
			int j = movingobjectposition.blockY;
			int k = movingobjectposition.blockZ;
			int targetedId = world.getBlockId(i, j, k);
			int targetedMeta = world.getBlockMetadata(i, j, k);

			// Check whether there is valid container for the liquid.
			LiquidContainerData container = LiquidHelper.getEmptyContainer(itemstack, new LiquidStack(targetedId, 1, targetedMeta));
			if (container == null)
				return itemstack;

			// Search for a slot to stow a filled container in player's
			// inventory
			int slot = getMatchingSlot(entityplayer, container.filled);
			if (slot < 0)
				return itemstack;

			if (entityplayer.inventory.getStackInSlot(slot) == null) {
				entityplayer.inventory.setInventorySlotContents(slot, container.filled.copy());
			} else {
				entityplayer.inventory.getStackInSlot(slot).stackSize++;
			}

			// Remove consumed liquid block in world
			world.setBlockAndMetadataWithNotify(i, j, k, 0, 0);
			// Remove consumed empty container
			itemstack.stackSize--;

			// Notify player that his inventory has changed.
			Proxies.net.inventoryChangeNotify(entityplayer);

			return itemstack;
		}

		return itemstack;

	}

	public int getHealAmount() {
		return healAmount;
	}

	public float getSaturationModifier() {
		return saturationModifier;
	}

	public ItemLiquidContainer setDrink(int healAmount, float saturationModifier) {
		isDrink = true;
		this.healAmount = healAmount;
		this.saturationModifier = saturationModifier;
		return this;
	}

	/*
	 * public ItemLiquidContainer setPotionEffect(int i, int j, int k, float f) { potionId = i; potionDuration = j; potionAmplifier = k; potionEffectProbability
	 * = f; return this; }
	 */

	public ItemLiquidContainer setAlwaysEdible() {
		isAlwaysEdible = true;
		return this;
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return StringUtil.localize(getItemNameIS(itemstack));
	}

}
