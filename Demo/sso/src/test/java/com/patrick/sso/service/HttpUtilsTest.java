package com.patrick.sso.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * @author Patrick Pan
 *
 */
public class HttpUtilsTest {

	@Test
	public void testIsStatusCode200() {
		assertTrue(HttpUtils.isStatusCode200(200));
		assertFalse(HttpUtils.isStatusCode200(404));
	}
}
