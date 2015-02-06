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

	@Getter
	private Set<String> issueProjects;

	private boolean automatedReview;
	private long transferDelay;

	public Destination(final Long id, final String name, final URI uri, final Set<String> projects,
			final Set<String> issueProjects) {
		this.id = id;
		this.name = name;
		this.uri = uri;
		this.projects = projects;
		this.issueProjects = issueProjects;
	}

	public Destination(final Long id, final String name, final URI uri) {
		this.id = id;
		this.name = name;
		this.uri = uri;
		this.projects = new HashSet<String>();
		this.issueProjects = new HashSet<String>();
	}

	public void addProject(final String projectName) {
		this.projects.add(projectName);
	}

	public void addIssueProject(final String projectName) {
		this.issueProjects.add(projectName);
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
