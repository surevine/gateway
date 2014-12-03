package com.surevine.community.gateway.hooks;

import java.util.Map;
import java.util.Set;

import com.surevine.community.gateway.model.Destination;
import com.surevine.community.gateway.model.TransferItem;

/**
 * PreExportHook to apply SCM-federation specific logic
 * to TransferItem exports.
 * @author jonnyheavey
 */
public class SCMFederatorPreExportHook implements GatewayPreExportHook {

	private static final String SCM_SOURCE_TYPE = "SCM";

	@Override
	public void call(Set<TransferItem> transferQueue) {

		for (final TransferItem item : transferQueue) {
			if(isSourceControlItem(item.getMetadata())) {
				if(!isSharedProject(item.getDestination(), item.getMetadata())) {
					// Don't export project to destination (as its not shared)
					item.setNotExportable();
				}
			}
		}
	}

	/**
	 * Detect whether item sent from federated SCM system
	 * @param source String representing item source system
	 * @return
	 */
	private boolean isSourceControlItem(Map<String, String> metadata) {
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
	private boolean isSharedProject(Destination destination, Map<String, String> metadata) {

		String projectSlug = String.format("%s/%s", metadata.get("project"), metadata.get("repo"));
		Set<String> destinationSharedProjects = destination.getProjects();

		if(destinationSharedProjects != null) {
			if(destinationSharedProjects.contains(projectSlug)) {
				return true;
			}
		}
		return false;
	}

}
