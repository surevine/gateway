package com.surevine.community.gateway;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

public class Quarantine {
	
	public static Path save(final byte[] file, final Map<String, String> properties) throws IOException {
		// FIXME: Hard coded.
		final Path quarantine = Paths.get("/tmp/export-quarantine", UUID.randomUUID().toString());
		if (!Files.exists(quarantine)) {
			Files.createDirectories(quarantine);
		}
		
		final Path target = Paths.get(quarantine.toString(),
				properties.get("filename"));
		
		Files.write(target, file);
		
		return target;
	}
	
	public static void remove(final Path target) throws IOException {
		Files.delete(target);
		Files.delete(target.getParent());
	}
}
