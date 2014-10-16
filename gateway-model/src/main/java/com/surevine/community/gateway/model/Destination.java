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
	private URI url;

	private boolean automatedReview;
	private long transferDelay;

	public Destination(final Long id, final String name, final URI url) {
		this.id = id;
		this.name = name;
		this.url = url;
	}

}
