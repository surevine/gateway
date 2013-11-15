package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copies an imported file to a file:// destination. 
 * 
 * @author rich.midwinter@gmail.com
 */
public class FileCopyTransferHook implements GatewayTransferHook {
	
	private static final Logger LOG = Logger.getLogger(FileCopyTransferHook.class.getName());

	public void call(final Path source, final Map<String, String> properties,
			final URI... destinations) {
		for (final URI uri : destinations) {
			if ("file".equals(uri.getScheme())) {
				try {
					LOG.info(String.format("Copying from %s to %s",
							source.toString(),
							Paths.get(Paths.get(uri).toString(),
									source.getFileName().toString())));
					
					Files.copy(source, Paths.get(Paths.get(uri).toString(),
							source.getFileName().toString()));
				} catch (final IOException e) {
					LOG.log(Level.SEVERE, e.getMessage(), e);
				}
			} else {
				LOG.warning(String.format("Unable to perform file copy for the %s scheme.", uri.getScheme()));
			}
		}
	}
}
