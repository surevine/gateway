package com.surevine.community.gateway.hooks;

import java.util.Set;

import com.surevine.community.gateway.model.TransferItem;

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

	void call(Set<TransferItem> transferQueue);
}
