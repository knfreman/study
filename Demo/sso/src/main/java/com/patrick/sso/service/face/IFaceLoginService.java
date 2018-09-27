package com.patrick.sso.service.face;

import java.io.InputStream;

import com.patrick.sso.ResponseWrapper;

/**
 * 
 * @author Patrick Pan
 *
 */
public interface IFaceLoginService {

	public ResponseWrapper login(InputStream image, String lang);
}
