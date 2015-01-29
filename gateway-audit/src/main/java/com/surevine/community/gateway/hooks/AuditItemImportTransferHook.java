package com.surevine.community.gateway.hooks;

import java.io.File;
import java.util.Map;

import com.surevine.community.gateway.audit.Audit;
import com.surevine.community.gateway.audit.action.ImportAuditAction;
import com.surevine.community.gateway.hooks.GatewayImportTransferHook;

/**
 * Audits import event for each item imported through gateway.
 *
 * @author jonnyheavey
 *
 */
public class AuditItemImportTransferHook implements GatewayImportTransferHook {

	@Override
	public void call(File[] received, Map<String, String> properties) {

		for(int i = 0; i < received.length; i++) {

			File receivedFile = received[i];
			String filename = receivedFile.getName();
			String source = properties.get("source_organisation");
			if(source == null) {
				source = "Unknown (source metadata not set)";
			}

			ImportAuditAction importAction = Audit.getImportAuditAction(filename, source);
			Audit.audit(importAction);
		}

	}

	@Override
	public boolean supports(Map<String, String> properties) {
		// We always want to audit import
		return true;
	}

}
