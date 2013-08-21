package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileCopyTransferHook implements GatewayTransferHook {
	
	private static final Logger LOG = Logger.getLogger(FileCopyTransferHook.class.getName());

	public void call(final Path source, final Map<String, String> properties,
			final URI... destinations) {
		for (final URI uri : destinations) {
			if ("file".equals(uri.getScheme())) {
				try {
					Files.copy(source, Paths.get(uri));
				} catch (final IOException e) {
					LOG.warning(String.format("Failed to transfer %s to %s because an error occured:",
							source.getFileName().toString(), uri.toString(), e.getMessage()));
					LOG.log(Level.FINE, e.getMessage(), e);
				}
			}
		}
	}
}
