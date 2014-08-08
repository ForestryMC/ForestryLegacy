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
package forestry.mail;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import forestry.api.core.INBTTagable;

public interface ILetter extends IInventory, INBTTagable {

	ItemStack[] getPostage();

	void setProcessed(boolean flag);

	boolean isProcessed();

	boolean isMailable();

	void setSender(MailAddress address);

	MailAddress getSender();

	boolean hasRecipient();

	void setRecipient(MailAddress address);

	MailAddress[] getRecipients();

	String getRecipientString();

	void setText(String text);

	String getText();

	void addTooltip(List list);

	boolean isPostPaid();

	int requiredPostage();

	void invalidatePostage();

	ItemStack[] getAttachments();

	void addAttachment(ItemStack itemstack);

	void addAttachments(ItemStack[] itemstacks);

	int countAttachments();

	void addStamps(ItemStack stamps);

}
