package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;

public class JavascriptContextHook implements GatewayContextHook {

	private static final Logger LOG = Logger.getLogger(JavascriptContextHook.class.getName());

	@Override
	public void init(final ServletContextEvent event) {
		final Properties config = new Properties();
		InputStream stream = null;
		try {
			stream = getClass().getResourceAsStream("/javascript-hook.properties");
			config.load(stream);
		} catch (final IOException e) {
			LOG.log(Level.SEVERE, "Failed to load Javascript hook module configuration.", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (final IOException e) {
					LOG.log(Level.SEVERE, "Failed to load Javascript hook module configuration.", e);
				}
			}
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
