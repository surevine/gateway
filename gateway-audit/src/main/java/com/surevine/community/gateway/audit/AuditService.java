package com.surevine.community.gateway.audit;

import com.surevine.community.gateway.audit.action.AuditAction;

public interface AuditService {

	/**
	 * Record a system action/event.
	 * @param action acton to audit
	 */
	public void audit(AuditAction action);

}
