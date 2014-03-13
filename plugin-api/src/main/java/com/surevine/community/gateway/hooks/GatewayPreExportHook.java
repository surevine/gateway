package com.surevine.community.gateway.hooks;

import java.util.Set;

import com.surevine.community.gateway.model.TransferItem;


/**
 * Implementations of {@link GatewayPreExportHook} are called to examine a file
 * before it's accepted into the transfer queue.
 * 
 * This allows for calls to other systems which may include an AV server.
 * 
 * @author richard.midwinter@surevine.com
 */
public interface GatewayPreExportHook {

	void call(Set<TransferItem> transferQueue);
}
