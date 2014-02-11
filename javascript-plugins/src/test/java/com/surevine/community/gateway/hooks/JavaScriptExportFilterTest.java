package com.surevine.community.gateway.hooks;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

//@Ignore
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
		
		final List<URI> destinations = new ArrayList<URI>(Arrays.asList(new URI[] {
				new URI("ftp://host/path"),
				new URI("scp://host/path"),
				new URI("file:///tmp/foreign"),
				new URI("file:///tmp/domestic")
		}));
		
		new JavascriptPreExportHook().call(Paths.get("/tmp"), properties, destinations);
		
		assertEquals(2, destinations.size());
	}
}
