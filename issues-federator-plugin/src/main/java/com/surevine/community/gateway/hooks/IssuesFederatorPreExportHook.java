package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.surevine.community.gateway.audit.Audit;
import com.surevine.community.gateway.audit.action.SanitisationFailAuditAction;
import com.surevine.community.gateway.management.api.GatewayManagementServiceFacade;
import com.surevine.community.gateway.model.Partner;
import com.surevine.community.gateway.model.Repository;
import com.surevine.community.gateway.model.TransferItem;
import com.surevine.community.sanitisation.SanitisationException;
import com.surevine.community.sanitisation.SanitisationResult;
import com.surevine.community.sanitisation.SanitisationServiceFacade;

/**
 * PreExportHook to apply issues federation specific logic
 * to TransferItem exports.
 */
public class IssuesFederatorPreExportHook implements GatewayPreExportHook {

	private static final Logger LOG = Logger.getLogger(IssuesFederatorPreExportHook.class.getName());
	private static final String SOURCE_TYPE = "Issues";
	private static final String SINGLE_DISTRIBUTION_TYPE = "single_distribution";

	private final Properties config = new Properties();

	public IssuesFederatorPreExportHook() {
		try {
			getConfig().load(getClass().getResourceAsStream("/issues-federator-plugin.properties"));
		} catch (final IOException e) {
			LOG.log(Level.WARNING, "Failed to load issues federation export hook configuration.", e);
		}
	}

	@Override
	public void call(final Set<TransferItem> transferQueue) {

		for (final TransferItem item : transferQueue) {
			if (isItemAppropriate(item.getMetadata())) {
				if (!isSharedProject(item.getPartner(), item.getMetadata())) {
					// Don't export project to destination (as its not shared)
					item.setNotExportable();
				}

				if (isSanitisationEnabled()) {
					sanitise(item);
				}

			}
		}
	}

	private Properties getConfig() {
		return config;
	}

	/**
	 * Reads configuration to determine if sanitisation has been enabled for
	 * exports
	 *
	 * @return
	 */
	private Boolean isSanitisationEnabled() {
		return Boolean.parseBoolean(getConfig().getProperty("issues.sanitisation.enabled"));
	}

	/**
	 * Detect whether item sent from federated issues system
	 *
	 * @param source
	 *            String representing item source system
	 * @return
	 */
	private boolean isItemAppropriate(final Map<String, String> metadata) {
		if (metadata.containsKey("source_type")) {
			if (metadata.get("source_type").equals(SOURCE_TYPE)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines whether project has been configured to be shared with
	 * destination
	 *
	 * @param partner partner being shared to
	 * @param projectName project to be shared
	 * @return
	 */
	private boolean isSharedProject(final Partner partner, final Map<String, String> metadata) {

		boolean isShared = false;

		if(metadata.get("distribution_type").equalsIgnoreCase(SINGLE_DISTRIBUTION_TYPE)) {
			isShared = partner.getSourceKey().equalsIgnoreCase(metadata.get("limit_distribution_to"));
		} else {
			String repoIdentifier = metadata.get("project");
			Repository federatedOutboundRepo = GatewayManagementServiceFacade.getInstance().
					getOutboundFederatedRepository(partner, "ISSUE", repoIdentifier);

			if(federatedOutboundRepo != null) {
				isShared = true;
			}
		}

		return isShared;
	}

	/**
	 * Confirms item is safe to export.
	 *
	 * @param item TransferItem to be sanitised
	 */
	private void sanitise(final TransferItem item) {

		final Map<String, String> metadata = item.getMetadata();
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");


		SanitisationResult result;
		try {
			result = SanitisationServiceFacade.getInstance(getConfig().getProperty("sanitisation.service.base.url"))
					.isSane(item.getSource(),
								"ISSUE",
								metadata.get("project"),
								"ISSUE-EXPORT-" + dateFormat.format(new Date()),
								Collections.<String, String> emptyMap());
		} catch (UnsupportedEncodingException | SanitisationException e) {

			// Don't export item as sanitisation failed
			item.setNotExportable();

			LOG.log(Level.WARNING, String.format("Export of item '%s' to partner '%s' prevented due to sanitisation service failure.",
					item.getSource(),
					item.getPartner().getName()), e);

			return;
		}

		if (!result.isSane()) {
			// Don't export item as sanitisation rejected
			item.setNotExportable();
			LOG.warning(String.format(
					"Export of item '%s' to partner '%s' prevented by sanitisation service. Reasons:",
					item.getSource(), item.getPartner().getName()));
			for (final String error : result.getErrors()) {
				LOG.warning(error);
			}

			// Audit failure
			final SanitisationFailAuditAction action = Audit.getSanitisationFailAuditAction(item.getSource(),
					item.getPartner());
			Audit.audit(action);
		}
	}

}
