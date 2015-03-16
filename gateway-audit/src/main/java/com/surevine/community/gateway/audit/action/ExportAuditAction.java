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
	protected Partner destination;

	public ExportAuditAction(String filename, Partner destination) {
		this.filename = filename;
		this.destination = destination;
	}

}
