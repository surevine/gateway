package com.surevine.community.gateway.hooks;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

public class JavaScriptExportFilterTest {

	@Test
	public void testFilter() throws URISyntaxException {
		final Map<String, String> properties = new HashMap<String, String>();

		properties.put("filename", "test");
		properties.put("organisation", "local");
		properties.put("classification", "COMMERCIAL");
		properties.put("decorator", "IN CONFIDENCE");
		properties.put("groups", "STAFF");
		properties.put("name", "test.jar");

		final List<Partner> destinations = new ArrayList<Partner>(Arrays.asList(new Partner[] {
				new Partner(1L, "FTP", new URI("ftp://host/path"), "key"),
				new Partner(2L, "SCP", new URI("scp://host/path"), "key"),
				new Partner(3L, "FILE_FOREIGN", new URI("file:///tmp/foreign"), "key"),
				new Partner(4L, "FILE_DOMESTIC", new URI("file:///tmp/domestic"), "key")
		}));

		final Set<TransferItem> transferQueue = new HashSet<TransferItem>(4);
		for (final Partner destination : destinations) {
			transferQueue.add(new TransferItem(destination, Paths.get("/tmp"), properties));
		}

		JavascriptPreExportHook jsPreExportHook = new JavascriptPreExportHook();
		MockRuleFileService mockRuleFileService = new MockRuleFileService(jsPreExportHook.getConfig());
		jsPreExportHook.setRuleFileService(mockRuleFileService);

		MockAuditService mockAuditService = new MockAuditService();
		MockAuditActionFactory mockAuditActionFactory = new MockAuditActionFactory();
		Audit.setAuditService(mockAuditService);
		Audit.setAuditActionFactory(mockAuditActionFactory);

		jsPreExportHook.call(transferQueue);

		int exportable = 0;
		for (final TransferItem item : transferQueue) {
			if (item.isExportable()) exportable ++;
		}

		assertEquals(1, exportable);
	}

}
