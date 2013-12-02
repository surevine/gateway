package com.surevine.community.gateway.hooks;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MetadataTest {

	@Test
	public void testFilter() throws URISyntaxException {
		final Map<String, String> properties = new HashMap<String, String>();
		
		properties.put("filename", "test");
		properties.put("organisation", "local");
		
		new JavascriptPreExportHook().call(null, properties, Arrays.asList(new URI[]{ new URI("http://google.com") }));
		
		assertEquals(1, properties.keySet().size());
	}
}
