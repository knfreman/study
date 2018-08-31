package com.patrick.sso.profileid.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import com.patrick.sso.profileid.ITokenProfileIdMap;

/**
 * 
 * @author Patrick Pan
 *
 */
@Component
public class SimpleTokenProfileIdMap implements ITokenProfileIdMap {

	private ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

	@Override
	public String get(String token) {
		return map.get(token);
	}

	@Override
	public void put(String token, String profile) {
		map.put(token, profile);
	}
}
