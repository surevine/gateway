package com.surevine.community.gateway.audit.action;

/**
 * Represents an item being imported via Gateway.
 *
 * @author jonnyheavey
 *
 */
public abstract class ImportAuditAction implements AuditAction {

	protected String filename;
	protected String source;

	public ImportAuditAction(String filename, String source) {
		this.filename = filename;
		this.source = source;
	}

}
