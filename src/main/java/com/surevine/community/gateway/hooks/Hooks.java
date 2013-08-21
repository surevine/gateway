package com.surevine.community.gateway.hooks;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.ServiceLoader;

public class Hooks {

	/**
	 * Call all hooks which review files immediately.
	 * 
	 * @param source Path to file in quarantine.
	 * @param properties Metadata associated with the file.
	 * @throws GatewayTransferException If submission should be rejected.
	 */
	public static void callPreReceive(final Path source,
			final Map<String, String> properties) throws GatewayTransferException {
        final ServiceLoader<GatewayPreReceiveHook> hooks = ServiceLoader.load(GatewayPreReceiveHook.class);
        
        for (final GatewayPreReceiveHook hook : hooks) {
            hook.call(source, properties);
        }
	}
	
	/**
	 * Call all hooks which transfer files.
	 * 
	 * @param source The file to transfer.
	 * @param properties The whitelisted properties to transfer with the file.
	 * @param destinations An array of URIs to transfer to.
	 */
	public static void callTransfer(final Path source,
			final Map<String, String> properties, final URI... destinations) {
        final ServiceLoader<GatewayTransferHook> hooks = ServiceLoader.load(GatewayTransferHook.class);
        
        for (final GatewayTransferHook hook : hooks) {
            hook.call(source, properties, destinations);
        }
	}
}
