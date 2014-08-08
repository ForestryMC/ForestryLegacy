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
package forestry.core.utils;

import java.util.ArrayList;
import java.util.HashMap;

import forestry.core.interfaces.IDescriptiveRecipe;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ShapedRecipeCustom implements IDescriptiveRecipe {

	private int width;
	private int height;
	
	private Object ingredients[];
	private ItemStack product;
	
	private boolean preserveNBT = false;

	public ShapedRecipeCustom(int width, int height, Object ingredients[], ItemStack product) {
		this.width = width;
		this.height = height;
		this.ingredients = ingredients;
		this.product = product;
	}

	public ShapedRecipeCustom setPreserveNBT() {
		this.preserveNBT = true;
		return this;
	}
	
	@Override public int getWidth() { return width; }
	@Override public int getHeight() { return height; }

	@Override
	public ItemStack getRecipeOutput() {
		return product;
	}

	@Override
	public Object[] getIngredients() {
		return ingredients;
	}

	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		ItemStack[][] resources = new ItemStack[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				resources[i][j] = inventorycrafting.getStackInRowAndColumn(i, j);
			}
		}

		return matches(resources);
	}

	public boolean matches(ItemStack[][] resources) {

		for (int i = 0; i <= 3 - width; i++) {
			for (int j = 0; j <= 3 - height; j++) {
				if (checkMatch(resources, i, j, true))
					return true;

				if (checkMatch(resources, i, j, false))
					return true;
			}
		}

		return false;
	}

	private boolean checkMatch(ItemStack[][] resources, int xInGrid, int yInGrid, boolean flag) {

		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 3; l++) {

				int widthIt = k - xInGrid;
				int heightIt = l - yInGrid;
				Object compare = null;

				if (widthIt >= 0 && heightIt >= 0 && widthIt < width && heightIt < height) {
					if (flag) {
						compare = ingredients[(width - widthIt - 1) + heightIt * width];
					} else {
						compare = ingredients[widthIt + heightIt * width];
					}
				}
				ItemStack resource = resources[k][l];

				if (compare instanceof ItemStack) {
					if (!checkItemMatch((ItemStack) compare, resource))
						return false;
				} else if (compare instanceof ArrayList) {
					boolean matched = false;

					for (ItemStack item : (ArrayList<ItemStack>) compare) {
						matched = matched || checkItemMatch(item, resource);
					}

					if (!matched)
						return false;

				} else if (compare == null && resource != null)
					return false;
			}
		}

		return true;
	}

	private boolean checkItemMatch(ItemStack compare, ItemStack resource) {

		if (resource == null && compare == null)
			return true;

		if (resource == null && compare != null || resource != null && compare == null)
			return false;

		if (compare.itemID != resource.itemID)
			return false;

		if (compare.getItemDamage() != -1 && compare.getItemDamage() != resource.getItemDamage())
			return false;

		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
		if(preserveNBT) {
			for(int i = 0; i < inventorycrafting.getSizeInventory(); i++) {
				if(inventorycrafting.getStackInSlot(i) == null)
					continue;
				if(!inventorycrafting.getStackInSlot(i).hasTagCompound())
					continue;
				
				ItemStack crafted = product.copy();
				crafted.setTagCompound((NBTTagCompound)inventorycrafting.getStackInSlot(i).getTagCompound().copy());
				return crafted;
			}
		}
		
		return product.copy();
	}

	@Override
	public int getRecipeSize() {
		return width * height;
	}

	/**
	 * @param resource
	 * @return true if resource is a valid ingredient in this recipe.
	 */
	public boolean isIngredient(ItemStack resource) {

		for (Object ingredient : ingredients) {

			if (ingredient instanceof ItemStack) {
				if (checkItemMatch((ItemStack) ingredient, resource))
					return true;

			} else if (ingredient instanceof ArrayList) {
				for (ItemStack item : (ArrayList<ItemStack>) ingredient)
					if (checkItemMatch(item, resource))
						return true;
			}
		}

		return false;

	}

	public static ShapedRecipeCustom createShapedRecipe(Object[] materials, ItemStack product) {

		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		if (materials[i] instanceof String[]) {
			String as[] = (String[]) materials[i++];
			for (int l = 0; l < as.length; l++) {
				String pattern = as[l];
				k++;
				j = pattern.length();
				s = (new StringBuilder()).append(s).append(pattern).toString();
			}

		} else {
			while (materials[i] instanceof String) {
				String pattern = (String) materials[i++];
				k++;
				j = pattern.length();
				s = (new StringBuilder()).append(s).append(pattern).toString();
			}
		}

		HashMap<Character, Object> hashmap = new HashMap<Character, Object>();
		for (; i < materials.length; i += 2) {

			Character character = (Character) materials[i];

			// Item
			if (materials[i + 1] instanceof Item) {
				hashmap.put(character, new ItemStack((Item) materials[i + 1]));
			} else if (materials[i + 1] instanceof Block) {
				hashmap.put(character, new ItemStack((Block) materials[i + 1], 1, -1));
			} else if (materials[i + 1] instanceof ItemStack) {
				hashmap.put(character, materials[i + 1]);
			} else if (materials[i + 1] instanceof String) {
				hashmap.put(character, OreDictionary.getOres((String) materials[i + 1]));
			}

		}

		Object ingredients[] = new Object[j * k];
		for (int l = 0; l < j * k; l++) {
			char c = s.charAt(l);
			if (hashmap.containsKey(Character.valueOf(c))) {
				ingredients[l] = hashmap.get(Character.valueOf(c));
			} else {
				ingredients[l] = null;
			}
		}

		return new ShapedRecipeCustom(j, k, ingredients, product);
	}

}
