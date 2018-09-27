package com.patrick.sso.common;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.Test;

/**
 * 
 * @author Patrick Pan
 *
 */
public class CommonUtilsTest {

	@Test
	public void testParseQueryString() {
		final String queryString = "lang=abc&username=Patrick&p1=p2=p3";
		Map<String, String> map = CommonUtils.parseQueryString(queryString);
		assertEquals(2, map.size());
		assertEquals("abc", map.get("lang"));
		assertEquals("Patrick", map.get("username"));

		map = CommonUtils.parseQueryString(null);
		assertEquals(0, map.size());
	}

	@Test
	public void testInputStreamToString() {
		final String str = new StringBuilder("test1").append(System.lineSeparator()).append("test2").toString();
		InputStream inputStream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
		String newStr = CommonUtils.inputStreamToString(inputStream);
		assertEquals(str, newStr);
	}
}
