package com.surevine.community.gateway.audit;

import java.util.logging.Logger;

import com.surevine.community.gateway.audit.action.AuditAction;

/**
 * Audit Service implementation to record audit events
 * in default gateway logfile.
 *
 * @author jonnyheavey
 *
 */
public class LogAuditServiceImpl implements AuditService {

	private static final Logger LOG = Logger.getLogger(LogAuditServiceImpl.class.getName());

	@Override
	public void audit(AuditAction action) {
		LOG.info(action.serialize());
	}

}
