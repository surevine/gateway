package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import com.surevine.community.gateway.model.Destination;
import com.surevine.community.gateway.model.TransferItem;
import com.surevine.community.gateway.sanitisation.SanitisationResult;
import com.surevine.community.gateway.sanitisation.SanitisationServiceFacade;

/**
 * PreExportHook to apply SCM-federation specific logic
 * to TransferItem exports.
 * @author jonnyheavey
 */
public class SCMFederatorPreExportHook implements GatewayPreExportHook {

	private static final Logger LOG = Logger.getLogger(SCMFederatorPreExportHook.class.getName());
	private static final String SCM_SOURCE_TYPE = "SCM";

	private Properties config = new Properties();

	public SCMFederatorPreExportHook() {
		try {
			getConfig().load(getClass().getResourceAsStream("/scm-federator-plugin.properties"));
		} catch (IOException e) {
			LOG.warning("Failed to load SCM federation export hook configuration.");
			e.printStackTrace();
		}
	}

	@Override
	public void call(Set<TransferItem> transferQueue) {

		for (final TransferItem item : transferQueue) {
			if(isSourceControlItem(item.getMetadata())) {
				if(!isSharedProject(item.getDestination(), item.getMetadata())) {
					// Don't export project to destination (as its not shared)
					item.setNotExportable();
				}

				if(isSanitisationEnabled()) {
					sanitise(item);
				}

			}
		}
	}

	private Properties getConfig() {
		return config;
	}

	/**
	 * Reads configuration to determine if sanitisation has been enabled for exports
	 * @return
	 */
	private Boolean isSanitisationEnabled() {
		return Boolean.parseBoolean(getConfig().getProperty("sanitisation.enabled"));
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

	/**
	 * Confirms item is safe to export.
	 * @param item TransferItem to be sanitised
	 */
	private void sanitise(final TransferItem item) {

		Map<String, String> metadata = item.getMetadata();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
		SanitisationResult result = SanitisationServiceFacade.getInstance().isSane(item.getSource(),
																					metadata.get("project"),
																					metadata.get("repo"),
																					"Export-"+dateFormat.format(new Date()));
		if(!result.isSane()) {
			// Don't export item as sanitisation rejected
			item.setNotExportable();
			LOG.warning(String.format("Export of item '%s' to destination '%s' prevented by sanitisation service. Reasons:",
					item.getSource(),
					item.getDestination().getName()));
			for(String error: result.getErrors()) {
				LOG.warning(error);
			}
		}
	}

}
