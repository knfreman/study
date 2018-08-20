package com.patrick.sso;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Patrick Pan
 *
 */
public class ResponseWrapper {

	public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
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

	public static ResponseWrapper newInstance(int statusCode) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		responseWrapper.statusCode = statusCode;
		return responseWrapper;
	}

	public static ResponseWrapper buildFailureResponse(int statusCode, String msg) {
		ResponseWrapper responseWrapper = ResponseWrapper.newInstance(statusCode);
		responseWrapper.content.put("msg", msg);
		return responseWrapper;
	}
}
