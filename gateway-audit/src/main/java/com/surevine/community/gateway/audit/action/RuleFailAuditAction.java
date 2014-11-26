package com.surevine.community.gateway.audit.action;

import java.nio.file.Path;

import com.surevine.community.gateway.model.Destination;

/**
 * Represents an export rule failure occurence.
 *
 * @author jonnyheavey
 *
 */
public abstract class RuleFailAuditAction implements AuditAction  {

	protected Path source;
	protected Destination destination;

	public RuleFailAuditAction(Path source, Destination destination) {
		this.source = source;
		this.destination = destination;
	}

}
