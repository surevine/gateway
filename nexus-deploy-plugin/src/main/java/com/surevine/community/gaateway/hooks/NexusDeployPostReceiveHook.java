package com.surevine.community.gaateway.hooks;

import java.nio.file.Path;
import java.util.Map;

import com.surevine.community.gateway.hooks.GatewayPostReceiveHook;

public class NexusDeployPostReceiveHook implements GatewayPostReceiveHook {

	@Override
	public void call(final Path source, final Map<String, String> properties) {
		System.out.println(String.format("Sending %s into a Nexus(es) configured in my properties file.", source));
	}
}
