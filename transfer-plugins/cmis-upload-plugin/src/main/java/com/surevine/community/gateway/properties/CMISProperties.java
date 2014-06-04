package com.surevine.community.gateway.properties;

import java.util.ResourceBundle;

public enum CMISProperties {

	HOST,
	PORT,
	USERNAME,
	PASSWORD,
	SITE,
	FOLDER,
	DESTINATIONS;
	
	private static final ResourceBundle BUNDLE;
	
	static {
		BUNDLE = ResourceBundle.getBundle("cmis");
	}
	
	public static String[] listDestinations() {
		return get(CMISProperties.DESTINATIONS).split(",");
	}
	
	public static String get(final CMISProperties property) {
		return BUNDLE.getString(String.format("cmis.%s", property.toString().toLowerCase().replaceAll("_", ".")));
	}
	
	public static String get(final String destination, final CMISProperties property) {
		return BUNDLE.getString(String.format("cmis.%s.%s", destination, property.toString().toLowerCase().replaceAll("_", ".")));
	}
}