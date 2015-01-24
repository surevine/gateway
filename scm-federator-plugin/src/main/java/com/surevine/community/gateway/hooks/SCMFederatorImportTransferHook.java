package com.surevine.community.gateway.hooks;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
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

	private Properties config = new Properties();

	public SCMFederatorImportTransferHook() {
		try {
			getConfig().load(getClass().getResourceAsStream("/scm-federation.properties"));
		} catch (IOException e) {
			LOG.warning("Failed to load SCM federation transfer hook configuration.");
			e.printStackTrace();
		}
	}

	@Override
	public void call(File[] received, Map<String, String> properties) {

		for (int i = 0; i < received.length; i++) {

			MultipartEntity entity = buildImportedBundleRequestBody(received[i], properties);

			LOG.info("Transferring imported bundle to SCM federator.");

			try {
				Request.Post(getConfig().getProperty("scm.federator.api.base.url") + "/incoming")
				.body(entity)
				.execute().returnContent().asString();
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Failed to transfer bundle to SCM federator", e);
			}

			LOG.info("Bundle transfer to SCM federator complete.");

		}
	}

	@Override
	public boolean supports(Map<String, String> properties) {

		LOG.info("Checking bundle support for SCM federation.");

		try {
			String sourceType=properties.get("source_type");
			if (sourceType==null) {
				sourceType=properties.get("SOURCE_TYPE");
			}
			LOG.info("Source type is: "+sourceType);
			boolean supported = sourceType.equalsIgnoreCase(SCM_SOURCE_TYPE);
			LOG.info("Does this class support this artifact? "+supported);

			// Additionally ensure SCM project is whitelisted
			if(!isWhitelisted(properties)) {
				LOG.info("artifact rejected as SCM project is not whitelisted.");
				supported = false;
			}

			return supported;
		}
		catch (Exception e) {
			LOG.info("Exception during support method: "+e);
			return false;
		}
	}

	/**
	 * Constructs body of post request to be sent to SCM federator
	 *
	 * @param bundle SCM bundle to be sent to federator
	 * @param properties metadata accompanying file
	 * @return
	 */
	private MultipartEntity buildImportedBundleRequestBody(File bundle,
			Map<String, String> properties) {

		LOG.info("Building multi-part request body for bundle transfer to SCM federator.");

		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		entity.addPart("bundle", new FileBody(bundle));

		Iterator<Entry<String, String>> it = properties.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, String> property = it.next();
			try {
				entity.addPart(property.getKey(), new StringBody(property.getValue()));
			} catch (UnsupportedEncodingException e) {
				LOG.warning("Failed to read metadata property value during transfer to SCM federator.");
				e.printStackTrace();
			}
			it.remove();
		}
		return entity;
	}

	private Properties getConfig() {
		return config;
	}

	/**
	 * Determines whether SCM project has been whitelisted for import via gateway
	 * @param properties Properties from imported archive (representing SCM project)
	 * @return
	 */
	private boolean isWhitelisted(Map<String, String> properties) {

		boolean isWhitelisted = false;

		WhitelistedProject inboundProject = new WhitelistedProject(properties.get("source_organisation"),
																properties.get("project"),
																properties.get("repo"));

		Set<WhitelistedProject> whitelistedProjects = GatewayManagementServiceFacade.getInstance().getWhitelistedProjects();

		for(WhitelistedProject whitelistedProject : whitelistedProjects) {
			if(inboundProject.equals(whitelistedProject)) {
				isWhitelisted = true;
				break;
			}
		}

		LOG.info("Is SCM project whitelisted? " + isWhitelisted);
		return isWhitelisted;
	}

}
