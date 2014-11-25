package com.surevine.community.gateway.audit;

import java.io.File;
import java.util.Map;

import com.surevine.community.gateway.audit.action.ImportAuditAction;
import com.surevine.community.gateway.hooks.GatewayImportTransferHook;

/**
 * Audits import event for each item imported through gateway.
 *
 * @author jonnyheavey
 *
 */
public class ItemImportAuditHook implements GatewayImportTransferHook {

	@Override
	public void call(File[] received, Map<String, String> properties) {

		for(int i = 0; i < received.length; i++) {

			File receivedFile = received[i];
			String filename = receivedFile.getName();
			String source = properties.get("source");
			if(source == null) {
				source = "Unknown";
			}

			// TODO potentially include more details on file origin
			ImportAuditAction importAction = new ImportAuditAction(filename, source);
			GatewayXMLAuditServiceImpl.getInstance().audit(importAction);
		}

	}

	@Override
	public boolean supports(Map<String, String> properties) {
		// We always want to audit import
		return true;
	}

}
