package com.surevine.community.gateway.audit.action.xml;

import java.nio.file.Path;

import com.surevine.community.gateway.audit.action.AuditActionFactory;
import com.surevine.community.gateway.audit.action.ExportAuditAction;
import com.surevine.community.gateway.audit.action.ImportAuditAction;
import com.surevine.community.gateway.audit.action.RuleFailAuditAction;
import com.surevine.community.gateway.model.Destination;

public class XMLAuditActionFactory implements AuditActionFactory {

	@Override
	public ImportAuditAction getImportAuditAction(String filename, String source) {
		return new XMLImportAuditAction(filename, source);
	}

	@Override
	public ExportAuditAction getExportAuditAction(String filename,
			Destination destination) {
		return new XMLExportAuditAction(filename, destination);
	}

	@Override
	public RuleFailAuditAction getRuleFailAuditAction(Path source,
			Destination destination) {
		return new XMLRuleFailAuditAction(source, destination);
	}

}
