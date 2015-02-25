package com.surevine.community.gateway.model;

import java.net.URI;

import lombok.Data;
import lombok.Getter;

@Data
public class Destination {

	@Getter
	private Long id;

	@Getter
	private String name;

	@Getter
	private URI uri;

	@Getter
	private String sourceKey;

	private boolean automatedReview;
	private long transferDelay;

	public Destination(final Long id, final String name, final URI uri) {
		this.id = id;
		this.name = name;
		this.uri = uri;
	}

	public Destination(final Long id, final String name, final URI uri, final String sourceKey) {
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
		if (!(obj instanceof Destination)) {
			return false;
		}
		final Destination oDest = (Destination) obj;
		return id == oDest.id;
	}

}
