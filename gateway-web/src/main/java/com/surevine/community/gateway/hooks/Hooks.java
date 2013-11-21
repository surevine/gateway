package com.surevine.community.gateway.hooks;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.servlet.ServletContextEvent;

public class Hooks {

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
            hook.call(source, properties, destinations);
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
            hook.call(source, properties);
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
            hook.call(received, properties);
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
            hook.call(source, properties, destinations);
        }
	}
	
	/**
	 * Call all hooks which initialise services.
	 */
	public static void callInit(final ServletContextEvent event) {
        final ServiceLoader<GatewayContextHook> hooks = ServiceLoader.load(GatewayContextHook.class);
        
        for (final GatewayContextHook hook : hooks) {
            hook.init(event);
        }
	}
	
	/**
	 * Call all hooks which destroy services.
	 */
	public static void callDestroy(final ServletContextEvent event) {
        final ServiceLoader<GatewayContextHook> hooks = ServiceLoader.load(GatewayContextHook.class);
        
        for (final GatewayContextHook hook : hooks) {
            hook.destroy(event);
        }
	}
}
