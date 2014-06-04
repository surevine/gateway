package com.surevine.community.gateway.properties;

import java.util.ResourceBundle;

public enum AlfrescoProperties {

	HOST,
	PORT,
	USERNAME,
	PASSWORD,
	SITE,
	FOLDER,
	DESTINATIONS;
	
	private static final ResourceBundle BUNDLE;
	
	static {
		BUNDLE = ResourceBundle.getBundle("alfresco");
	}
	
	public static String[] listDestinations() {
		return get(AlfrescoProperties.DESTINATIONS).split(",");
	}
	
	public static String get(final AlfrescoProperties property) {
		return BUNDLE.getString(String.format("alfresco.%s", property.toString().toLowerCase().replaceAll("_", ".")));
	}
	
	public static String get(final String destination, final AlfrescoProperties property) {
		return BUNDLE.getString(String.format("alfresco.%s.%s", destination, property.toString().toLowerCase().replaceAll("_", ".")));
	}
}