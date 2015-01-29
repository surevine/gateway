package com.surevine.community.gateway.audit;

public enum AuditMode {

	LOG,
	XML,
	UNKNOWN;

	public static AuditMode getMode(final String mode) {

		if(mode.equalsIgnoreCase("xml")) {
			return XML;
		} else if(mode.equalsIgnoreCase("log")) {
			return LOG;
		} else {
			return UNKNOWN;
		}

	}

}
