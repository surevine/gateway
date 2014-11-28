package com.surevine.community.gateway.hooks;

import java.io.File;
import java.util.Map;

public class SCMFederatorImportTransferHook implements GatewayImportTransferHook {

	@Override
	public void call(File[] received, Map<String, String> properties) {
		// TODO Move item to configurable federator watch dir
	}

	@Override
	public boolean supports(Map<String, String> properties) {
		// TODO check source for SCM string
		return false;
	}

}
