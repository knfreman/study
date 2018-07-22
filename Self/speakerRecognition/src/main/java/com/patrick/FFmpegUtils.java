package com.patrick;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Patrick Pan
 *
 */
public class FFmpegUtils {
	private static final String DEST = Config.getInstance().getDest();
	private static final Logger LOGGER = LogManager.getLogger("SpeakerRecognitionlogger");

	private static final String CMD_FORMAT = "ffmpeg -i %s -ar 16000 -ac 1 -acodec pcm_s16le %s.wav";

	private static final String arg0;
	private static final String arg1;

	static {
		if ("windows".equalsIgnoreCase(Config.getInstance().getOs())) {
			arg0 = "cmd.exe";
			arg1 = "/C";
		} else {
			arg0 = "/bin/sh";
			arg1 = "-c";
		}
	}

	public static synchronized boolean transcode(String filename) {
		if (DEST.isEmpty()) {
			return false;
		}

		String filePath = new StringBuilder(DEST).append(File.separatorChar).append(filename).toString();
		String cmd = String.format(CMD_FORMAT, filePath, filePath);

		LOGGER.info(cmd);

		try {
			Runtime.getRuntime().exec(new String[] { arg0, arg1, cmd }).waitFor();
			return true;
		} catch (Exception e) {
			LOGGER.error("", e);
			return false;
		}
	}

	private FFmpegUtils() {
	}
}
