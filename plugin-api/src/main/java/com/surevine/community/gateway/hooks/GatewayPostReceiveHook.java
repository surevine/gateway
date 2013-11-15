package com.surevine.community.gateway.hooks;

import java.nio.file.Path;
import java.util.Map;

/**
 * Implementations of {@link GatewayPostReceiveHook} are called to examine a
 * file after it's been received from an external source.
 * 
 * This allows for calls to other systems which may include an AV server.
 * 
 * @author richard.midwinter@surevine.com
 */
public interface GatewayPostReceiveHook {

	void call(Path source, Map<String, String> properties);
}
