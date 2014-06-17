package com.surevine.community.gateway;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.UUID;

/**
 * Saves imported content to a temporary directory.
 * 
 * @author rich.midwinter@gmail.com
 */
public class Quarantine {
	
	public static Path save(final byte[] file,
			final Map<String, String> properties) throws IOException {
		final Path quarantine = Paths.get(GatewayProperties.get(
				GatewayProperties.EXPORT_QUARANTINE_DIR),
				UUID.randomUUID().toString());
		if (!Files.exists(quarantine)) {
			Files.createDirectories(quarantine);
		}
		
		final Path target = Paths.get(quarantine.toString(), properties.get("filename"));
		
		Files.write(target, file, StandardOpenOption.CREATE);
		
		return target;
	}
	
	public static void remove(final Path target) throws IOException {
		if (Files.exists(target)) {
			Files.delete(target);
		}
		
		if (Files.exists(target.getParent())) {
			for (File f : target.getParent().toFile().listFiles()) {
				f.delete();
			}
			Files.delete(target.getParent());
		}
	}
}
