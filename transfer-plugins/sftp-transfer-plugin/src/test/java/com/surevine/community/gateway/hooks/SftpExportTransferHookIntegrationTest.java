package com.surevine.community.gateway.hooks;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

// Ignored by default as it's an integration test, don't want to break or slow
// down anyone else's build.
@Ignore
public class SftpExportTransferHookIntegrationTest {

	private SftpExportTransferHook hook;
	
	@Before
	public void doSetup() {
		hook = new SftpExportTransferHook();
	}
	
	@Test
	public void testSuccess() throws URISyntaxException {
		hook.call(Paths.get("/System/Library/CoreServices/SystemVersion.plist"),
				new HashMap<String, String>(),
				new URI("sftp://ec2-user@10.66.2.169/tmp"));
	}
}
