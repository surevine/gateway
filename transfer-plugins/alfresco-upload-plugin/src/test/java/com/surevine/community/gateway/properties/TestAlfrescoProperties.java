package com.surevine.community.gateway.properties;
import org.junit.Assert;
import org.junit.Test;

import com.surevine.community.gateway.properties.AlfrescoProperties;


public class TestAlfrescoProperties {

	
	@Test
	public void testHostname() {
		Assert.assertEquals("10.66.2.140", AlfrescoProperties.get("test", AlfrescoProperties.HOST));
		Assert.assertEquals("10.66.2.140", AlfrescoProperties.get("test2", AlfrescoProperties.HOST));
	}
	
	@Test
	public void testUsername() {
		Assert.assertEquals("admin", AlfrescoProperties.get("test", AlfrescoProperties.USERNAME));
		Assert.assertEquals("admin2", AlfrescoProperties.get("test2", AlfrescoProperties.USERNAME));
	}
	
	@Test
	public void testPassword() {
		Assert.assertEquals("Password12345", AlfrescoProperties.get("test", AlfrescoProperties.PASSWORD));
		Assert.assertEquals("Password123452", AlfrescoProperties.get("test2", AlfrescoProperties.PASSWORD));
	}
	
	@Test
	public void testSite() {
		Assert.assertEquals("test", AlfrescoProperties.get("test", AlfrescoProperties.SITE));
		Assert.assertEquals("test2", AlfrescoProperties.get("test2", AlfrescoProperties.SITE));
	}
	
	@Test
	public void testFolder() {
		Assert.assertEquals("testFolder", AlfrescoProperties.get("test", AlfrescoProperties.FOLDER));
		Assert.assertEquals("testFolder2", AlfrescoProperties.get("test2", AlfrescoProperties.FOLDER));
	}
	
	@Test (expected = java.util.MissingResourceException.class)
	public void testDestinationDoesNotExist() {
		Assert.assertEquals(null, AlfrescoProperties.get("wibble", AlfrescoProperties.HOST));
	}
	
	@Test
	public void testListDestinations() {
		String[] destinations = AlfrescoProperties.listDestinations();
		String[] expected={"test", "test2"};
		Assert.assertArrayEquals(expected, destinations);
	}
	
	
}
