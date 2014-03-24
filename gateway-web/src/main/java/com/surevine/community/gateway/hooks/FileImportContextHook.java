package com.surevine.community.gateway.hooks;

import com.surevine.community.gateway.GatewayProperties;
import com.surevine.community.gateway.history.History;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import javax.enterprise.context.RequestScoped;
import javax.servlet.ServletContextEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Watches for new files within a directory. As and when they appear it then
 * extracts them, parses the metadata and calls available PostReceiveHooks for
 * them to distribute to LAN systems.
 * 
 * @author rich.midwinter@gmail.com
 */
@RequestScoped
public class FileImportContextHook implements GatewayContextHook {
	
	private static final Logger LOG = Logger.getLogger(FileImportContextHook.class.getName());
	
//	@Inject
//	private History history;
	
	private static Thread fileImporter;
	
	@Override
	public void init(final ServletContextEvent event) {

        try
        {
            processFilesOnStartup();
        } catch (final IOException | InterruptedException e)
        {
            LOG.log(Level.SEVERE, "Failure importing files at startup: " +e.getMessage());
            LOG.log(Level.FINEST, "Failure importing files at startup.", e);
        }

        LOG.info("Listening for filesystem imports in " + GatewayProperties.get(GatewayProperties.IMPORT_WATCH_DIR));
		LOG.info("List of export destinations: " +GatewayProperties.get(GatewayProperties.EXPORT_DESTINATIONS));
		
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



    /**
     * Checks that the supplied path exists and is readable
     * @param dir the Path representation of the dir to check
     */
    private boolean checkPathExistsAndIsReadable(Path dir) {

        boolean result = Files.isDirectory(dir);
       result &= Files.isReadable(dir);

       return result;
    }



    @Override
	public void destroy(final ServletContextEvent event) {
		fileImporter.interrupt();
	}

    /**
     * Listens to FileWatch Events and process the files for transfer.
     *
     * @throws IOException
     * @throws InterruptedException
     */
	private void listen() throws IOException, InterruptedException {
		final Path importDirectory = Paths.get(GatewayProperties.get(GatewayProperties.IMPORT_WATCH_DIR));
		Files.createDirectories(importDirectory);
		
		final WatchService watcher = FileSystems.getDefault().newWatchService();
		importDirectory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
		
		while (true) {
			LOG.info("Awaiting filesystem events.");
			final WatchKey key = watcher.take();
			
			for (final WatchEvent<?> event : key.pollEvents()) {
				final WatchEvent.Kind<?> kind = event.kind();

             	if (StandardWatchEventKinds.OVERFLOW.equals(kind)) {
					LOG.warning("Overflow watching import directory.");
				} else if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind) || StandardWatchEventKinds.ENTRY_MODIFY.equals(kind)) {
					final Path directory = (Path) key.watchable();
					final Path target = directory.resolve((Path) event.context());

					LOG.info(String.format("New file detected via event %s and with name %s: ",kind.name(),target.getFileName()));
                    final Path workingDirectory = importFileAtPath(target);

                    if (workingDirectory == null) return;
                    cleanupImport(target, workingDirectory);

                    History.getInstance().add(String.format("Finished importing %s.", target.getFileName()));
				}
			}
			
			LOG.info("Resetting watch key.");
			
			key.reset();
		}
	}

    /**
     *  Iterates over any files in the import directory and processes those prior to setting up the FileSystem WatchService
     *
     *  This handles the case where files have been delivered into the directory, when the FileSystem WatchService hasn't been running
     */
    private void processFilesOnStartup() throws IOException, InterruptedException {

        final Path importWatchDirectory = Paths.get(GatewayProperties.get(GatewayProperties.IMPORT_WATCH_DIR));

        if (!checkPathExistsAndIsReadable(importWatchDirectory))
        {
            Files.createDirectories(Paths.get(GatewayProperties.get(GatewayProperties.IMPORT_WATCH_DIR)));
        }

        LOG.info(String.format("Checking for existing filesystem imports in  %s: ", importWatchDirectory));

        DirectoryStream<Path> stream = Files.newDirectoryStream(importWatchDirectory);

        for (Path target: stream)
        {
            // loop over the files in the import watch dir, and process them
            LOG.info(String.format("Processing file with name %s on service init.",target.getFileName()));

            final Path workingDirectory = importFileAtPath(target);

            if (workingDirectory!=null)
            {
                cleanupImport(target, workingDirectory);
                History.getInstance().add(String.format("Finished importing %s on service init.", target.getFileName()));
            }
        }

    }

    /**
     * Cleans up an imported path identified by the target, and the temporary working directory files associated with the import
     * @param target The file path of the imported file
     * @param workingDirectory The working directory where the imported file is processed
     * @throws IOException
     */
    private void cleanupImport(Path target, Path workingDirectory) throws IOException {


        LOG.info(String.format("Cleaning up processing of filename %s: ",target.getFileName()));

        // Remove.
        if (GatewayProperties.doCleanUp()) {
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

    /**
     * Imports a file at the specified path
     * @param target  The file patch to process and import
     * @return the working directory
     * @throws IOException
     * @throws InterruptedException
     */
    private Path importFileAtPath(Path target) throws IOException, InterruptedException {

        // Skip any incomplete transfers.
        if (target.getFileName().toString().endsWith(GatewayProperties.get(GatewayProperties.TRANSFER_EXTENSION))) {
            return null;
        }

        History.getInstance().add(String.format("Received file %s for import.", target.getFileName()));

        // Create a working directory
        LOG.info("Creating working directory");
        final Path workingDirectory = Paths.get(
                GatewayProperties.get(GatewayProperties.IMPORT_WORKING_DIR),
                UUID.randomUUID().toString());
        Files.createDirectories(workingDirectory);

        // Extract.
        LOG.info("Extracting received file.");
        Runtime.getRuntime().exec(
                new String[] {"tar", "xzvf", target.toAbsolutePath().toString(),
                        "-C", workingDirectory.toString()},
                new String[] {},
                target.toFile().getAbsoluteFile().getParentFile()).waitFor();

        // Read the metadata.json file.
        LOG.info("Reading metadata file.");
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
        final File[] received = workingDirectory.toFile().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return !name.equals(".metadata.json");
            }});

        for (final File file : received) {
            Hooks.callPreImport(file.toPath(), properties);
        }

        Hooks.callImportTransfer(received, properties);
        return workingDirectory;
    }
}
