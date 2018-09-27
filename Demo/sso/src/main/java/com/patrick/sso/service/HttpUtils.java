package com.patrick.sso.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Patrick Pan
 *
 */
public class HttpUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger("ssoLogger");

	public static boolean isStatusCode200(int statusCode) {
		if (statusCode != 200) {
			LOGGER.warn("Status code is not 200.");
			return false;
		}

		return true;
	}

	private HttpUtils() {
	}
}
