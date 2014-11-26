package com.surevine.community.gateway.util;

import java.nio.file.Path;

import com.surevine.community.gateway.audit.action.AuditActionFactory;
import com.surevine.community.gateway.audit.action.ExportAuditAction;
import com.surevine.community.gateway.audit.action.ImportAuditAction;
import com.surevine.community.gateway.audit.action.RuleFailAuditAction;
import com.surevine.community.gateway.model.Destination;

public class MockAuditActionFactory implements AuditActionFactory {

	@Override
	public ImportAuditAction getImportAuditAction(String filename, String source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExportAuditAction getExportAuditAction(String filename,
			Destination destination) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RuleFailAuditAction getRuleFailAuditAction(Path source,
			Destination destination) {
		// TODO Auto-generated method stub
		return null;
	}

}
