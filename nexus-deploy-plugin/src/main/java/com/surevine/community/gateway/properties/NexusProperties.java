package com.surevine.community.gateway.properties;

import java.util.ResourceBundle;

public enum NexusProperties {

	SCHEME,
	HOSTNAME,
	PORT,
	USERNAME,
	PASSWORD;
	
	private static final ResourceBundle BUNDLE;
	
	static {
		BUNDLE = ResourceBundle.getBundle("nexus");
	}
	
	public static String[] listDestinations() {
		return BUNDLE.getString("nexus.destinations").split(",");
	}
	
	public static String get(final String destination,
			final NexusProperties property) {
		return BUNDLE.getString(String.format("nexus.%s.%s", destination,
				property.toString().toLowerCase().replaceAll("_", ".")));
	}
}
