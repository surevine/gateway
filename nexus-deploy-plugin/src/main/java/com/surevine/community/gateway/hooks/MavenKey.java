package com.surevine.community.gateway.hooks;

public enum MavenKey {
	GROUP_ID("groupId"),
	ARTIFACT_ID("artifactId"),
	VERSION("version"),
	CLASSIFIER("classifier"),
	FILE("file"),
	REPOSITORY_ID("repositoryId"),
	SOURCE_TYPE("source_type"),
	URL("url");
	
	private String token;
	
	private MavenKey(final String token) {
		this.token = token;
	}
	
	public String toString() {
		return token;
	}
}
