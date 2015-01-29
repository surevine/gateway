package com.surevine.community.gateway.audit.action;

import java.nio.file.Path;

import com.surevine.community.gateway.model.Destination;

/**
 * Factory to provide auditable actions
 *
 * @author jonnyheavey
 */
public interface AuditActionFactory {

	ImportAuditAction getImportAuditAction(String filename, String source);

	ExportAuditAction getExportAuditAction(String filename, Destination destination);

	RuleFailAuditAction getRuleFailAuditAction(Path source, Destination destination);

	SanitisationFailAuditAction getSanitisationFailAuditAction(Path source, Destination destination);

}
