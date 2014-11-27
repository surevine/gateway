package com.surevine.community.gateway.audit;

public class AuditServiceException extends RuntimeException {

	public AuditServiceException(final String message) {
        super(message);
    }

    public AuditServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AuditServiceException(final Throwable cause) {
        super(cause);
    }

}
