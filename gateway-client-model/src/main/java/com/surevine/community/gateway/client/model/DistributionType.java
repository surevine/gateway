package com.surevine.community.gateway.client.model;

public enum DistributionType {
	SINGLE_DISTRIBUTION("single_distribution"), DISTRIBUTE_TO_ALL_PERMITTED("distribute_to_all_permitted");

	private final String friendlyName;

	private DistributionType(final String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getFriendlyName() {
		return friendlyName;
	}
}
