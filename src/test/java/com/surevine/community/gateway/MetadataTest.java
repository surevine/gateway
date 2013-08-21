package com.surevine.community.gateway;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MetadataTest {

	@Test
	public void testFilter() throws URISyntaxException {
		final Map<String, String> properties = new HashMap<String, String>();
		
		properties.put("filenamae", "test");
		properties.put("organisation", "local");
		
		final Map<String, String> filteredProperties = Metadata.sanitise(properties);
		
		assertEquals(1, filteredProperties.keySet().size());
	}
}
