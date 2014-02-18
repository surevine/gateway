package com.surevine.community.gateway.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

public class RedisTest {

	@Test
	public void testMainSuccess() {
	    final Properties config = new Properties();
	    try {
			config.load(getClass().getResourceAsStream("/javascript-hook.properties"));
		} catch (final IOException e1) {
			e1.printStackTrace(); // FIXME: Handle
		}
	    
	    final long pos = new Redis("10.66.2.254").getIncr("test");
	    
		assertEquals(pos +1, new Redis("10.66.2.254").getIncr("test"));
	}
}
