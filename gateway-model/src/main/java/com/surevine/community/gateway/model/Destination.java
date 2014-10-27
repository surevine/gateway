package com.surevine.community.gateway.model;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

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
	private Set<String> projects;

	private boolean automatedReview;
	private long transferDelay;

	public Destination(final Long id, final String name, final URI uri, final Set<String> projects) {
		this.id = id;
		this.name = name;
		this.uri = uri;
		this.projects = projects;
	}

	public Destination(final Long id, final String name, final URI uri) {
		this.id = id;
		this.name = name;
		this.uri = uri;
		this.projects = new HashSet<String>();
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

	public void addProject(String projectName) {
		this.projects.add(projectName);
	}

}
