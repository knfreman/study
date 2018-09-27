package com.patrick.sso.controller.face;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patrick.sso.ResponseWrapper;
import com.patrick.sso.common.CommonUtils;
import com.patrick.sso.service.face.IFaceLoginService;

/**
 * 
 * @author Patrick Pan
 *
 */
@RestController
@RequestMapping(value = "/login/face")
public class FaceLoginController {

	private static final Logger LOGGER = LoggerFactory.getLogger("ssoLogger");

	@Autowired
	private IFaceLoginService faceLoginService;

	@CrossOrigin("*")
	@PostMapping(consumes = "application/octet-stream", produces = "application/json")
	public Map<String, Object> loginByFace(HttpServletRequest req, HttpServletResponse resp) {
		ResponseWrapper responseWrapper = null;
		try {
			responseWrapper = loginByFace(req);
		} catch (IOException e) {
			LOGGER.error("Exception occurs in FaceLoginController.loginByFace", e);
			responseWrapper = ResponseWrapper.buildFailureResponse(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		resp.setStatus(responseWrapper.getStatusCode());
		resp.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		return responseWrapper.getContent();
	}

	private ResponseWrapper loginByFace(HttpServletRequest req) throws IOException {
		String lang = CommonUtils.parseQueryString(req.getQueryString()).get("lang");
		LOGGER.debug("Receive parameter: lang = [{}]", lang);
		try (ServletInputStream inputStream = req.getInputStream()) {
			if (inputStream.isFinished()) {
				return ResponseWrapper.buildFailureResponse(HttpStatus.BAD_REQUEST, "Binary data of image is missing");
			}

			return faceLoginService.login(inputStream, lang);
		}
	}
}
