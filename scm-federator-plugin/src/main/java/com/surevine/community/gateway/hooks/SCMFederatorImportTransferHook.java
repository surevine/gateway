package com.surevine.community.gateway.hooks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

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
			getConfig().load(getClass().getResourceAsStream("/federator.properties"));
		} catch (IOException e) {
			LOG.warning("Failed to load scm federator transfer hook configuration.");
			e.printStackTrace();
		}
	}

	@Override
	public void call(File[] received, Map<String, String> properties) {

		for (int i = 0; i < received.length; i++) {

			File importedFile = received[i];

			Path importedFilePath = Paths.get(importedFile.getAbsolutePath());
			Path scmImportPath = Paths.get(getConfig().getProperty("scm.federator.import.dir") + importedFile.getName());

			try {
				Files.copy(importedFilePath, scmImportPath);
			} catch (IOException e) {
				LOG.severe("Failed to transfer imported bundle to SCM federator.");
				e.printStackTrace();
			}

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
			return supported;
		}
		catch (Exception e) {
			LOG.info("Exception during support method: "+e);
			return false;
		}
	}

	private Properties getConfig() {
		return config;
	}

}
