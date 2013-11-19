package com.surevine.community.gateway;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.surevine.community.gateway.hooks.Hooks;

public class GatewayContextListener implements ServletContextListener {

	public void contextInitialized(final ServletContextEvent event) {
		Hooks.callInit(event);
	}

	public void contextDestroyed(final ServletContextEvent event) {
		Hooks.callDestroy(event);
	}
}
