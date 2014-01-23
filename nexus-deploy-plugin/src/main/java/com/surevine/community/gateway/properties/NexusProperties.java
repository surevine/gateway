package com.surevine.community.gateway.properties;

import java.util.ResourceBundle;

public enum NexusProperties {

	SCHEME,
	HOSTNAME,
	PORT,
	USERNAME,
	PASSWORD,
	DEPLOY_SCRIPT,
	DESTINATIONS;
	
	private static final ResourceBundle BUNDLE;
	
	static {
		BUNDLE = ResourceBundle.getBundle("nexus");
	}
	
	public static String[] listDestinations() {
		return get(NexusProperties.DESTINATIONS).split(",");
	}
	
	public static String get(final NexusProperties property) {
		return BUNDLE.getString(String.format("nexus.%s",
				property.toString().toLowerCase().replaceAll("_", ".")));
	}
	
	public static String get(final String destination,
			final NexusProperties property) {
		return BUNDLE.getString(String.format("nexus.%s.%s", destination,
				property.toString().toLowerCase().replaceAll("_", ".")));
	}
}
