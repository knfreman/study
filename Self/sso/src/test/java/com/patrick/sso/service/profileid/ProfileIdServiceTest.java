package com.patrick.sso.service.profileid;

import static com.patrick.sso.TestUtils.verify;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.patrick.sso.ResponseWrapper;
import com.patrick.sso.profileid.ITokenProfileIdMap;

/**
 * 
 * @author Patrick Pan
 *
 */
public class ProfileIdServiceTest {

	@Mock
	private ITokenProfileIdMap tokenProfileMap;
	@InjectMocks
	private ProfileIdService profileIdService;

	private static final String TOKEN = "token";

	@Before
	public void initMocks() {
		// To initialize annotated fields
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetProfileId0() throws IOException {
		final String token = "abcd-efgh-ijkl";
		final String profileId = "mnop-qrst-uvwx";

		when(tokenProfileMap.get(token)).thenReturn(profileId);

		JSONObject json = new JSONObject();
		json.put(TOKEN, token);
		final String str = json.toString();
		InputStream inputStream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));

		ResponseWrapper responseWrapper = profileIdService.getProfileId(inputStream);
		assertEquals(200, responseWrapper.getStatusCode());

		Map<String, Object> content = responseWrapper.getContent();
		assertEquals(1, content.size());
		assertEquals(profileId, content.get("profileId"));
	}

	@Test
	public void testGetProfileId1() throws IOException {
		final String str = "{abc?".toString();
		InputStream inputStream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
		ResponseWrapper responseWrapper = profileIdService.getProfileId(inputStream);
		assertEquals(400, responseWrapper.getStatusCode());
		Map<String, Object> content = responseWrapper.getContent();
		verify(content, 1, ResponseWrapper.INVALID_JSON_FORMAT);
	}

	@Test
	public void testGetProfileId2() throws IOException {
		JSONObject json = new JSONObject();
		json.put("msg", "Hello, world!");
		final String str = json.toString();
		InputStream inputStream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
		ResponseWrapper responseWrapper = profileIdService.getProfileId(inputStream);
		assertEquals(400, responseWrapper.getStatusCode());
		Map<String, Object> content = responseWrapper.getContent();
		verify(content, 1, ResponseWrapper.INVALID_JSON_FORMAT);
	}

	@Test
	public void testGetProfileId3() throws IOException {
		final String token = "abcd-efgh-ijkl";
		JSONObject json = new JSONObject();
		json.put(TOKEN, token);
		final String str = json.toString();
		InputStream inputStream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));

		ResponseWrapper responseWrapper = profileIdService.getProfileId(inputStream);
		assertEquals(204, responseWrapper.getStatusCode());

		Map<String, Object> content = responseWrapper.getContent();
		verify(content, 0, null);
	}
}
