package com.surevine.community.gateway.hooks;

import org.junit.Test;

public class GitlabInitHookIntegrationTest {

	@Test
	public void testInit() {
		new GitlabContextHook().init();
	}
}
