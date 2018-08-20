package com.patrick.sso.controller.profileid;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patrick.sso.ResponseWrapper;
import com.patrick.sso.service.profileid.ProfileIdService;

/**
 * 
 * @author Patrick Pan
 *
 */
@RestController
@RequestMapping(value = "/profileid")
public class ProfileIdController {

	private static final Logger LOGGER = LoggerFactory.getLogger("ssoLogger");

	@Autowired
	private ProfileIdService profileIdService;

	@PostMapping(consumes = "application/json", produces = "application/json")
	public Map<String, Object> getProfileId(HttpServletRequest req, HttpServletResponse resp) {
		ResponseWrapper responseWrapper = null;

		try {
			responseWrapper = getProfileId(req);
		} catch (IOException e) {
			LOGGER.error("Exception occurs in FaceLoginController.loginByFace", e);
			responseWrapper = ResponseWrapper.buildFailureResponse(500, ResponseWrapper.INTERNAL_SERVER_ERROR);
		}

		resp.setStatus(responseWrapper.getStatusCode());
		return responseWrapper.getContent();
	}

	private ResponseWrapper getProfileId(HttpServletRequest req) throws IOException {
		try (ServletInputStream inputStream = req.getInputStream()) {
			if (inputStream.isFinished()) {
				return ResponseWrapper.buildFailureResponse(400, "Invalid JSON Format");
			}

			return profileIdService.getProfileId(inputStream);
		}
	}
}
