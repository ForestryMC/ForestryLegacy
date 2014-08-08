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
package forestry;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.FingerprintWarning;
import cpw.mods.fml.common.Mod.IMCCallback;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import forestry.core.ForestryCore;
import forestry.core.config.Version;
import forestry.core.network.PacketHandler;
import forestry.core.proxy.Proxies;

/**
 * Forestry Minecraft Mod
 * 
 * @author SirSengir
 */
@Mod(
		modid = "Forestry",
		name = "Forestry",
		version = Version.VERSION,
		dependencies = "after:ExtrabiomesXL;after:BiomesOPlenty",
		certificateFingerprint = Version.FINGERPRINT
)
@NetworkMod(channels = { "FOR" }, clientSideRequired = true, serverSideRequired = true, packetHandler = PacketHandler.class)
public class Forestry {

	@SidedProxy(clientSide = "forestry.core.ForestryClient", serverSide = "forestry.core.ForestryCore")
	public static ForestryCore core = new ForestryCore();

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		core.preInit(event.getSourceFile(), this);
	}

	@Init
	public void init(FMLInitializationEvent event) {
		core.init(this);
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		core.postInit();
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event) {
		core.serverStarting(event.getServer());
	}

	@FingerprintWarning
	public void fingerprintWarning(FMLFingerprintViolationEvent event) {
		Proxies.log.warning("Fingerprint of the mod jar is invalid. The jar file was tampered with. This is not good.");
		//FMLInterModComms.sendMessage("Forestry", "securityViolation", "Fingerprint of jar file did not match.");
		FMLInterModComms.sendMessage("Railcraft", "securityViolation", "Fingerprint of jar file did not match.");
		FMLInterModComms.sendMessage("Thaumcraft", "securityViolation", "Fingerprint of jar file did not match.");
		FMLInterModComms.sendMessage("IC2", "securityViolation", "Fingerprint of jar file did not match.");
	}
	
	@IMCCallback
	public void processIMCMessages(IMCEvent event) {
		core.processIMCMessages(event.getMessages());
	}
}
