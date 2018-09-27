package com.patrick.sso.service.face.impl.ms;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.forbidden;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.patrick.sso.ResponseWrapper;
import com.patrick.sso.common.InputStreamHttpMessageConverter;
import com.patrick.sso.profileid.ITokenProfileIdMap;
import com.patrick.sso.profileid.impl.SimpleTokenProfileIdMap;
import com.patrick.sso.service.face.impl.ms.Authentication.PersonGroup;

import io.specto.hoverfly.junit.dsl.RequestMatcherBuilder;
import io.specto.hoverfly.junit.dsl.StubServiceBuilder;
import io.specto.hoverfly.junit.rule.HoverflyRule;

/**
 * 
 * @author Patrick Pan
 *
 */
public class FaceLoginServiceImplTest {

	private static final String LANG = "en";
	private static final String MSG = "msg";
	private static final String UNKNOWN_FACE_ID = "unknown_face_id";
	private static final String KNOWN_FACE_ID_FOR_EXCEPTION = "known_face_id_for_exception";
	private static final String KNOWN_FACE_ID = "known_face_id";
	private static final String PERSON_ID = "person_id";

	private static final int OK = 200;
	private static final int FORBIDDEN = 403;

	private static final String DETECT_BODY_EXCEPTION = "Exception.";
	private static final String DETECT_BODY_NO_FACES = "No faces.";
	private static final String DETECT_BODY_AT_LEASET_TWO_FACES = "At leaset two faces.";
	private static final String DETECT_BODY_UNKNOWN_FACES = "Unknown face.";
	private static final String DETECT_BODY_KNOWN_FACES_FOR_EXCEPTION = "Known face for exception.";
	private static final String DETECT_BODY_KNOWN_FACES = "Known face.";

	private static final String SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";
	private static final String CONTENT_TYPE = "Content-Type";

	private static final String HOST = "westus.api.cognitive.microsoft.com";
	private static final String DETECT_PATH = "/face/v1.0/detect";
	private static final String IDENTIFY_PATH = "/face/v1.0/identify";

	@ClassRule
	public static HoverflyRule hoverflyRule = HoverflyRule.inSimulationMode(dsl(
			// Exception
			buildStubServiceBuilderForDetectAPI(FORBIDDEN, DETECT_BODY_EXCEPTION),
			// No Faces
			buildStubServiceBuilderForDetectAPI(OK, DETECT_BODY_NO_FACES),
			// At leaset two Faces
			buildStubServiceBuilderForDetectAPI(OK, DETECT_BODY_AT_LEASET_TWO_FACES, "faceId_1", "faceId_2"),
			// Unknown Face
			buildStubServiceBuilderForDetectAPI(OK, DETECT_BODY_UNKNOWN_FACES, UNKNOWN_FACE_ID),
			// Known Face For Exception
			buildStubServiceBuilderForDetectAPI(OK, DETECT_BODY_KNOWN_FACES_FOR_EXCEPTION, KNOWN_FACE_ID_FOR_EXCEPTION),
			// Known Face
			buildStubServiceBuilderForDetectAPI(OK, DETECT_BODY_KNOWN_FACES, KNOWN_FACE_ID),
			// Unknown Identity
			buildStubServiceBuilderForIdentifyAPI(OK, UNKNOWN_FACE_ID, ""),
			// Exception
			buildStubServiceBuilderForIdentifyAPI(FORBIDDEN, KNOWN_FACE_ID_FOR_EXCEPTION, ""),
			// Known Identity
			buildStubServiceBuilderForIdentifyAPI(OK, KNOWN_FACE_ID, PERSON_ID)));

	private ITokenProfileIdMap tokenProfileIdMap;
	private FaceLoginServiceImpl faceLoginService;

	@Before
	public void beforeTest() throws Exception {
		faceLoginService = new FaceLoginServiceImpl();
		tokenProfileIdMap = new SimpleTokenProfileIdMap();

		injectField("tokenProfileIdMap", tokenProfileIdMap);
		injectField("restTemplate", buildRestTemplate());
	}

	@Test
	public void testLogin0() {
		testLogin(DETECT_BODY_EXCEPTION, 500, "Internal Server Error");
	}

	@Test
	public void testLogin1() {
		testLogin(DETECT_BODY_NO_FACES, 400, "Cannot detect any faces.");
	}

	@Test
	public void testLogin2() {
		testLogin(DETECT_BODY_AT_LEASET_TWO_FACES, 400, "At least two faces are detected.");
	}

	@Test
	public void testLogin3() {
		testLogin(DETECT_BODY_UNKNOWN_FACES, 403,
				"Sorry, I don't know who you are. Please ask Patrick Pan to introduce you to me.");
	}

	@Test
	public void testLogin4() {
		testLogin(DETECT_BODY_KNOWN_FACES_FOR_EXCEPTION, 500, "Internal Server Error");
	}

	@Test
	public void testLogin5() {
		String token = testLogin(DETECT_BODY_KNOWN_FACES, 200);
		assertEquals(PERSON_ID, tokenProfileIdMap.get(token));
	}

	private static String buildDetectAPIResponse(String... faceIds) {
		JSONArray jsonArray = new JSONArray();

		if (Objects.nonNull(faceIds)) {
			for (String faceId : faceIds) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("faceId", faceId);
				jsonArray.put(jsonObject);
			}
		}

		return jsonArray.toString();
	}

	private static String buildIdentifyAPIRequestBody(String faceId) {
		JSONArray faceIds = new JSONArray();
		faceIds.put(faceId);

		JSONObject json = new JSONObject();

		json.put("faceIds", faceIds);
		json.put("personGroupId", PersonGroup.COLLEAGUE.getPersonGroupId());
		json.put("maxNumOfCandidatesReturned", 1);

		return json.toString();
	}

	private static String buildIdentifyAPIResponse(String personId) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonArray.put(jsonObject);

		JSONArray candidates = new JSONArray();
		jsonObject.put("candidates", candidates);

		if (!StringUtils.isEmpty(personId)) {
			JSONObject candidate = new JSONObject();
			candidate.put("personId", personId);
			candidates.put(candidate);
		}

		return jsonArray.toString();
	}

	private static StubServiceBuilder buildStubServiceBuilder(int statusCode, String path, String requestContentType,
			String requestBody, String responseContentType, String responseBody) {
		RequestMatcherBuilder builder = service(HOST).post(path)
				.header(SUBSCRIPTION_KEY, Authentication.SUBSCRIPTION_KEY).header(CONTENT_TYPE, requestContentType)
				.body(requestBody);

		if (statusCode == FORBIDDEN) {
			return builder.willReturn(forbidden());
		} else {
			return builder.willReturn(success(responseBody, responseContentType));
		}
	}

	private static StubServiceBuilder buildStubServiceBuilderForDetectAPI(int statusCode, String body,
			String... faceIds) {
		return buildStubServiceBuilder(statusCode, DETECT_PATH, APPLICATION_OCTET_STREAM_VALUE, body,
				APPLICATION_JSON_VALUE, buildDetectAPIResponse(faceIds));
	}

	private static StubServiceBuilder buildStubServiceBuilderForIdentifyAPI(int statusCode, String faceId,
			String personId) {
		return buildStubServiceBuilder(statusCode, IDENTIFY_PATH, APPLICATION_JSON_VALUE,
				buildIdentifyAPIRequestBody(faceId), APPLICATION_JSON_VALUE, buildIdentifyAPIResponse(personId));
	}

	private void testLogin(String body, int expectedStatusCode, String expectedMsg) {
		InputStream inputStream = buildInputStream(body);
		ResponseWrapper responseWrapper = faceLoginService.login(inputStream, LANG);
		assertEquals(expectedStatusCode, responseWrapper.getStatusCode());
		assertEquals(expectedMsg, responseWrapper.getContent().get(MSG));
	}

	private String testLogin(String body, int expectedStatusCode) {
		InputStream inputStream = buildInputStream(body);
		ResponseWrapper responseWrapper = faceLoginService.login(inputStream, LANG);
		assertEquals(expectedStatusCode, responseWrapper.getStatusCode());
		return responseWrapper.getContent().get("token").toString();
	}

	private InputStream buildInputStream(String content) {
		return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
	}

	private void injectField(String name, Object val) throws Exception {
		Class<FaceLoginServiceImpl> clazz = FaceLoginServiceImpl.class;
		Field field = clazz.getDeclaredField(name);
		field.setAccessible(true);
		field.set(faceLoginService, val);
	}

	private RestTemplate buildRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new InputStreamHttpMessageConverter());
		return restTemplate;
	}
}
