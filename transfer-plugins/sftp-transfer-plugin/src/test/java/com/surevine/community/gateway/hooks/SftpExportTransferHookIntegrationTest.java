package com.surevine.community.gateway.hooks;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.surevine.community.gateway.model.TransferItem;

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
		final Set<TransferItem> transferQueue = new HashSet<TransferItem>(1);
		transferQueue.add(new TransferItem(
				new URI("sftp://ec2-user@10.66.2.169/tmp"),
				Paths.get("/System/Library/CoreServices/SystemVersion.plist"),
				new HashMap<String, String>(0)));
		
		hook.call(transferQueue);
	}
}
