package com.surevine.community.gateway;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class JavaScriptExportFilterTest {

	@Test
	public void testFilter() throws URISyntaxException {
		final Map<String, String> properties = new HashMap<String, String>();
		
		properties.put("filenamae", "test");
		properties.put("organisation", "local");
		
		final URI[] destinations = new URI[] {
				new URI("ftp://host/path"),
				new URI("scp://host/path")
		};
		
		final URI[] targets = JavaScriptExportFilter.filter(null, properties, destinations);
		
		assertEquals(1, targets.length);
	}
}
