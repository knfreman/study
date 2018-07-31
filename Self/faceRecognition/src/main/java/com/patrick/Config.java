package com.patrick;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Patrick Pan
 *
 */
public class Config {

	private static final Logger LOGGER = LogManager.getLogger("FaceRecognitionlogger");

	private String dest;
	private String imageUriPrefix;

	private Config() {
		Properties config = new Properties();
		try {
			config.load(Config.class.getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
			LOGGER.error("Exception occurs when load 'config.properties'.", e);
		}

		dest = config.getProperty("dest", "");
		imageUriPrefix = config.getProperty("image_uri_prefix", "");

		LOGGER.debug("dest is " + dest);
		LOGGER.debug("imageUriPrefix is " + imageUriPrefix);
	}

	public String getDest() {
		return dest;
	}

	public String getImageUriPrefix() {
		return imageUriPrefix;
	}

	public static Config getInstance() {
		return SingletonHelper.INSTANCE;
	}

	private static class SingletonHelper {
		private static final Config INSTANCE = new Config();
	}
}
