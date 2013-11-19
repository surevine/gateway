package com.surevine.community.gateway.hooks;

import javax.servlet.ServletContextEvent;

/**
 * Implementations of {@link GatewayContextHook} are called at the
 * contextInitialised of the {@GatewayContextListener}.
 * 
 * This allows for caches to be populated.
 * 
 * @author richard.midwinter@surevine.com
 */
public interface GatewayContextHook {

	void init(ServletContextEvent event);
	
	void destroy(ServletContextEvent event);
}
