package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileImportContextHook implements GatewayContextHook {
	
	private static final Logger LOG = Logger.getLogger(FileImportContextHook.class.getName());
	
	private static Thread fileImporter;

	@Override
	public void init() {
		fileImporter = new Thread() {
			{
				setDaemon(true);
				setName("FileImportContextHook");
			}
			
			public void run() {
				try {
					listen();
				} catch (final IOException | InterruptedException e) {
					LOG.log(Level.SEVERE, "Failure listening for file system imports.", e);
				}
			}
		};
		
		fileImporter.start();
	}
	
	private void listen() throws IOException, InterruptedException {
		LOG.info("Starting file import watcher.");
		
		final Path importDirectory = Paths.get("/tmp/import-quarantine");
		if (!Files.exists(importDirectory)) {
			Files.createDirectories(importDirectory);
		}
		
		final WatchService watcher = FileSystems.getDefault().newWatchService();
		importDirectory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		
		while (true) {
			final WatchKey key = watcher.take();
			for (final WatchEvent<?> event : key.pollEvents()) {
				switch (event.kind().name()) {
				case "ENTRY_CREATE":
					final Path target = (Path) event.context();
					final Path workingDirectory = Paths.get(
							target.getParent().toString(),
							UUID.randomUUID().toString());
					
					LOG.info(String.format("Extracting archive %s", target.getFileName()));
					
					Files.createDirectories(workingDirectory);
					
					// Extract.
					Runtime.getRuntime().exec(
							new String[] {"tar", "xzvf", target.toString(),
									"-C", workingDirectory.toString()},
							new String[] {},
							target.getParent().toFile()).waitFor();
					
					// Read the metadata.json file.
					final List<String> lines = Files.readAllLines(Paths.get(workingDirectory.toString(), ".metadata.json"), Charset.forName("UTF-8"));
					LOG.info("Metadata contents:");
					for (final String line : lines) {
						LOG.info(line);
					}
					
					// Add git remote if we're doing a push.
					/*Runtime.getRuntime().exec(
							new String[] {"git", "remote", target.getFileName().toString()},
							new String[] {},
							target.getParent().toFile()).waitFor();*/
					
					// Run import hooks with metadata and file
					
					
					// Remove.
					
					break;
				}
			}
			
			key.reset();
		}
	}

	@Override
	public void destroy() {
	}
}
