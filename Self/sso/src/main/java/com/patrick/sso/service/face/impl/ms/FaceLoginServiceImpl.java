package com.patrick.sso.service.face.impl.ms;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.patrick.sso.ResponseWrapper;
import com.patrick.sso.profileid.ITokenProfileIdMap;
import com.patrick.sso.service.HttpUtils;
import com.patrick.sso.service.face.AbstractFaceLoginService;
import com.patrick.sso.service.face.impl.ms.Authentication.PersonGroup;

/**
 * 
 * @author Patrick Pan
 *
 */
@Service
public class FaceLoginServiceImpl extends AbstractFaceLoginService {

	private static final String DETECT_URL = "https://westus.api.cognitive.microsoft.com/face/v1.0/detect";
	private static final String IDENTIFY_URL = "https://westus.api.cognitive.microsoft.com/face/v1.0/identify";

	@Autowired
	private ITokenProfileIdMap tokenProfileIdMap;

	@Override
	public ResponseWrapper login(InputStream image, String lang) {
		Response resp = getFaceId(image);
		if (!resp.isSuccess()) {
			return ResponseWrapper.buildFailureResponse(403, resp.getMsg());
		}

		String faceId = resp.getMsg();
		LOGGER.debug("Face id is " + faceId);

		resp = identify(faceId);
		if (!resp.isSuccess()) {
			return ResponseWrapper.buildFailureResponse(403, resp.getMsg());
		}

		String personId = resp.getMsg();
		LOGGER.debug("Person id is " + personId);

		String token = UUID.randomUUID().toString();
		tokenProfileIdMap.put(token, personId);

		ResponseWrapper responseWrapper = ResponseWrapper.newInstance(200);
		responseWrapper.addField("token", token);
		return responseWrapper;
	}

	private HttpPost buildPostRequest(URI uri, String contentType, HttpEntity reqEntity) {
		HttpPost request = new HttpPost(uri);
		request.setHeader(HttpUtils.CONTENT_TYPE, contentType);
		request.setHeader(HttpUtils.SUBSCRIPTION_KEY, Authentication.SUBSCRIPTION_KEY);
		request.setEntity(reqEntity);

		return request;
	}

	private Response getFaceId(InputStream image) {
		try {
			return invokeDetectAPI(image);
		} catch (URISyntaxException | IOException | JSONException e) {
			LOGGER.error("Exception occurs during invoking Microsoft Cognitive Service - Face Detect .", e);
			return Response.buildFailureResponse(ResponseWrapper.INTERNAL_SERVER_ERROR);
		}
	}

	private Response invokeDetectAPI(InputStream image)
			throws URISyntaxException, ClientProtocolException, IOException {
		URI uri = new URIBuilder(DETECT_URL).build();

		HttpPost request = buildPostRequest(uri, ContentType.APPLICATION_OCTET_STREAM.getMimeType(),
				new InputStreamEntity(image));
		HttpResponse response = HttpUtils.sendRequest(request);

		return parseDetectAPIResponse(response);
	}

	private Response parseDetectAPIResponse(HttpResponse response) throws ParseException, IOException, JSONException {
		int statusCode = response.getStatusLine().getStatusCode();
		String responseEntity = HttpUtils.getResponseEntity(response.getEntity());

		if (!HttpUtils.isStatusCode200(statusCode)) {
			return Response.buildFailureResponse(ResponseWrapper.INTERNAL_SERVER_ERROR);
		}

		return parseDetectAPIResponse(responseEntity);
	}

	private Response parseDetectAPIResponse(String responseEntity) throws JSONException {
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

	private Response identify(String faceId) {
		try {
			return invokeIdentifyAPI(faceId);
		} catch (URISyntaxException | IOException | JSONException e) {
			LOGGER.error("Exception occurs during invoking Microsoft Cognitive Service - Face Identify .", e);
			return Response.buildFailureResponse(ResponseWrapper.INTERNAL_SERVER_ERROR);
		}
	}

	private Response invokeIdentifyAPI(String faceId)
			throws URISyntaxException, ClientProtocolException, IOException, JSONException {
		URI uri = new URIBuilder(IDENTIFY_URL).build();
		String reqContent = buildIdentifyRequestContent(faceId);

		StringEntity reqEntity = new StringEntity(reqContent);
		HttpPost request = buildPostRequest(uri, ContentType.APPLICATION_JSON.getMimeType(), reqEntity);
		HttpResponse response = HttpUtils.sendRequest(request);
		return parseIdentifyAPIResponse(response);
	}

	private String buildIdentifyRequestContent(String faceId) throws JSONException {
		JSONArray faceIds = new JSONArray();
		faceIds.put(faceId);

		JSONObject json = new JSONObject();

		json.put("faceIds", faceIds);
		json.put("personGroupId", PersonGroup.AIA.getPersonGroupId());
		json.put("maxNumOfCandidatesReturned", 1);

		return json.toString();
	}

	private Response parseIdentifyAPIResponse(HttpResponse response) throws ParseException, IOException, JSONException {
		int statusCode = response.getStatusLine().getStatusCode();
		String responseEntity = HttpUtils.getResponseEntity(response.getEntity());

		if (!HttpUtils.isStatusCode200(statusCode)) {
			return Response.buildFailureResponse(ResponseWrapper.INTERNAL_SERVER_ERROR);
		}

		return parseIdentifyAPIResponse(responseEntity);
	}

	private Response parseIdentifyAPIResponse(String responseEntity) throws JSONException {
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

	/**
	 * 
	 * @author Patrick Pan
	 *
	 */
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
