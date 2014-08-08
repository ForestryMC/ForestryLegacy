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

import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;
import forestry.core.config.Defaults;

public class ProxyLog {

	/* LOGGING */
	private static Logger forestryLogger;
	
	private void initLogger() {
		forestryLogger = Logger.getLogger(Defaults.MOD);
		forestryLogger.setParent(FMLLog.getLogger());
	}

	/* FINEST */
	public void finest(String message) {
		log(Level.FINEST, message);
	}

	public void finest(String message, Object param) {
		log(Level.FINEST, message, param);
	}

	public void finest(String message, Object... params) {
		log(Level.FINEST, message, params);
	}

	/* FINER */
	public void finer(String message) {
		log(Level.FINER, message);
	}

	public void finer(String message, Object param) {
		log(Level.FINER, message, param);
	}

	public void finer(String message, Object... params) {
		log(Level.FINER, message, params);
	}
	
	/* FINE */
	public void fine(String message) {
		log(Level.FINE, message);
	}

	public void fine(String message, Object param) {
		log(Level.FINE, message, param);
	}

	public void fine(String message, Object... params) {
		log(Level.FINE, message, params);
	}
	
	/* INFO */
	public void info(String message) {
		log(Level.INFO, message);
	}

	public void info(String message, Object param) {
		log(Level.INFO, message, param);
	}

	public void info(String message, Object... params) {
		log(Level.INFO, message, params);
	}

	/* WARNING */
	public void warning(String message) {
		log(Level.WARNING, message);
	}

	public void warning(String message, Object param) {
		log(Level.WARNING, message, param);
	}

	public void warning(String message, Object... params) {
		log(Level.WARNING, message, params);
	}

	/* SEVERE */
	public void severe(String message) {
		log(Level.SEVERE, message);
	}

	public void severe(String message, Object param) {
		log(Level.SEVERE, message, param);
	}

	public void severe(String message, Object... params) {
		log(Level.SEVERE, message, params);
	}

	/* GENERIC */
	public void log(Level logLevel, String message) {
		if(forestryLogger == null)
			initLogger();
		
		forestryLogger.log(logLevel, message);
	}

	public void log(Level logLevel, String message, Object... params) {
		log(logLevel, String.format(message, params));
	}

	public void log(Level logLevel, String message, Object param) {
		log(logLevel, String.format(message, param));
	}
	
}
