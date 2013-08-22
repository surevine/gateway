package com.surevine.community.gateway.hooks;

/**
 * Implementations of {@link GatewayInitHook} are called at the
 * contextInitialised of the {@GatewayContextListener}.
 * 
 * This allows for caches to be populated.
 * 
 * @author richard.midwinter@surevine.com
 */
public interface GatewayInitHook {

	void call();
}
