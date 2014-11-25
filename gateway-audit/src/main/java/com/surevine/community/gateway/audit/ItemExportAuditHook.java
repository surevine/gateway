package com.surevine.community.gateway.audit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import com.surevine.community.gateway.audit.action.ExportAuditAction;
import com.surevine.community.gateway.hooks.GatewayExportTransferHook;
import com.surevine.community.gateway.model.Destination;
import com.surevine.community.gateway.model.TransferItem;

/**
 * Audits export event for each exported item
 *
 * @author jonnyheavey
 *
 */
public class ItemExportAuditHook implements GatewayExportTransferHook {

	public void call(final Set<TransferItem> transferQueue) {

		for (final TransferItem item : transferQueue) {

			if(item.isExportable()) {
				Destination destination = item.getDestination();
				String filename = item.getSource().getFileName().toString();
				ExportAuditAction exportAction = new ExportAuditAction(filename, destination);

				GatewayXMLAuditServiceImpl.getInstance().audit(exportAction);
			}

		}

	}

}