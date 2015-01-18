package com.surevine.community.gateway.model;

import lombok.Getter;

/**
 * Represents an SCM project that has been
 * approved (whitelisted) for import.
 *
 * @author jonnyheavey
 *
 */
public class WhitelistedProject {

	@Getter
	private String sourceOrganisation;

	@Getter
	private String projectKey;

	@Getter
	private String repositorySlug;

	public WhitelistedProject(final String sourceOrganisation,
								final String projectKey,
								final String repositorySlug) {
		this.sourceOrganisation = sourceOrganisation;
		this.projectKey = projectKey;
		this.repositorySlug = repositorySlug;
	}

}
