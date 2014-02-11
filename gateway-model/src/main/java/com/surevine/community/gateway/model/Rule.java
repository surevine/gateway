package com.surevine.community.gateway.model;

import java.util.logging.Logger;

import lombok.Getter;

public class Rule {
	
	private static final Logger LOG = Logger.getLogger(Rule.class.getName());
	
	@Getter
	private boolean allowed = true;
	
	public void mandate(final boolean condition) {
		if (!condition) {
			allowed = false;
		}
	}
	
	public void mandate(final boolean condition, final String message) {
		mandate(condition);
		
		if (!condition) {
			LOG.warning(message);
		}
	}
}
