package com.surevine.community.gateway.audit.action.logfile;

import java.nio.file.Path;

import com.surevine.community.gateway.audit.action.SanitisationFailAuditAction;
import com.surevine.community.gateway.model.Partner;

public class LogSanitisationFailAuditAction extends SanitisationFailAuditAction {

	public LogSanitisationFailAuditAction(Path source, Partner destination) {
		super(source, destination);
	}

	@Override
	public String serialize() {
		return String.format("Export of item '%s' to destination %s(%s) failed due to sanitisation failure.",
				source.toString(),
				destination.getName(),
				destination.getUri());
	}

}
