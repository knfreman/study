package com.patrick.sso.service;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Patrick Pan
 *
 */
public class HttpUtils {

	private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();

	private static final Logger LOGGER = LoggerFactory.getLogger("ssoLogger");

	public static final String CONTENT_TYPE = "Content-Type";
	public static final String SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";

	public static HttpResponse sendRequest(HttpUriRequest request) throws IOException {
		return HTTP_CLIENT.execute(request);
	}

	public static String getResponseEntity(HttpEntity entity) throws IOException {
		String responseEntity = EntityUtils.toString(entity);
		LOGGER.debug("Response entity is {}.", responseEntity);
		return responseEntity;
	}

	public static boolean isStatusCode200(int statusCode) {
		if (statusCode != 200) {
			LOGGER.warn("Status code is not 200.");
			return false;
		}

		return true;
	}

	private HttpUtils() {
	}
}
