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

	private boolean automatedReview;
	private long transferDelay;

	public Destination(final Long id, final String name, final URI uri) {
		this.id = id;
		this.name = name;
		this.uri = uri;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		 if(!(obj instanceof Destination)) {
			 return false;
		 }
		 Destination oDest = (Destination) obj;
		 return id == oDest.id;
	}

}
