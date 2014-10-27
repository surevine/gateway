package com.surevine.community.gateway.scm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.surevine.community.gateway.model.Destination;

/**
 * Manages the sharing of SCM projects across the gateway
 *
 * @author jonnyheavey
 *
 */
public class FederatedSCMManager {

	private static final String SCM_SOURCE_TYPE = "SCM";

	/**
	 * Detect whether item sent from federated SCM system
	 * @param source String representing item source system
	 * @return
	 */
	public boolean isSourceControlItem(Map<String, String> metadata) {
		if(metadata.containsKey("source_type")) {
			if(metadata.get("source_type").equals(SCM_SOURCE_TYPE)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines whether project has been configured to be shared with destination
	 * @param destination destination being shared to
	 * @param projectName project to be shared
	 * @return
	 */
	public boolean isSharedProject(Destination destination, Map<String, String> metadata) {

		String projectSlug = String.format("%s/%s", metadata.get("project"), metadata.get("repo"));
		Set<String> destinationSharedProjects = destination.getProjects();

		if(destinationSharedProjects.contains(projectSlug)) {
			return true;
		}
		return false;
	}

}
