package com.surevine.community.gateway.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class ResteasyApplication extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	
	public ResteasyApplication() {
		singletons.add(new GatewayAPI());
		singletons.add(new GitlabAPI());
	}
	
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
