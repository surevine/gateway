package com.surevine.community.gateway.model;

import lombok.Getter;

public class Rule {
	@Getter
	private boolean allowed = true;
	
	public void mandate(final boolean condition) {
		if (!condition) {
			allowed = false;
		}
	}
}
