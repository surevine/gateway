package com.surevine.community.gateway.model;

import com.surevine.community.gateway.SecurityLabel;

import lombok.Data;

@Data
public class Project {

	private String
		name,
		description,
		friendlySecurityLabel,
		securityLabel,
		sourceUrl,
		webUrl;
	
	private boolean enabled;
	
	public void setSecurityLabel(final String securityLabel) {
		this.securityLabel = securityLabel;
		
		this.friendlySecurityLabel = SecurityLabel.asFriendlyString(securityLabel);
	}
}
