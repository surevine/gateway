package com.surevine.community.gateway.model.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SecurityLabelTest {

	@Test
	public void getAsFriendlyString() {
		assertEquals("COMMERCIAL IN CONFIDENCE US FR", SecurityLabel.asFriendlyString("01010101010101010101010"));
	}
}
