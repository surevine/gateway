package com.surevine.community.gateway.audit.action.logfile;

import com.surevine.community.gateway.audit.action.ImportAuditAction;

public class LogImportAuditAction extends ImportAuditAction {

	public LogImportAuditAction(String filename, String source) {
		super(filename, source);
	}

	@Override
	public String serialize() {
		return String.format("Item '%s' was imported from '%s'.",
				filename,
				source);
	}

}
