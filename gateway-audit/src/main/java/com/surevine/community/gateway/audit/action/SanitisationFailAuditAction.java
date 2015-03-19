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
	protected Partner partner;

	public SanitisationFailAuditAction(Path source, Partner partner) {
		this.source = source;
		this.partner = partner;
	}

}
