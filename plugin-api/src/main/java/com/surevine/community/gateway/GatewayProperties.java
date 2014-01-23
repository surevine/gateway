package com.surevine.community.gateway;

import java.util.ResourceBundle;

public enum GatewayProperties {

	GITLAB_PROTOCOL,
	GITLAB_HOST,
	GITLAB_PORT,
	GITLAB_CONTEXT,
	GITLAB_VERSION,
	GITLAB_TOKEN,
	EXPORT_QUARANTINE_DIR,
	EXPORT_DESTINATIONS,
	IMPORT_WATCH_DIR,
	IMPORT_WORKING_DIR,
	IMPORT_CLEANUP;
	
	private static final ResourceBundle BUNDLE;
	
	static {
		BUNDLE = ResourceBundle.getBundle("gateway");
	}
	
	public static String get(final GatewayProperties property) {
		return BUNDLE.getString(String.format("gateway.%s", property.toString().toLowerCase().replaceAll("_", ".")));
	}
	
	public static boolean doCleanUp() {
		return !get(GatewayProperties.IMPORT_CLEANUP).equalsIgnoreCase("false");
	}
}
