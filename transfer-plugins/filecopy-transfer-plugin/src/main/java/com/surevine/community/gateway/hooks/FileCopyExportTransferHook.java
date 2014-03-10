package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.surevine.community.gateway.GatewayProperties;

/**
 * Copies an imported file to a file:// destination. 
 * 
 * @author rich.midwinter@gmail.com
 */
public class FileCopyExportTransferHook implements GatewayExportTransferHook {
	
	private static final Logger LOG = Logger.getLogger(FileCopyExportTransferHook.class.getName());

	public void call(final Path source, final Map<String, String> properties,
			final URI... destinations) {
		for (final URI uri : destinations) {
			if ("file".equals(uri.getScheme())) {
				try {
					LOG.info(String.format("Copying from %s to %s",
							source.toString(),
							Paths.get(Paths.get(uri).toString(),
									source.getFileName().toString())));
					
					// We copy to a temporary location then move. This ensures
					// (i) any watching import doesn't start while a copy has
					// not completed for a large file and (ii) the move doesn't
					// prevent subsequent transfer plugins finding the source.
					final Path temporaryFile = Paths.get(Paths.get(uri).toString(),
							source.getFileName().toString()
							+GatewayProperties.get(GatewayProperties.TRANSFER_EXTENSION));
					
					// Create parent directories
					Files.createDirectories(Paths.get(uri));
					
					// Copy source to temporary
					Files.copy(source, temporaryFile);
					
					// Move from temporary to export
					if (properties.containsKey("destinationFilename")) {
						LOG.info("Using destinationFilename key: " +properties.get("destinationFilename"));
						copy(temporaryFile, uri, properties.get("destinationFilename").split(","));
					} else {
						LOG.info("No destinationFilename key. Preserving existing filename.");
						copy(temporaryFile, uri, new String[] { source.getFileName().toString() });
					}
					
					Files.delete(temporaryFile);
					
					LOG.info("Copy complete.");
				} catch (final IOException e) {
					LOG.log(Level.SEVERE, e.getMessage(), e);
				}
			} else {
				LOG.warning(String.format("Unable to perform file copy for the %s scheme.", uri.getScheme()));
			}
		}
	}
	
	private void copy(final Path temporaryFile, final URI destinationFolder, final String[] destinationFilenames) throws IOException {
		LOG.info("Copy " +temporaryFile +" to " +destinationFolder);
		
		// Ensure the temporary and destination directories exist
		Files.createDirectories(Paths.get(destinationFolder));
		
		for (final String destinationFilename : destinationFilenames) {
			LOG.info(String.format("Split destination copying %s from %s to %s",
					destinationFilename,
					temporaryFile.toString(),
					Paths.get(Paths.get(destinationFolder).toString(),
							destinationFilename)));

			// Create parent directories
			Files.createDirectories(Paths.get(destinationFolder));
			
			Files.copy(temporaryFile,
					Paths.get(Paths.get(destinationFolder).toString(),
							destinationFilename),
							StandardCopyOption.REPLACE_EXISTING);
		}
	}
}
