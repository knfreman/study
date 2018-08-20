package com.patrick.sso.service.face;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

/**
 * 
 * @author Patrick Pan
 *
 */
public abstract class AbstractFaceLoginService implements IFaceLoginService {

	protected static final Logger LOGGER = LoggerFactory.getLogger("ssoLogger");

	@Autowired
	protected MessageSource messageSource;

	protected boolean isLangValid(String lang) {
		if (StringUtils.isEmpty(lang)) {
			return false;
		}

		return true;
	}
}
