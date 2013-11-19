package com.surevine.community.gateway.model;

import java.net.URI;

import lombok.Data;

@Data
public class Destination {

	private String name;
	private URI location;
	private boolean automatedReview;
	private long transferDelay;
}
