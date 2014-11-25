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

import com.surevine.community.gateway.model.Destination;
import com.surevine.community.gateway.model.TransferItem;
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

		final List<Destination> destinations = new ArrayList<Destination>(Arrays.asList(new Destination[] {
				new Destination(1L, "FTP", new URI("ftp://host/path")),
				new Destination(2L, "SCP", new URI("scp://host/path")),
				new Destination(3L, "FILE_FOREIGN", new URI("file:///tmp/foreign")),
				new Destination(4L, "FILE_DOMESTIC", new URI("file:///tmp/domestic"))
		}));

		final Set<TransferItem> transferQueue = new HashSet<TransferItem>(4);
		for (final Destination destination : destinations) {
			transferQueue.add(new TransferItem(destination, Paths.get("/tmp"), properties));
		}

		JavascriptPreExportHook jsPreExportHook = new JavascriptPreExportHook();
		MockRuleFileService mockRuleFileService = new MockRuleFileService(jsPreExportHook.getConfig());
		jsPreExportHook.setRuleFileService(mockRuleFileService);

		MockAuditService mockAuditService = new MockAuditService();
		jsPreExportHook.setAuditService(mockAuditService);

		jsPreExportHook.call(transferQueue);

		int exportable = 0;
		for (final TransferItem item : transferQueue) {
			if (item.isExportable()) exportable ++;
		}

		assertEquals(1, exportable);
	}

}
