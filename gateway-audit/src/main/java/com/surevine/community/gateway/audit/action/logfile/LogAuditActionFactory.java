package com.surevine.community.gateway.audit.action.logfile;

import java.nio.file.Path;

import com.surevine.community.gateway.audit.action.AuditActionFactory;
import com.surevine.community.gateway.audit.action.ExportAuditAction;
import com.surevine.community.gateway.audit.action.ImportAuditAction;
import com.surevine.community.gateway.audit.action.RuleFailAuditAction;
import com.surevine.community.gateway.audit.action.SanitisationFailAuditAction;
import com.surevine.community.gateway.model.Destination;

public class LogAuditActionFactory implements AuditActionFactory {

	@Override
	public ImportAuditAction getImportAuditAction(String filename, String source) {
		return new LogImportAuditAction(filename, source);
	}

	@Override
	public ExportAuditAction getExportAuditAction(String filename,
			Destination destination) {
		return new LogExportAuditAction(filename, destination);
	}

	@Override
	public RuleFailAuditAction getRuleFailAuditAction(Path source,
			Destination destination) {
		return new LogRuleFailAuditAction(source, destination);
	}

	@Override
	public SanitisationFailAuditAction getSanitisationFailAuditAction(
			Path source, Destination destination) {
		return new LogSanitisationFailAuditAction(source, destination);
	}
}
