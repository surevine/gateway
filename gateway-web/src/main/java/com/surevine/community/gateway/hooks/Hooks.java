package com.surevine.community.gateway.hooks;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;

public class Hooks {
	
	private static final Logger LOG = Logger.getLogger(Hooks.class.getName());

	/**
	 * Call all hooks which review files on receipt, bound for export.
	 * 
	 * @param source Path to file in quarantine.
	 * @param properties Metadata associated with the file.
	 * @throws GatewayTransferException If submission should be rejected.
	 */
	public static void callPreExport(final Path source,
			final Map<String, String> properties, final List<URI> destinations) {
        final ServiceLoader<GatewayPreExportHook> hooks = ServiceLoader.load(GatewayPreExportHook.class);
        
        for (final GatewayPreExportHook hook : hooks) {
        	LOG.info(String.format("STARTING [%s]", hook.getClass().getName()));
        	
            hook.call(source, properties, destinations);
            
        	LOG.info(String.format("COMPLETE [%s]", hook.getClass().getName()));
        }
	}

	/**
	 * Call all hooks which review files on receipt, bound for local import.
	 * 
	 * @param source Path to file in quarantine.
	 * @param properties Metadata associated with the file.
	 * @throws GatewayTransferException If submission should be rejected.
	 */
	public static void callPreImport(final Path source,
			final Map<String, String> properties) throws GatewayTransferException {
        final ServiceLoader<GatewayPreImportHook> hooks = ServiceLoader.load(GatewayPreImportHook.class);
        
        for (final GatewayPreImportHook hook : hooks) {
        	LOG.info(String.format("STARTING [%s]", hook.getClass().getName()));
        	
            hook.call(source, properties);
            
        	LOG.info(String.format("COMPLETE [%s]", hook.getClass().getName()));
        }
	}

	/**
	 * Call all hooks which transfer imported files to local systems.
	 * 
	 * @param source Path to file.
	 * @param properties Metadata associated with the file.
	 */
	public static void callImportTransfer(final File[] received,
			final Map<String, String> properties) {
        final ServiceLoader<GatewayImportTransferHook> hooks = ServiceLoader.load(GatewayImportTransferHook.class);
        
        for (final GatewayImportTransferHook hook : hooks) {
        	LOG.info(String.format("STARTING [%s]", hook.getClass().getName()));
        	
            hook.call(received, properties);
            
        	LOG.info(String.format("COMPLETE [%s]", hook.getClass().getName()));
        }
	}
	
	/**
	 * Call all hooks which transfer files.
	 * 
	 * @param source The file to transfer.
	 * @param properties The whitelisted properties to transfer with the file.
	 * @param destinations An array of URIs to transfer to.
	 */
	public static void callExportTransfer(final Path source,
			final Map<String, String> properties, final URI... destinations) {
        final ServiceLoader<GatewayExportTransferHook> hooks = ServiceLoader.load(GatewayExportTransferHook.class);
        
        for (final GatewayExportTransferHook hook : hooks) {
        	LOG.info(String.format("STARTING [%s]", hook.getClass().getName()));
        	
            hook.call(source, properties, destinations);
            
        	LOG.info(String.format("COMPLETE [%s]", hook.getClass().getName()));
        }
	}
	
	/**
	 * Call all hooks which initialise services.
	 */
	public static void callInit(final ServletContextEvent event) {
        final ServiceLoader<GatewayContextHook> hooks = ServiceLoader.load(GatewayContextHook.class);
        
        for (final GatewayContextHook hook : hooks) {
        	LOG.info(String.format("STARTING [%s]", hook.getClass().getName()));
        	
            hook.init(event);
            
        	LOG.info(String.format("COMPLETE [%s]", hook.getClass().getName()));
        }
	}
	
	/**
	 * Call all hooks which destroy services.
	 */
	public static void callDestroy(final ServletContextEvent event) {
        final ServiceLoader<GatewayContextHook> hooks = ServiceLoader.load(GatewayContextHook.class);
        
        for (final GatewayContextHook hook : hooks) {
        	LOG.info(String.format("STARTING [%s]", hook.getClass().getName()));
        	
            hook.destroy(event);
            
        	LOG.info(String.format("COMPLETE [%s]", hook.getClass().getName()));
        }
	}
}
