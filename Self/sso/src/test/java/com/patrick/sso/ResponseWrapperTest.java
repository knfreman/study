package com.patrick.sso;

import static com.patrick.sso.ResponseWrapper.INVALID_JSON_FORMAT;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 * 
 * @author Patrick Pan
 *
 */
public class ResponseWrapperTest {

	@Test
	public void testBuildFailureResponse0() {
		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
		final int statusCode = httpStatus.value();
		final String msg = httpStatus.getReasonPhrase();
		ResponseWrapper responseWrapper = ResponseWrapper.buildFailureResponse(httpStatus);
		assertEquals(statusCode, responseWrapper.getStatusCode());

		Map<String, Object> content = responseWrapper.getContent();
		assertEquals(1, content.size());
		assertEquals(msg, content.get("msg"));
	}

	@Test
	public void testBuildFailureResponse1() {
		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
		final int statusCode = httpStatus.value();
		final String msg = INVALID_JSON_FORMAT;
		ResponseWrapper responseWrapper = ResponseWrapper.buildFailureResponse(httpStatus, INVALID_JSON_FORMAT);
		assertEquals(statusCode, responseWrapper.getStatusCode());

		Map<String, Object> content = responseWrapper.getContent();
		assertEquals(1, content.size());
		assertEquals(msg, content.get("msg"));
	}

	@Test
	public void testAddField() {
		HttpStatus httpStatus = HttpStatus.OK;
		final String key = "token";
		final String value = "abc-def-ghi";
		ResponseWrapper responseWrapper = ResponseWrapper.newInstance(httpStatus);
		assertEquals(httpStatus.value(), responseWrapper.getStatusCode());

		responseWrapper.addField(key, value);

		Map<String, Object> content = responseWrapper.getContent();
		assertEquals(1, content.size());
		assertEquals(value, content.get(key));
	}
}
