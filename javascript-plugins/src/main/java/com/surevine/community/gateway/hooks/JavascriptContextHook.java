package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;

public class JavascriptContextHook implements GatewayContextHook {

	private static final Logger LOG = Logger.getLogger(JavascriptContextHook.class.getName());

	@Override
	public void init(final ServletContextEvent event) {
		final Properties config = new Properties();
		try {
			config.load(getClass().getResourceAsStream("/javascript-hook.properties"));
		} catch (final IOException e) {
			// FIXME: Handle better
			LOG.log(Level.SEVERE, "Failed to load Javascript hook module configuration.", e);
		}

		LOG.info("Using the following JS engine properties:");
		for (final String key : config.stringPropertyNames()) {
			LOG.info(String.format("  %s : %s", key, config.getProperty(key)));
		}
	}

	@Override
	public void destroy(final ServletContextEvent event) {
	}
}
