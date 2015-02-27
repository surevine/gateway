package com.surevine.community.gateway.hooks;

import java.util.Set;

import com.surevine.community.gateway.audit.Audit;
import com.surevine.community.gateway.audit.action.ExportAuditAction;
import com.surevine.community.gateway.hooks.GatewayExportTransferHook;
import com.surevine.community.gateway.model.Partner;
import com.surevine.community.gateway.model.TransferItem;

/**
 * Audits export event for each exported item
 *
 * @author jonnyheavey
 *
 */
public class AuditItemExportTransferHook implements GatewayExportTransferHook {

	public void call(final Set<TransferItem> transferQueue) {

		for (final TransferItem item : transferQueue) {

			if(item.isExportable()) {
				Partner partner = item.getPartner();
				String filename = item.getSource().getFileName().toString();

				ExportAuditAction exportAction = Audit.getExportAuditAction(filename, partner);
				Audit.audit(exportAction);
			}

		}

	}

}