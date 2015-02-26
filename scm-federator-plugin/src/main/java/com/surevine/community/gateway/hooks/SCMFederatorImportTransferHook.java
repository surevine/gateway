package com.surevine.community.gateway.hooks;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.surevine.community.gateway.management.api.GatewayManagementServiceFacade;
import com.surevine.community.gateway.model.Repository;
import com.surevine.community.gateway.model.WhitelistedProject;

/**
 * Transfers compatible imported bundles to SCM federator component.
 *
 * @author jonnyheavey
 *
 */
public class SCMFederatorImportTransferHook implements GatewayImportTransferHook {

	private static final Logger LOG = Logger.getLogger(SCMFederatorImportTransferHook.class.getName());
	private static final String SCM_SOURCE_TYPE = "SCM";

	private final Properties config = new Properties();

	public SCMFederatorImportTransferHook() {
		try {
			getConfig().load(getClass().getResourceAsStream("/scm-federator-plugin.properties"));
		} catch (final IOException e) {
			LOG.log(Level.WARNING, "Failed to load SCM federation transfer hook configuration.", e);
		}
	}

	@Override
	public void call(final File[] received, final Map<String, String> properties) {

		for (final File element : received) {

			final MultipartEntity entity = buildImportedBundleRequestBody(element, properties);

			LOG.info("Transferring imported bundle to SCM federator. " + getConfig().getProperty("scm.federator.api.base.url") + "/incoming");

			try {
				Request.Post(getConfig().getProperty("scm.federator.api.base.url") + "/incoming").body(entity)
				.execute().returnContent().asString();
			} catch (final IOException e) {
				LOG.log(Level.SEVERE, "Failed to transfer bundle to SCM federator", e);
			}

			LOG.info("Bundle transfer to SCM federator complete.");

		}
	}

	@Override
	public boolean supports(final Map<String, String> properties) {

		LOG.info("Checking bundle support for SCM federation.");

		try {
			String sourceType = properties.get("source_type");
			if (sourceType == null) {
				sourceType = properties.get("SOURCE_TYPE");
			}
			LOG.info("Source type is: " + sourceType);
			boolean supported = sourceType.equalsIgnoreCase(SCM_SOURCE_TYPE);
			LOG.info("Does this class support this artifact? " + supported);

			if (supported) {
				if (!isWhitelisted(properties)) {
					LOG.info("artifact rejected as SCM project is not whitelisted for inbound federation from destination.");
					supported = false;
				}
			}

			return supported;
		} catch (final Exception e) {
			LOG.log(Level.INFO, "Exception during support method.", e);
			return false;
		}
	}

	/**
	 * Constructs body of post request to be sent to SCM federator
	 *
	 * @param bundle
	 *            SCM bundle to be sent to federator
	 * @param properties
	 *            metadata accompanying file
	 * @return
	 */
	private MultipartEntity buildImportedBundleRequestBody(final File bundle, final Map<String, String> properties) {

		LOG.info("Building multi-part request body for bundle transfer to SCM federator.");

		final MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		entity.addPart("bundle", new FileBody(bundle));

		for (final Entry<String, String> property : properties.entrySet()) {
			try {
				entity.addPart(property.getKey(), new StringBody(property.getValue()));
			} catch (final UnsupportedEncodingException e) {
				LOG.log(Level.WARNING, "Failed to read metadata property value during transfer to SCM federator.", e);
			}
		}
		return entity;
	}

	private Properties getConfig() {
		return config;
	}

	/**
	 * Determines whether repository has been whitelisted for inbound federation from
	 * source organisation (destination) via gateway
	 *
	 * @param properties
	 *            Properties from imported archive (representing SCM project)
	 * @return
	 */
	private boolean isWhitelisted(final Map<String, String> properties) {

		boolean isWhitelisted = false;

		Repository federatedInboundRepo = GatewayManagementServiceFacade.getInstance().
			getInboundFederatedRepository(properties.get("source_organisation"),
					properties.get("project") + "/" + properties.get("repo"),
					"SCM");

		if(federatedInboundRepo != null) {
			isWhitelisted = true;
		}

		LOG.info("Is SCM repository whitelisted for inbound federation from destination? " + isWhitelisted);
		return isWhitelisted;

	}

}
