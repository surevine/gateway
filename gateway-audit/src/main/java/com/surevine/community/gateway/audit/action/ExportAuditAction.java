package com.surevine.community.gateway.audit.action;

import com.surevine.community.gateway.model.Destination;

/**
 * Represents an item being exported through gateway.
 *
 * @author jonnyheavey
 *
 */
public abstract class ExportAuditAction implements AuditAction {

	protected String filename;
	protected Destination destination;

	public ExportAuditAction(String filename, Destination destination) {
		this.filename = filename;
		this.destination = destination;
	}

}
