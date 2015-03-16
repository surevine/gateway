package com.surevine.community.gateway.audit.action;

import java.nio.file.Path;

import com.surevine.community.gateway.model.Partner;

/**
 * Represents an sanitisation failure occurence.
 *
 * @author jonnyheavey
 *
 */
public abstract class SanitisationFailAuditAction implements AuditAction  {

	protected Path source;
	protected Partner destination;

	public SanitisationFailAuditAction(Path source, Partner destination) {
		this.source = source;
		this.destination = destination;
	}

}
