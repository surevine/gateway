package com.surevine.community.gateway.hooks;

import java.nio.file.Path;
import java.util.Map;

/**
 * Implementations of {@link GatewayPreImportHook} are called to examine a file
 * before it's imported to a local system.
 * 
 * This allows for rewriting metadata, calls to other systems which may include
 * an AV server, etc.
 * 
 * @author richard.midwinter@surevine.com
 */
public interface GatewayPreImportHook {

	void call(Path source, Map<String, String> properties)
			throws GatewayTransferException;
}
