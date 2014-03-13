package com.surevine.community.gateway.hooks;

import java.util.Set;
import java.util.logging.Logger;

import com.surevine.community.gateway.model.TransferItem;

/**
 * Passes imported file details to shell code.
 * 
 * Could be used for custom scp, ftp, etc. transfers.
 * 
 * @author rich.midwinter@gmail.com
 */
public class ShellExportTransferHook implements GatewayExportTransferHook {
	
	private static final Logger LOG = Logger.getLogger(ShellExportTransferHook.class.getName());

	public void call(final Set<TransferItem> transferQueue) {
		for (final TransferItem item : transferQueue) {
			LOG.info(String.format("Calling out to shell code for URI %s with source %s",
					item.getDestination(), item.getSource()));
			if (item.isExportable()) {
				// Delegate to shell script
			}
		}
	}
}
