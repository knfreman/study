package com.patrick.sso;

import static org.junit.Assert.assertEquals;

import java.util.Map;

/**
 * 
 * @author Patrick Pan
 *
 */
public class TestUtils {

	public static void verify(Map<String, Object> content, int expectedSize, String expectedMsg) {
		assertEquals(expectedSize, content.size());
		assertEquals(expectedMsg, content.get("msg"));
	}
}
