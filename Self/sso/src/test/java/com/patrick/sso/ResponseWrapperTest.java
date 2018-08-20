package com.patrick.sso;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.patrick.sso.ResponseWrapper;

/**
 * 
 * @author Patrick Pan
 *
 */
public class ResponseWrapperTest {

	@Test
	public void testBuildFailureResponse() {
		final int statusCode = 400;
		final String msg = "Bad Request";
		ResponseWrapper responseWrapper = ResponseWrapper.buildFailureResponse(statusCode, msg);
		assertEquals(statusCode, responseWrapper.getStatusCode());

		Map<String, Object> content = responseWrapper.getContent();
		assertEquals(1, content.size());
		assertEquals(msg, content.get("msg"));
	}

	@Test
	public void testAddField() {
		final int statusCode = 200;
		final String key = "token";
		final String value = "abc-def-ghi";
		ResponseWrapper responseWrapper = ResponseWrapper.newInstance(statusCode);
		assertEquals(statusCode, responseWrapper.getStatusCode());

		responseWrapper.addField(key, value);

		Map<String, Object> content = responseWrapper.getContent();
		assertEquals(1, content.size());
		assertEquals(value, content.get(key));
	}
}
