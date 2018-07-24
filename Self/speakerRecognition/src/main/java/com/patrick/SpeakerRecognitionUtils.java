package com.patrick;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * 
 * @author Patrick Pan
 *
 */
public class SpeakerRecognitionUtils {
	private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();
	private static final String IDENTIFICATION_PROFILE_IDS = buildIdentificationProfileIds();

	private static final String IDENTIFICATION_URL = new StringBuilder(
			"https://westus.api.cognitive.microsoft.com/spid/v1.0/identify?identificationProfileIds=")
					.append(IDENTIFICATION_PROFILE_IDS).append("&shortAudio=").append(true).toString();

	private static final Logger LOGGER = LogManager.getLogger("SpeakerRecognitionlogger");

	private static URI buildURI(String url) {
		URI uri = null;

		try {
			uri = new URIBuilder(url).build();
		} catch (URISyntaxException e) {
			LOGGER.error("Exception occurs during building URI.", e);
		}

		return uri;
	}

	private static String getResponseEntity(HttpEntity entity) {
		if (entity != null) {
			try {
				return EntityUtils.toString(entity);
			} catch (IOException | ParseException e) {
				LOGGER.error("Exception occurs during parsing http response.", e);
			}
		}

		return "";
	}

	private static HttpResponse sendRequest(HttpUriRequest request) {
		try {
			return HTTP_CLIENT.execute(request);
		} catch (IOException e) {
			LOGGER.error("Exception occurs during sending http request.", e);
			return null;
		}
	}

	private static String identify(String filePath) {
		URI uri = buildURI(IDENTIFICATION_URL);

		if (uri == null) {
			return "";
		}

		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-Type", "application/octet-stream");
		request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);

		try {
			request.setEntity(new InputStreamEntity(new FileInputStream(filePath)));
		} catch (IOException e) {
			LOGGER.error("Exception occurs during setting http request entity.", e);
			return "";
		}

		HttpResponse response = sendRequest(request);
		if (response == null) {
			return "";
		}

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 202) {
			LOGGER.info(getResponseEntity(response.getEntity()));
			return "";
		}

		Header header = response.getFirstHeader("Operation-Location");
		if (header == null) {
			LOGGER.warn("'Operation-Location' cannot be found in response header.");
			return "";
		}

		String value = header.getValue();
		return value.substring(value.lastIndexOf('/'));
	}

	private static JSONObject getOperationStatus(String operationId) {
		URI uri = buildURI("https://westus.api.cognitive.microsoft.com/spid/v1.0/operations/" + operationId);

		if (uri == null) {
			return new JSONObject();
		}

		HttpGet request = new HttpGet(uri);
		request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);

		HttpResponse response = sendRequest(request);
		if (response == null) {
			return new JSONObject();
		}

		String responseEntity = getResponseEntity(response.getEntity());
		if (responseEntity.isEmpty()) {
			return new JSONObject();
		}

		return new JSONObject(responseEntity);
	}

	private static String buildIdentificationProfileIds() {
		StringBuilder identificationProfileIds = new StringBuilder();

		Authentication.IdentificationProfile[] identificationProfiles = Authentication.IdentificationProfile.values();

		String delimiter = "";
		for (int i = 0; i < identificationProfiles.length; i++) {
			identificationProfileIds.append(delimiter).append(identificationProfiles[i].getProfileId());
			delimiter = ",";
		}

		return identificationProfileIds.toString();
	}

	public static JSONObject getIdentification(String filePath) {
		String operationId = identify(filePath);

		if (operationId.isEmpty()) {
			return new JSONObject();
		}

		JSONObject operationStatus;

		do {
			waitForOneSecond();
			operationStatus = getOperationStatus(operationId);
		} while (operationStatus.has("status") && ("notstarted".equalsIgnoreCase(operationStatus.getString("status"))
				|| "running".equalsIgnoreCase(operationStatus.getString("status"))));

		LOGGER.debug("Operation Status is: " + operationStatus);
		return operationStatus;
	}

	private static void waitForOneSecond() {
		try {
			TimeUnit.SECONDS.sleep(1l);
		} catch (InterruptedException e) {
			LOGGER.error("Exception occurs in TimeUnit#sleep.", e);
		}
	}

	private SpeakerRecognitionUtils() {
	}
}
