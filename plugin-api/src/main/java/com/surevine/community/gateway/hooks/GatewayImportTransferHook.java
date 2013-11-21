package com.surevine.community.gateway.hooks;

import java.io.File;
import java.util.Map;

/**
 * Implementations of {@link GatewayImportTransferHook} are called to examine a
 * file after it's been received from an external source.
 * 
 * This allows for calls to other systems which may include an AV server.
 * 
 * @author richard.midwinter@surevine.com
 */
public interface GatewayImportTransferHook {

	void call(File[] received, Map<String, String> properties);
}
