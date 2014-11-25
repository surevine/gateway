package com.surevine.community.gateway.audit;

import com.surevine.community.gateway.audit.action.AuditAction;

public interface AuditService {

	public void audit(AuditAction action);

}
