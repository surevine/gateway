package com.surevine.community.gateway.hooks;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

/**
 * Implementations of {@link GatewayExportTransferHook} are called to send received
 * files out to other {link URI} based destinations with whitelisted metadata.
 * 
 * This allows for various transport mechanisms to send files to other gateways
 * but also to send files to archive, continuous integration servers, etc.
 * 
 * @author richard.midwinter@surevine.com
 */
public interface GatewayExportTransferHook {

	void call(Path source, Map<String, String> properties, URI... destinations);
}
