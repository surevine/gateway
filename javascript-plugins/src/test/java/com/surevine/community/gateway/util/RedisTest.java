package com.surevine.community.gateway.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class RedisTest {

	@Test
	public void testMainSuccess() {
		final Properties config = new Properties();
		InputStream stream = null;
		try {
			stream = getClass().getResourceAsStream("/javascript-hook.properties");
			config.load(stream);
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}

		final long pos = new Redis("10.66.2.254").getIncr("test");

		assertEquals(pos + 1, new Redis("10.66.2.254").getIncr("test"));
	}
}
