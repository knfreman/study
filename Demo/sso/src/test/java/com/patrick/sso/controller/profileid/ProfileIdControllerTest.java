package com.patrick.sso.controller.profileid;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import com.patrick.sso.ResponseWrapper;
import com.patrick.sso.TestUtils;
import com.patrick.sso.service.profileid.ProfileIdService;

/**
 * 
 * @author Patrick Pan
 *
 */
public class ProfileIdControllerTest {

	@Mock
	private ProfileIdService profileIdService;
	@InjectMocks
	private ProfileIdController profileIdController;

	private static final ResponseWrapper responseWrapper = ResponseWrapper.buildFailureResponse(HttpStatus.BAD_REQUEST,
			ResponseWrapper.INVALID_JSON_FORMAT);

	@Before
	public void initMocks() throws IOException {
		// To initialize annotated fields
		MockitoAnnotations.initMocks(this);
		when(profileIdService.getProfileId(any())).thenReturn(responseWrapper);
	}

	@Test
	public void testGetProfile0() throws IOException {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getInputStream()).thenThrow(new IOException("Unit Test"));
		HttpServletResponse resp = mock(HttpServletResponse.class);
		Map<String, Object> content = profileIdController.getProfile(req, resp);
		TestUtils.verify(content, 1, "Internal Server Error");
	}

	@Test
	public void testGetProfile1() throws IOException {
		ServletInputStream inputStream = mock(ServletInputStream.class);
		// Empty Request Body
		when(inputStream.isFinished()).thenReturn(true);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getInputStream()).thenReturn(inputStream);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		Map<String, Object> content = profileIdController.getProfile(req, resp);
		TestUtils.verify(content, 1, ResponseWrapper.INVALID_JSON_FORMAT);
	}

	@Test
	public void testGetProfile2() throws IOException {
		ServletInputStream inputStream = mock(ServletInputStream.class);
		when(inputStream.isFinished()).thenReturn(false);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getInputStream()).thenReturn(inputStream);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		Map<String, Object> content = profileIdController.getProfile(req, resp);
		TestUtils.verify(content, 1, ResponseWrapper.INVALID_JSON_FORMAT);
	}
}
