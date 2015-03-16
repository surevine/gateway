package com.surevine.community.gateway.hooks;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.surevine.community.gateway.audit.Audit;
import com.surevine.community.gateway.model.Partner;
import com.surevine.community.gateway.model.TransferItem;
import com.surevine.community.gateway.util.MockAuditActionFactory;
import com.surevine.community.gateway.util.MockAuditService;
import com.surevine.community.gateway.util.MockRuleFileService;

public class MetadataTest {

	@Test
	public void testFilter() throws URISyntaxException {
		final Map<String, String> properties = new HashMap<String, String>();

		properties.put("filename", "test");
		properties.put("organisation", "local");
		properties.put("name", "test.jar");

		final Set<TransferItem> transferQueue = new HashSet<TransferItem>(1);
		Partner destination = new Partner(1L, "Google", new URI("http://google.com"), "");
		transferQueue.add(new TransferItem(destination, Paths.get("/tmp"), properties));

		JavascriptPreExportHook jsPreExportHook = new JavascriptPreExportHook();
		MockRuleFileService mockRuleFileService = new MockRuleFileService(jsPreExportHook.getConfig());
		jsPreExportHook.setRuleFileService(mockRuleFileService);

		MockAuditService mockAuditService = new MockAuditService();
		MockAuditActionFactory mockAuditActionFactory = new MockAuditActionFactory();
		Audit.setAuditService(mockAuditService);
		Audit.setAuditActionFactory(mockAuditActionFactory);

		jsPreExportHook.call(transferQueue);

		assertEquals(2, properties.keySet().size());
	}
}
