package com.surevine.community.gateway.model;

import lombok.Data;

@Data
public class Project {

	private String
		name,
		description,
		securityLabel,
		sourceUrl,
		webUrl;
	
	private boolean enabled;
}
