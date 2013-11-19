package com.surevine.community.gateway.model;

import lombok.Data;

import com.surevine.community.gateway.model.util.SecurityLabel;

@Data
public class Project {

	private String
		id,
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
