package com.surevine.community.gateway.audit.action.logfile;

import java.nio.file.Path;

import com.surevine.community.gateway.audit.action.SanitisationFailAuditAction;
import com.surevine.community.gateway.model.Partner;

public class LogSanitisationFailAuditAction extends SanitisationFailAuditAction {

	public LogSanitisationFailAuditAction(Path source, Partner partner) {
		super(source, partner);
	}

	@Override
	public String serialize() {
		return String.format("Export of item '%s' to partner %s(%s) failed due to sanitisation failure.",
				source.toString(),
				partner.getName(),
				partner.getUri());
	}

}
