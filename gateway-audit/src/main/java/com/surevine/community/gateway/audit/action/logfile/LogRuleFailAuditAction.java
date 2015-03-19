package com.surevine.community.gateway.audit.action.logfile;

import java.nio.file.Path;

import com.surevine.community.gateway.audit.action.RuleFailAuditAction;
import com.surevine.community.gateway.model.Partner;

public class LogRuleFailAuditAction extends RuleFailAuditAction {

	public LogRuleFailAuditAction(Path source, Partner partner) {
		super(source, partner);
	}

	@Override
	public String serialize() {
		return String.format("Export of item '%s' to partner %s(%s) failed due to rule failure.",
				source.toString(),
				partner.getName(),
				partner.getUri());
	}

}
