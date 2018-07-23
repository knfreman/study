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

	private static final Logger LOGGER = LogManager.getLogger("SpeakerRecognitionlogger");

	private String dest;
	private String os;

	private Config() {
		Properties config = new Properties();
		try {
			config.load(Config.class.getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
			LOGGER.error("Exception occurs when load 'config.properties'.", e);
		}

		dest = config.getProperty("dest", "");
		os = config.getProperty("os", "");
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public static Config getInstance() {
		return SingletonHelper.INSTANCE;
	}

	private static class SingletonHelper {
		private static final Config INSTANCE = new Config();
	}
}
