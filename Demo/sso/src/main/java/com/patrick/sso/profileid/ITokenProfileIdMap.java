package com.patrick.sso.profileid;

/**
 * 
 * @author Patrick Pan
 *
 */
public interface ITokenProfileIdMap {

	public void put(String token, String profileId);

	public String get(String token);
}
