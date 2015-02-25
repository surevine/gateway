package com.surevine.community.gateway.model;

import lombok.Getter;

/**
 * Represents an repository configured for federation
 * in the management console.
 *
 * @author jonnyheavey
 *
 */
public class Repository {

	// TODO replace String type with enum
	@Getter
	private final String repoType;

	@Getter
	private final String identifier;

	public Repository(String repoType, String identifier) {
		this.repoType = repoType;
		this.identifier = identifier;
	}

	@Override
	public int hashCode() {
		final String uid = repoType + ":" + identifier;
		return uid.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Repository)) {
			return false;
		}
		final Repository oRepo = (Repository) obj;

		return (repoType.equalsIgnoreCase(oRepo.repoType))
				&& (identifier.equalsIgnoreCase(oRepo.identifier));
	}

}
