package com.surevine.community.gateway.hooks;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class MetadataTest {

	@Test
	public void testFilter() throws URISyntaxException {
		final Map<String, String> properties = new HashMap<String, String>();
		
		properties.put("filename", "test");
		properties.put("organisation", "local");
		properties.put("name", "test.jar");
		
		new JavascriptPreExportHook().call(Paths.get("/tmp"), properties, new ArrayList<URI>(Arrays.asList(new URI[]{ new URI("http://google.com") })));
		
		assertEquals(1, properties.keySet().size());
	}
}
