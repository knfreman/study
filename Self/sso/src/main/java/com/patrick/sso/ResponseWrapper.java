package com.patrick.sso;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

/**
 * 
 * @author Patrick Pan
 *
 */
public class ResponseWrapper {
	public static final String INVALID_JSON_FORMAT = "Invalid JSON Format";

	private int statusCode;
	private Map<String, Object> content = new HashMap<>();

	public int getStatusCode() {
		return statusCode;
	}

	public Map<String, Object> getContent() {
		return content;
	}

	public void addField(String key, Object value) {
		content.put(key, value);
	}

	public static ResponseWrapper newInstance(HttpStatus httpStatus) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		responseWrapper.statusCode = httpStatus.value();
		return responseWrapper;
	}

	public static ResponseWrapper buildFailureResponse(HttpStatus httpStatus, String msg) {
		ResponseWrapper responseWrapper = ResponseWrapper.newInstance(httpStatus);
		responseWrapper.content.put("msg", msg);
		return responseWrapper;
	}

	public static ResponseWrapper buildFailureResponse(HttpStatus httpStatus) {
		ResponseWrapper responseWrapper = ResponseWrapper.newInstance(httpStatus);
		responseWrapper.content.put("msg", httpStatus.getReasonPhrase());
		return responseWrapper;
	}
}
