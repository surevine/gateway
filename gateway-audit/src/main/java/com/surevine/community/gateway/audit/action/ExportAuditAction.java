package com.surevine.community.gateway.audit.action;

import com.surevine.community.gateway.model.Partner;

/**
 * Represents an item being exported through gateway.
 *
 * @author jonnyheavey
 *
 */
public abstract class ExportAuditAction implements AuditAction {

	protected String filename;
	protected Partner partner;

	public ExportAuditAction(String filename, Partner partner) {
		this.filename = filename;
		this.partner = partner;
	}

}
