package com.surevine.community.gateway.audit.action;

import java.nio.file.Path;

import com.surevine.community.gateway.model.Partner;

/**
 * Represents an export rule failure occurence.
 *
 * @author jonnyheavey
 *
 */
public abstract class RuleFailAuditAction implements AuditAction  {

	protected Path source;
	protected Partner partner;

	public RuleFailAuditAction(Path source, Partner partner) {
		this.source = source;
		this.partner = partner;
	}

}
