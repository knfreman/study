package com.patrick.sso.service.profileid;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.patrick.sso.ResponseWrapper;
import com.patrick.sso.common.CommonUtils;
import com.patrick.sso.profileid.ITokenProfileIdMap;

/**
 * 
 * @author Patrick Pan
 *
 */
@Service
public class ProfileIdService {

	private static final String TOKEN = "token";
	private static final Logger LOGGER = LoggerFactory.getLogger("ssoLogger");

	@Autowired
	private ITokenProfileIdMap tokenProfileMap;

	public ResponseWrapper getProfileId(InputStream inputStream) {
		String jsonString = CommonUtils.inputStreamToString(inputStream);
		LOGGER.debug("JSON String is {}.", jsonString);

		try {
			return getProfileId(new JSONObject(jsonString));
		} catch (JSONException e) {
			LOGGER.error("Exception occurs in ProfileService.getProfileId", e);
			return ResponseWrapper.buildFailureResponse(HttpStatus.BAD_REQUEST, ResponseWrapper.INVALID_JSON_FORMAT);
		}
	}

	private ResponseWrapper getProfileId(JSONObject json) {
		if (!json.has(TOKEN)) {
			return ResponseWrapper.buildFailureResponse(HttpStatus.BAD_REQUEST, ResponseWrapper.INVALID_JSON_FORMAT);
		}

		String token = json.getString(TOKEN);
		String profile = tokenProfileMap.get(token);

		if (StringUtils.isEmpty(profile)) {
			return ResponseWrapper.newInstance(HttpStatus.NO_CONTENT);
		}

		ResponseWrapper responseWrapper = ResponseWrapper.newInstance(HttpStatus.OK);
		responseWrapper.addField("profileId", profile);
		return responseWrapper;
	}
}
