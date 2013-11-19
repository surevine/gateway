package com.surevine.community.gateway.hooks;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * Watches for new files within a directory. As and when they appear it then
 * extracts them, parses the metadata and calls available PostReceiveHooks for
 * them to distribute to LAN systems.
 * 
 * @author rich.midwinter@gmail.com
 */
public class FileImportContextHook implements GatewayContextHook {
	
	private static final Logger LOG = Logger.getLogger(FileImportContextHook.class.getName());
	
	private static Thread fileImporter;
	
	@Override
	public void init(final ServletContextEvent event) {
		fileImporter = new Thread() {
			{
				setDaemon(true);
				setName("FileImportContextHook");
			}
			
			public void run() {
				try {
					listen();
				} catch (final IOException | InterruptedException e) {
					LOG.log(Level.SEVERE, "Failure listening for file system imports: " +e.getMessage());
					LOG.log(Level.FINEST, "Failure listening for file system imports.", e);
				}
			}
		};
		
		fileImporter.start();
	}

	@Override
	public void destroy(final ServletContextEvent event) {
		fileImporter.interrupt();
	}
	
	private void listen() throws IOException, InterruptedException {
		final Path importDirectory = Paths.get("/tmp/import-quarantine");  //FIXME: Hard coded
		if (!Files.exists(importDirectory)) {
			Files.createDirectories(importDirectory);
		}
		
		final WatchService watcher = FileSystems.getDefault().newWatchService();
		importDirectory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		
		while (true) {
			final WatchKey key = watcher.take();
			for (final WatchEvent<?> event : key.pollEvents()) {
				final WatchEvent.Kind<?> kind = event.kind();
				
				if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind)) {
					final Path directory = (Path) key.watchable();
					final Path target = directory.resolve((Path) event.context());
					
					// Create a working directory
					final Path workingDirectory = Paths.get(
							"/tmp/import-working", // FIXME: Hard coded
							UUID.randomUUID().toString());
					Files.createDirectories(workingDirectory);
					
					// Extract.
					Runtime.getRuntime().exec(
							new String[] {"tar", "xzvf", target.toAbsolutePath().toString(),
									"-C", workingDirectory.toString()},
							new String[] {},
							target.toFile().getAbsoluteFile().getParentFile()).waitFor();
					
					// Read the metadata.json file.
				    final TypeReference<HashMap<String,String>> typeRef = new TypeReference<HashMap<String,String> 
				               >() {}; 
				    final HashMap<String,String> properties = new ObjectMapper(
				    		new JsonFactory()).readValue(
				    		Paths.get(workingDirectory.toString(),
							".metadata.json").toFile(), typeRef); 
					
					// FIXME: This should be in a Git / Gitlab post-receive hook
					// Add git remote if we're doing a push.
					/*Runtime.getRuntime().exec(
							new String[] {"git", "remote", target.getFileName().toString()},
							new String[] {},
							target.getParent().toFile()).waitFor();*/
					
					// Run import hooks with metadata and file
					Hooks.callPostReceive(target, properties);
					
					// Remove.
					Files.delete(target);
					Files.walkFileTree(workingDirectory, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(final Path file,
								final BasicFileAttributes attrs) throws IOException {
							Files.delete(file);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFileFailed(final Path file,
								final IOException exc) throws IOException {
							Files.delete(file);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(final Path dir,
								final IOException exc) throws IOException {
							if (exc == null) {
								Files.delete(dir);
								return FileVisitResult.CONTINUE;
							} else {
								throw exc;
							}
						}
					});
				}
			}
			
			key.reset();
		}
	}
}
