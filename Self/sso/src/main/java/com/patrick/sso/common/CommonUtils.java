package com.patrick.sso.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author Patrick Pan
 *
 */
public class CommonUtils {

	private CommonUtils() {
	}

	public static Map<String, String> parseQueryString(String queryString) {
		Map<String, String> map = new HashMap<>();

		if (queryString == null) {
			return map;
		}

		String[] keysAndValues = queryString.split("&");
		for (String keyAndValue : keysAndValues) {
			String[] array = keyAndValue.split("=");
			if (array.length != 2) {
				continue;
			}

			map.put(array[0], array[1]);
		}

		return map;
	}

	public static String inputStreamToString(InputStream inputStream) throws IOException {
		return new BufferedReader(new InputStreamReader(inputStream)).lines()
				.collect(Collectors.joining(System.lineSeparator()));
	}
}
