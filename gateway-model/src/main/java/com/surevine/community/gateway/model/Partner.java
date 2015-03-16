package com.surevine.community.gateway.model;

import java.net.URI;

import lombok.Data;
import lombok.Getter;

@Data
public class Partner {

	@Getter
	private Long id;

	@Getter
	private String name;

	@Getter
	private URI uri;

	@Getter
	private String sourceKey;

	public Partner(final Long id, final String name, final URI uri, final String sourceKey) {
		this.id = id;
		this.name = name;
		this.uri = uri;
		this.sourceKey = sourceKey;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Partner)) {
			return false;
		}
		final Partner oDest = (Partner) obj;
		return id == oDest.id;
	}

}
