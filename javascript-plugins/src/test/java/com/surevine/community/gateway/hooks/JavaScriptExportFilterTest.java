package com.surevine.community.gateway.hooks;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class JavaScriptExportFilterTest {

	@Test
	public void testFilter() throws URISyntaxException {
		final Map<String, String> properties = new HashMap<String, String>();
		
		properties.put("filename", "test");
		properties.put("organisation", "local");
		
		final List<URI> destinations = new ArrayList<URI>(Arrays.asList(new URI[] {
				new URI("ftp://host/path"),
				new URI("scp://host/path")
		}));
		
		new JavascriptPreExportHook().call(null, properties, destinations);
		
		assertEquals(1, destinations.size());
	}
}
