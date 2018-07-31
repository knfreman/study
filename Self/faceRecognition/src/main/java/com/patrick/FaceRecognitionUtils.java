package com.patrick;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.patrick.Authentication.PersonGroup;
import com.patrick.Authentication.PersonGroup.Person;

/**
 * 
 * @author Patrick Pan
 *
 */
public class FaceRecognitionUtils {

	private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();
	private static final Logger LOGGER = LogManager.getLogger("FaceRecognitionlogger");

	private static final String DETECT_URL = "https://westus.api.cognitive.microsoft.com/face/v1.0/detect";
	private static final String IDENTIFY_URL = "https://westus.api.cognitive.microsoft.com/face/v1.0/identify";

	private static final PersonGroup personGroup = PersonGroup.Colleague;

	private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

	private static URI buildURI(String url) {
		URI uri = null;

		try {
			uri = new URIBuilder(url).build();
		} catch (URISyntaxException e) {
			LOGGER.error("Exception occurs during building URI.", e);
		}

		return uri;
	}

	private static HttpResponse sendRequest(HttpUriRequest request) {
		try {
			return HTTP_CLIENT.execute(request);
		} catch (IOException e) {
			LOGGER.error("Exception occurs during sending http request.", e);
			return null;
		}
	}

	private static StringEntity buildEntity(String content) {
		try {
			return new StringEntity(content);
		} catch (Exception e) {
			LOGGER.error("Exception occurs in FaceRecognitionUtils#buildEntity.", e);
			return null;
		}
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

	private static HttpPost buildPostRequest(URI uri, StringEntity reqEntity) {
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);
		request.setEntity(reqEntity);

		return request;
	}

	private static Response getFaceId(String imageURI) {
		URI uri = buildURI(DETECT_URL);

		if (uri == null) {
			return Response.buildFailureResponse(INTERNAL_SERVER_ERROR);
		}

		JSONObject json = new JSONObject();
		json.put("url", imageURI);
		StringEntity reqEntity = buildEntity(json.toString());

		if (reqEntity == null) {
			return Response.buildFailureResponse(INTERNAL_SERVER_ERROR);
		}

		HttpPost request = buildPostRequest(uri, reqEntity);

		HttpResponse response = sendRequest(request);

		if (response == null) {
			return Response.buildFailureResponse(INTERNAL_SERVER_ERROR);
		}

		String responseEntity = getResponseEntity(response.getEntity());

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			LOGGER.warn("Status code is not 200.");
			LOGGER.debug("Response entity is " + responseEntity);
			return Response.buildFailureResponse(INTERNAL_SERVER_ERROR);
		}

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

	private static Response identify(String faceId) {
		URI uri = buildURI(IDENTIFY_URL);

		if (uri == null) {
			return Response.buildFailureResponse(INTERNAL_SERVER_ERROR);
		}

		JSONArray faceIds = new JSONArray();
		faceIds.put(faceId);

		JSONObject json = new JSONObject();
		json.put("faceIds", faceIds);
		json.put("personGroupId", personGroup.getPersonGroupId());
		json.put("maxNumOfCandidatesReturned", 1);

		StringEntity reqEntity = buildEntity(json.toString());

		if (reqEntity == null) {
			return Response.buildFailureResponse(INTERNAL_SERVER_ERROR);
		}

		HttpPost request = buildPostRequest(uri, reqEntity);

		HttpResponse response = sendRequest(request);

		if (response == null) {
			return Response.buildFailureResponse(INTERNAL_SERVER_ERROR);
		}

		String responseEntity = getResponseEntity(response.getEntity());
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			LOGGER.warn("Status code is not 200.");
			LOGGER.debug("Response entity is " + responseEntity);
			return Response.buildFailureResponse(INTERNAL_SERVER_ERROR);
		}

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

	public static JSONObject getPersonName(String imageURI) {
		Response resp = getFaceId(imageURI);
		if (!resp.isSuccess()) {
			return buildResponse(false, resp.getMsg());
		}

		String faceId = resp.getMsg();
		LOGGER.debug("Face id is " + faceId);

		resp = identify(faceId);
		if (!resp.isSuccess()) {
			return buildResponse(false, resp.getMsg());
		}

		String personId = resp.getMsg();
		LOGGER.debug("Person id is " + personId);

		String personName = getPersonNameByPersonId(personId);

		if (personName.isEmpty()) {
			LOGGER.warn("Cannot get person name by person id [" + personId + "]");
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
}
