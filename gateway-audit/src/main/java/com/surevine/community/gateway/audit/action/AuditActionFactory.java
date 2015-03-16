package com.surevine.community.gateway.audit.action;

import java.nio.file.Path;

import com.surevine.community.gateway.model.Partner;

/**
 * Factory to provide auditable actions
 *
 * @author jonnyheavey
 */
public interface AuditActionFactory {

	ImportAuditAction getImportAuditAction(String filename, String source);

	ExportAuditAction getExportAuditAction(String filename, Partner destination);

	RuleFailAuditAction getRuleFailAuditAction(Path source, Partner destination);

	SanitisationFailAuditAction getSanitisationFailAuditAction(Path source, Partner destination);

}
