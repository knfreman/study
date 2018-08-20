package com.patrick.sso.controller.face;

import static com.patrick.sso.TestUtils.verify;
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

import com.patrick.sso.ResponseWrapper;
import com.patrick.sso.service.face.IFaceLoginService;

/**
 * 
 * @author Patrick Pan
 *
 */
public class FaceLoginControllerTest {

	@Mock
	private IFaceLoginService faceLoginService;
	@InjectMocks
	private FaceLoginController faceLoginController;

	private static final ResponseWrapper responseWrapper = ResponseWrapper.buildFailureResponse(400,
			ResponseWrapper.INVALID_JSON_FORMAT);

	@Before
	public void initMocks() {
		// To initialize annotated fields
		MockitoAnnotations.initMocks(this);
		when(faceLoginService.login(any(), any())).thenReturn(responseWrapper);
	}

	@Test
	public void testLoginByFace0() throws IOException {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getInputStream()).thenThrow(new IOException("Unit Test"));
		HttpServletResponse resp = mock(HttpServletResponse.class);
		Map<String, Object> content = faceLoginController.loginByFace(req, resp);
		verify(content, 1, ResponseWrapper.INTERNAL_SERVER_ERROR);
	}

	@Test
	public void testLoginByFace1() throws IOException {
		ServletInputStream inputStream = mock(ServletInputStream.class);
		// Empty Request Body
		when(inputStream.isFinished()).thenReturn(true);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getInputStream()).thenReturn(inputStream);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		Map<String, Object> content = faceLoginController.loginByFace(req, resp);
		verify(content, 1, "Binary data of image is missing");
	}

	@Test
	public void testLoginByFace2() throws IOException {
		ServletInputStream inputStream = mock(ServletInputStream.class);
		when(inputStream.isFinished()).thenReturn(false);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getInputStream()).thenReturn(inputStream);
		when(req.getQueryString()).thenReturn("lang=en");
		HttpServletResponse resp = mock(HttpServletResponse.class);
		Map<String, Object> content = faceLoginController.loginByFace(req, resp);
		verify(content, 1, ResponseWrapper.INVALID_JSON_FORMAT);
	}
}
