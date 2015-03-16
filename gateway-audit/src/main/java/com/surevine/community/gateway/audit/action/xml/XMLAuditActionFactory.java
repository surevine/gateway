package com.surevine.community.gateway.audit.action.xml;

import java.nio.file.Path;

import com.surevine.community.gateway.audit.action.AuditActionFactory;
import com.surevine.community.gateway.audit.action.ExportAuditAction;
import com.surevine.community.gateway.audit.action.ImportAuditAction;
import com.surevine.community.gateway.audit.action.RuleFailAuditAction;
import com.surevine.community.gateway.audit.action.SanitisationFailAuditAction;
import com.surevine.community.gateway.model.Partner;

public class XMLAuditActionFactory implements AuditActionFactory {

	@Override
	public ImportAuditAction getImportAuditAction(String filename, String source) {
		return new XMLImportAuditAction(filename, source);
	}

	@Override
	public ExportAuditAction getExportAuditAction(String filename,
			Partner destination) {
		return new XMLExportAuditAction(filename, destination);
	}

	@Override
	public RuleFailAuditAction getRuleFailAuditAction(Path source,
			Partner destination) {
		return new XMLRuleFailAuditAction(source, destination);
	}

	@Override
	public SanitisationFailAuditAction getSanitisationFailAuditAction(
			Path source, Partner destination) {
		return new XMLSanitisationFailAuditAction(source, destination);
	}

}
