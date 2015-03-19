package com.surevine.community.gateway.audit.action.logfile;

import com.surevine.community.gateway.audit.action.ExportAuditAction;
import com.surevine.community.gateway.model.Partner;

public class LogExportAuditAction extends ExportAuditAction {

	public LogExportAuditAction(String filename, Partner partner) {
		super(filename, partner);
	}

	@Override
	public String serialize() {
		return String.format("Item '%s' was exported to partner %s(%s).",
				filename,
				partner.getName(),
				partner.getUri());
	}

}
