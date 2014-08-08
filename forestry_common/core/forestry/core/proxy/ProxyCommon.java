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
package forestry.core.proxy;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.texturepacks.ITexturePack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.core.gadgets.MachinePackage;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketFXSignal;
import forestry.core.network.PacketIds;

public class ProxyCommon {

	public String getMinecraftVersion() {
		return "1.4.7";
	}

	public void addRecipe(ItemStack itemstack, Object[] obj) {
		CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(itemstack, obj));
	}

	public void addShapelessRecipe(ItemStack itemstack, Object[] obj) {
		CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(itemstack, obj));
	}

	public void addSmelting(ItemStack res, ItemStack prod) {
		GameRegistry.addSmelting(res.itemID, prod, 0.0f);
	}

	public void dropItemPlayer(EntityPlayer player, ItemStack stack) {
		player.dropPlayerItem(stack);
	}

	public void setBiomeFinderCoordinates(EntityPlayer player, ChunkCoordinates coordinates) {
		if (coordinates != null) {
			((EntityPlayerMP) player).playerNetServerHandler
					.sendPacketToPlayer(new PacketCoordinates(PacketIds.HABITAT_BIOME_POINTER, coordinates).getPacket());
		}
	}

	public void removePotionEffect(EntityPlayer player, Potion effect) {
		player.clearActivePotions();
	}

	public String getCurrentLanguage() {
		return null;
	}

	public String getItemDisplayName(Item item) {
		return null;
	}

	public String getDisplayName(ItemStack itemstack) {
		return null;
	}

	public File getForestryRoot() {
		return new File("./");
	}

	public int getByBlockModelId() {
		return 0;
	}

	public boolean isOp(EntityPlayer player) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server.getConfigurationManager().areCommandsAllowed(player.username);
	}

	public double getBlockReachDistance(EntityPlayer entityplayer) {
		return 4f;
	}

	public boolean isSimulating(World world) {
		return true;
	}

	public boolean isShiftDown() {
		return false;
	}

	public boolean isItemStackTagEqual(ItemStack stack1, ItemStack stack2) {
		return ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	public String getItemDisplayName(ItemStack stack) {
		return null;
	}

	public void playSoundFX(World world, int x, int y, int z, Block block) {
		Proxies.net.sendNetworkPacket(new PacketFXSignal(PacketFXSignal.SoundFXType.LEAF, x, y, z, block.blockID, 0), x, y, z);
	}

	public void playSoundFX(World world, int x, int y, int z, String sound, float volume, float pitch) {
	}

	public void addEntityBiodustFX(World world, double d1, double d2, double d3, float f1, float f2, float f3) {
	}

	public void addEntitySwarmFX(World world, double d1, double d2, double d3, float f1, float f2, float f3) {
	}

	public void addEntityExplodeFX(World world, double d1, double d2, double d3, float f1, float f2, float f3) {
	}

	public void registerPlanterRenderer(int meta, MachinePackage pack) {
	}

	public void registerMachineRenderer(int meta, MachinePackage pack) {
	}

	public void registerMillRenderer(int meta, MachinePackage pack) {
	}

	public boolean needsTagCompoundSynched(Item item) {
		return item.getShareTag();
	}

	public void addBlockDestroyEffects(World world, int xCoord, int yCoord, int zCoord, int blockid, int i) {
		sendFXSignal(PacketFXSignal.VisualFXType.BLOCK_DESTROY, PacketFXSignal.SoundFXType.BLOCK_DESTROY, world, xCoord, yCoord, zCoord, blockid, i);
	}

	public void addBlockPlaceEffects(World world, int xCoord, int yCoord, int zCoord, int blockid, int i) {
		sendFXSignal(PacketFXSignal.VisualFXType.NONE, PacketFXSignal.SoundFXType.BLOCK_PLACE, world, xCoord, yCoord, zCoord, blockid, i);
	}

	public void playBlockBreakSoundFX(World world, int x, int y, int z, Block block) {
	}

	public void playBlockPlaceSoundFX(World world, int x, int y, int z, Block block) {
	}

	public void sendFXSignal(PacketFXSignal.VisualFXType visualFX, PacketFXSignal.SoundFXType soundFX, World world, int xCoord, int yCoord, int zCoord, int blockid, int i) {
		if(Proxies.common.isSimulating(world))
			Proxies.net.sendNetworkPacket(new PacketFXSignal(visualFX, soundFX, xCoord, yCoord, zCoord, blockid, i), xCoord, yCoord, zCoord);
	}

	public void bindTexture(String textureFile) {
	}

	public ITexturePack getSelectedTexturePack(Minecraft minecraft) {
		return null;
	}

	public World getRenderWorld() {
		return null;
	}

	public Minecraft getClientInstance() {
		return FMLClientHandler.instance().getClient();
	}

	public int getBlockModelIdEngine() {
		return 0;
	}

	public void closeGUI(EntityPlayer player) {
		((EntityPlayerMP) player).closeScreen();
	}

	/* DEPENDENCY HANDLING */
	public boolean isModLoaded(String modname) {
		return Loader.isModLoaded(modname);
	}

	public Object instantiateIfModLoaded(String modname, String className) {

		if (isModLoaded(modname)) {

			try {
				Class<?> clas = Class.forName(className, true, Loader.instance().getModClassLoader());
				return clas.newInstance();
			} catch (Exception ex) {
				Proxies.log.severe("Failed to load " + className + " even though mod " + modname + " was available.");
				return null;
			}
		} else
			return null;

	}

}
