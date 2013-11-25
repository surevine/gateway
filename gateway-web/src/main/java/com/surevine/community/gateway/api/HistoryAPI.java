package com.surevine.community.gateway.api;

import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.collect.Lists;
import com.surevine.community.gateway.history.History;

@ManagedBean
@Path("/history")
public class HistoryAPI {

	@Inject
	private History history;

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, String>> listAll() {
		if (history != null) {
			System.out.println("CDI SUCCESS!!!!!!");
		} else {
			System.out.println("CDI FAIL :-(");
		}
		
		return Lists.reverse(History.getInstance().get());
	}
}
