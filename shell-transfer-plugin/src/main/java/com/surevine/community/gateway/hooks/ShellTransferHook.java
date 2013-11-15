package com.surevine.community.gateway.hooks;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Passes imported file details to shell code.
 * 
 * Could be used for custom scp, ftp, etc. transfers.
 * 
 * @author rich.midwinter@gmail.com
 */
public class ShellTransferHook implements GatewayTransferHook {
	
	private static final Logger LOG = Logger.getLogger(ShellTransferHook.class.getName());

	public void call(final Path source, final Map<String, String> properties,
			final URI... destinations) {
		for (final URI uri : destinations) {
			LOG.info(String.format("Calling out to shell code for URI %s with source %s", uri, source));
			// Delegate to shell script
		}
	}
}
