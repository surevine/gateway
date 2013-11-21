package com.surevine.community.gateway.hooks;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Implementations of {@link GatewayPreExportHook} are called to examine a file
 * before it's accepted into the transfer queue.
 * 
 * This allows for calls to other systems which may include an AV server.
 * 
 * @author richard.midwinter@surevine.com
 */
public interface GatewayPreExportHook {

	void call(Path source, Map<String, String> properties, List<URI> destinations);
}
