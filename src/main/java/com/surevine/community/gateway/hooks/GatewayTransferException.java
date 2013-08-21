package com.surevine.community.gateway.hooks;

public class GatewayTransferException extends Throwable {

	private static final long serialVersionUID = 8444224129592560808L;
	
	public GatewayTransferException(final String message, final Throwable t) {
		super(message, t);
	}
}
