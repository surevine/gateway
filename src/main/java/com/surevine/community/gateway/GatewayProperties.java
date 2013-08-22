package com.surevine.community.gateway;

import java.util.ResourceBundle;

public enum GatewayProperties {

	GITLAB_PROTOCOL,
	GITLAB_HOST,
	GITLAB_PORT,
	GITLAB_CONTEXT,
	GITLAB_VERSION,
	GITLAB_TOKEN;
	
	private static final ResourceBundle BUNDLE;
	
	static {
		BUNDLE = ResourceBundle.getBundle("gateway");
	}
	
	public static String get(final GatewayProperties property) {
		return BUNDLE.getString(String.format("gateway.%s", property.toString().toLowerCase().replaceAll("_", ".")));
	}
}
