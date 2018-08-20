package com.patrick.sso.service;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.patrick.sso.service.face.impl.ms.Authentication;

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

	public static HttpResponse sendRequest(HttpUriRequest request) throws ClientProtocolException, IOException {
		return HTTP_CLIENT.execute(request);
	}

	public static String getResponseEntity(HttpEntity entity) throws ParseException, IOException {
		String responseEntity = EntityUtils.toString(entity);
		LOGGER.debug("Response entity is " + responseEntity);
		return responseEntity;
	}

	public static HttpPost buildPostRequest(URI uri, String contentType, HttpEntity reqEntity) {
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-Type", contentType);
		request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);
		request.setEntity(reqEntity);

		return request;
	}

	public static boolean isStatusCode200(int statusCode) {
		if (statusCode != 200) {
			LOGGER.warn("Status code is not 200.");
			return false;
		}

		return true;
	}
}
