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

	@Override
	public int hashCode() {
		String uid = sourceOrganisation + ":" +
					projectKey + "/" +
					repositorySlug;
		return uid.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		 if(!(obj instanceof WhitelistedProject)) {
			 return false;
		 }
		 WhitelistedProject oProj = (WhitelistedProject) obj;

		 return (sourceOrganisation.equalsIgnoreCase(oProj.sourceOrganisation)) &&
				 (projectKey.equalsIgnoreCase(oProj.projectKey)) &&
				 (repositorySlug.equalsIgnoreCase(oProj.repositorySlug));
	}

}
