package com.patrick;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.patrick.Authentication.PersonGroup;
import com.patrick.Authentication.PersonGroup.Person;

/**
 * 
 * @author Patrick Pan
 *
 */
public class FaceRecognitionUtils {

	private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();
	private static final Logger LOGGER = LoggerFactory.getLogger("faceRecognitionlogger");

	private static final String DETECT_URL = "https://westus.api.cognitive.microsoft.com/face/v1.0/detect";
	private static final String IDENTIFY_URL = "https://westus.api.cognitive.microsoft.com/face/v1.0/identify";

	private static final PersonGroup personGroup = PersonGroup.COLLEAGUE;

	public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

	private static class Response {
		private String msg;
		private boolean success;

		private static Response buildResponse(String msg) {
			Response resp = new Response();
			resp.msg = msg;
			return resp;
		}

		private static Response buildSuccessResponse(String msg) {
			Response resp = buildResponse(msg);
			resp.success = true;
			return resp;
		}

		private static Response buildFailureResponse(String msg) {
			Response resp = buildResponse(msg);
			resp.success = false;
			return resp;
		}

		private boolean isSuccess() {
			return success;
		}

		private String getMsg() {
			return msg;
		}
	}

	public static JSONObject getPersonName(InputStream image) {
		Response resp = getFaceId(image);
		if (!resp.isSuccess()) {
			return buildResponse(false, resp.getMsg());
		}

		String faceId = resp.getMsg();
		LOGGER.debug("Face id is {}.", faceId);

		resp = identify(faceId);
		if (!resp.isSuccess()) {
			return buildResponse(false, resp.getMsg());
		}

		String personId = resp.getMsg();
		LOGGER.debug("Person id is {}.", personId);

		String personName = getPersonNameByPersonId(personId);

		if (personName.isEmpty()) {
			LOGGER.warn("Cannot get person name by person id [{}]", personId);
			return buildResponse(false, INTERNAL_SERVER_ERROR);
		}

		return buildResponse(true, personName);
	}

	private static JSONObject buildResponse(boolean isSuccess, String msg) {
		JSONObject json = new JSONObject();
		json.put("isSuccess", isSuccess);
		json.put("msg", msg);
		return json;
	}

	private static String getPersonNameByPersonId(String personId) {
		Person[] persons = personGroup.getPersons();

		for (Person person : persons) {
			if (person.getId().equalsIgnoreCase(personId)) {
				return person.getName();
			}
		}

		return "";
	}

	private static String getResponseEntity(HttpEntity entity) throws IOException {
		String responseEntity = EntityUtils.toString(entity);
		LOGGER.debug("Response entity is {}.", responseEntity);
		return responseEntity;
	}

	/* ============================== Face Detect ============================== */

	private static Response invokeDetectAPI(InputStream image) throws URISyntaxException, IOException {
		URI uri = new URIBuilder(DETECT_URL).build();

		HttpPost request = buildPostRequest(uri, ContentType.APPLICATION_OCTET_STREAM.getMimeType(),
				new InputStreamEntity(image));
		HttpResponse response = HTTP_CLIENT.execute(request);

		return parseDetectAPIResponse(response);
	}

	private static Response parseDetectAPIResponse(HttpResponse response) throws IOException {
		int statusCode = response.getStatusLine().getStatusCode();
		String responseEntity = getResponseEntity(response.getEntity());

		if (statusCode != 200) {
			return Response.buildFailureResponse(INTERNAL_SERVER_ERROR);
		}

		return parseDetectAPIResponse(responseEntity);
	}

	private static Response parseDetectAPIResponse(String responseEntity) {
		JSONArray jsonArray = new JSONArray(responseEntity);
		if (jsonArray.length() == 0) {
			LOGGER.warn("Cannot detect any faces.");
			return Response.buildFailureResponse("Cannot detect any faces.");
		}

		if (jsonArray.length() > 1) {
			LOGGER.warn("At least two faces are detected.");
			return Response.buildFailureResponse("At least two faces are detected.");
		}

		String faceId = jsonArray.getJSONObject(0).getString("faceId");
		return Response.buildSuccessResponse(faceId);
	}

	private static HttpPost buildPostRequest(URI uri, String contentType, HttpEntity reqEntity) {
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-Type", contentType);
		request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);
		request.setEntity(reqEntity);

		return request;
	}

	private static Response getFaceId(InputStream image) {
		try {
			return invokeDetectAPI(image);
		} catch (URISyntaxException | IOException | JSONException e) {
			LOGGER.error("Exception occurs during invoking Microsoft Cognitive Service - Face Detect.", e);
			return Response.buildFailureResponse(INTERNAL_SERVER_ERROR);
		}
	}

	/* ============================= Face Identify ============================= */

	private static Response invokeIdentifyAPI(String faceId) throws URISyntaxException, IOException {
		URI uri = new URIBuilder(IDENTIFY_URL).build();
		String reqContent = buildIdentifyRequestContent(faceId);

		StringEntity reqEntity = new StringEntity(reqContent);
		HttpPost request = buildPostRequest(uri, ContentType.APPLICATION_JSON.getMimeType(), reqEntity);
		HttpResponse response = HTTP_CLIENT.execute(request);
		return parseIdentifyAPIResponse(response);
	}

	private static String buildIdentifyRequestContent(String faceId) {
		JSONArray faceIds = new JSONArray();
		faceIds.put(faceId);

		JSONObject json = new JSONObject();

		json.put("faceIds", faceIds);
		json.put("personGroupId", personGroup.getPersonGroupId());
		json.put("maxNumOfCandidatesReturned", 1);

		return json.toString();
	}

	private static Response parseIdentifyAPIResponse(HttpResponse response) throws IOException {
		int statusCode = response.getStatusLine().getStatusCode();
		String responseEntity = getResponseEntity(response.getEntity());

		if (statusCode != 200) {
			return Response.buildFailureResponse(INTERNAL_SERVER_ERROR);
		}

		return parseIdentifyAPIResponse(responseEntity);
	}

	private static Response parseIdentifyAPIResponse(String responseEntity) {
		JSONArray jsonArray = new JSONArray(responseEntity);
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		JSONArray candidates = jsonObject.getJSONArray("candidates");
		if (candidates.length() == 0) {
			return Response.buildFailureResponse(
					"Sorry, I don't know who you are. Please ask Patrick to introduce you to me.");
		}

		String personId = candidates.getJSONObject(0).getString("personId");
		return Response.buildSuccessResponse(personId);
	}

	private static Response identify(String faceId) {
		try {
			return invokeIdentifyAPI(faceId);
		} catch (URISyntaxException | IOException | JSONException e) {
			LOGGER.error("Exception occurs during invoking Microsoft Cognitive Service - Face Identify.", e);
			return Response.buildFailureResponse(INTERNAL_SERVER_ERROR);
		}
	}

	private FaceRecognitionUtils() {
	}
}
