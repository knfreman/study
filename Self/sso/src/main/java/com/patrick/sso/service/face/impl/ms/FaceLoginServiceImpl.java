package com.patrick.sso.service.face.impl.ms;

import static com.patrick.sso.ResponseWrapper.buildFailureResponse;
import static com.patrick.sso.ResponseWrapper.newInstance;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
	private static final String SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ITokenProfileIdMap tokenProfileIdMap;

	private HttpHeaders invokeDetectAPIHeaders;
	private HttpHeaders invokeIdentifyAPIHeaders;

	public FaceLoginServiceImpl() {
		invokeDetectAPIHeaders = buildHttpHeaders(MediaType.APPLICATION_OCTET_STREAM);
		invokeIdentifyAPIHeaders = buildHttpHeaders(MediaType.APPLICATION_JSON);
	}

	@Override
	public ResponseWrapper login(InputStream image, String lang) {
		Response resp = getFaceId(image);
		if (!resp.isSuccess()) {
			return buildFailureResponse(resp.getHttpStatus(), resp.getMsg());
		}

		String faceId = resp.getMsg();
		LOGGER.debug("Face id is {}.", faceId);

		resp = identify(faceId);
		if (!resp.isSuccess()) {
			return buildFailureResponse(resp.getHttpStatus(), resp.getMsg());
		}

		String personId = resp.getMsg();
		LOGGER.debug("Person id is {}.", personId);

		String token = UUID.randomUUID().toString();
		tokenProfileIdMap.put(token, personId);

		ResponseWrapper responseWrapper = newInstance(HttpStatus.OK);
		responseWrapper.addField("token", token);
		return responseWrapper;
	}

	private HttpHeaders buildHttpHeaders(MediaType contentType) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(SUBSCRIPTION_KEY, Authentication.SUBSCRIPTION_KEY);
		httpHeaders.setContentType(contentType);
		return httpHeaders;
	}

	private Response getFaceId(InputStream image) {
		try {
			return invokeDetectAPI(image);
		} catch (URISyntaxException | RestClientException | JSONException e) {
			LOGGER.error("Exception occurs during invoking Microsoft Cognitive Service - Face Detect.", e);
			return Response.buildFailureResponse(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Response invokeDetectAPI(InputStream image) throws URISyntaxException {
		URI uri = new URI(DETECT_URL);
		HttpEntity<InputStream> entity = new HttpEntity<>(image, invokeDetectAPIHeaders);
		ResponseEntity<String> resp = restTemplate.postForEntity(uri, entity, String.class);

		int statusCode = resp.getStatusCodeValue();
		String responseEntity = resp.getBody();
		LOGGER.debug("Response entity is {}.", responseEntity);

		if (!HttpUtils.isStatusCode200(statusCode)) {
			return Response.buildFailureResponse(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return parseDetectAPIResponse(responseEntity);
	}

	private Response parseDetectAPIResponse(String responseEntity) {
		JSONArray jsonArray = new JSONArray(responseEntity);
		if (jsonArray.length() == 0) {
			LOGGER.warn("Cannot detect any faces.");
			return Response.buildFailureResponse(HttpStatus.BAD_REQUEST, "Cannot detect any faces.");
		}

		if (jsonArray.length() > 1) {
			LOGGER.warn("At least two faces are detected.");
			return Response.buildFailureResponse(HttpStatus.BAD_REQUEST, "At least two faces are detected.");
		}

		String faceId = jsonArray.getJSONObject(0).getString("faceId");
		return Response.buildSuccessResponse(faceId);
	}

	private Response identify(String faceId) {
		try {
			return invokeIdentifyAPI(faceId);
		} catch (URISyntaxException | RestClientException | JSONException e) {
			LOGGER.error("Exception occurs during invoking Microsoft Cognitive Service - Face Identify.", e);
			return Response.buildFailureResponse(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Response invokeIdentifyAPI(String faceId) throws URISyntaxException {
		URI uri = new URI(IDENTIFY_URL);
		String reqContent = buildIdentifyRequestContent(faceId);
		HttpEntity<String> entity = new HttpEntity<>(reqContent, invokeIdentifyAPIHeaders);
		ResponseEntity<String> resp = restTemplate.postForEntity(uri, entity, String.class);

		int statusCode = resp.getStatusCodeValue();
		String responseEntity = resp.getBody();
		LOGGER.debug("Response entity is {}.", responseEntity);

		if (!HttpUtils.isStatusCode200(statusCode)) {
			return Response.buildFailureResponse(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return parseIdentifyAPIResponse(responseEntity);
	}

	private String buildIdentifyRequestContent(String faceId) {
		JSONArray faceIds = new JSONArray();
		faceIds.put(faceId);

		JSONObject json = new JSONObject();

		json.put("faceIds", faceIds);
		json.put("personGroupId", PersonGroup.COLLEAGUE.getPersonGroupId());
		json.put("maxNumOfCandidatesReturned", 1);

		return json.toString();
	}

	private Response parseIdentifyAPIResponse(String responseEntity) {
		JSONArray jsonArray = new JSONArray(responseEntity);
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		JSONArray candidates = jsonObject.getJSONArray("candidates");
		if (candidates.length() == 0) {
			return Response.buildFailureResponse(HttpStatus.FORBIDDEN,
					"Sorry, I don't know who you are. Please ask Patrick Pan to introduce you to me.");
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
		private HttpStatus httpStatus;

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

		private static Response buildFailureResponse(HttpStatus httpStatus, String msg) {
			Response resp = buildResponse(msg);
			resp.success = false;
			resp.httpStatus = httpStatus;
			return resp;
		}

		private static Response buildFailureResponse(HttpStatus httpStatus) {
			Response resp = buildResponse(httpStatus.getReasonPhrase());
			resp.success = false;
			resp.httpStatus = httpStatus;
			return resp;
		}

		private boolean isSuccess() {
			return success;
		}

		private String getMsg() {
			return msg;
		}

		private HttpStatus getHttpStatus() {
			return httpStatus;
		}
	}
}
