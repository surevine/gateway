package com.surevine.community.gateway.properties;
import org.junit.Assert;
import org.junit.Test;

import com.surevine.community.gateway.properties.CMISProperties;


public class TestCMISProperties {

	
	@Test
	public void testHostname() {
		Assert.assertEquals("10.66.2.140", CMISProperties.get("test", CMISProperties.HOST));
		Assert.assertEquals("10.66.2.140", CMISProperties.get("test2", CMISProperties.HOST));
	}
	
	@Test
	public void testUsername() {
		Assert.assertEquals("admin", CMISProperties.get("test", CMISProperties.USERNAME));
		Assert.assertEquals("admin2", CMISProperties.get("test2", CMISProperties.USERNAME));
	}
	
	@Test
	public void testPassword() {
		Assert.assertEquals("Password12345", CMISProperties.get("test", CMISProperties.PASSWORD));
		Assert.assertEquals("Password123452", CMISProperties.get("test2", CMISProperties.PASSWORD));
	}
	
	@Test
	public void testSite() {
		Assert.assertEquals("test", CMISProperties.get("test", CMISProperties.SITE));
		Assert.assertEquals("test2", CMISProperties.get("test2", CMISProperties.SITE));
	}
	
	@Test
	public void testFolder() {
		Assert.assertEquals("testFolder", CMISProperties.get("test", CMISProperties.FOLDER));
		Assert.assertEquals("testFolder2", CMISProperties.get("test2", CMISProperties.FOLDER));
	}
	
	@Test (expected = java.util.MissingResourceException.class)
	public void testDestinationDoesNotExist() {
		Assert.assertEquals(null, CMISProperties.get("wibble", CMISProperties.HOST));
	}
	
	@Test
	public void testListDestinations() {
		String[] destinations = CMISProperties.listDestinations();
		String[] expected={"test", "test2"};
		Assert.assertArrayEquals(expected, destinations);
	}
	
	
}
