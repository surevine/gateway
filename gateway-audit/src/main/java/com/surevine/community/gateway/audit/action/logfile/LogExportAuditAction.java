package com.surevine.community.gateway.audit.action.logfile;

import com.surevine.community.gateway.audit.action.ExportAuditAction;
import com.surevine.community.gateway.model.Destination;

public class LogExportAuditAction extends ExportAuditAction {

	public LogExportAuditAction(String filename, Destination destination) {
		super(filename, destination);
	}

	@Override
	public String serialize() {
		return String.format("Item '%s' was exported to destination %s(%s).",
				filename,
				destination.getName(),
				destination.getUri());
	}

}
