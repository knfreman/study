package com.patrick;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Patrick Pan
 *
 */
public class FFmpegUtils {
	private static final String DEST = Config.getInstance().getDest();
	private static final Logger LOGGER = LoggerFactory.getLogger("speakerRecognitionLogger");

	private static final String CMD_FORMAT = "ffmpeg -i %s -ar 16000 -ac 1 -acodec pcm_s16le %s.wav";

	private static final String ARG0;
	private static final String ARG1;

	static {
		if ("windows".equalsIgnoreCase(Config.getInstance().getOs())) {
			ARG0 = "cmd.exe";
			ARG1 = "/C";
		} else {
			ARG0 = "/bin/sh";
			ARG1 = "-c";
		}
	}

	public static synchronized String transcode(String filename) {
		if (DEST.isEmpty()) {
			return "";
		}

		String filePath = new StringBuilder(DEST).append(File.separatorChar).append(filename).toString();
		String cmd = String.format(CMD_FORMAT, filePath, filePath);

		LOGGER.debug(cmd);

		try {
			Runtime.getRuntime().exec(new String[] { ARG0, ARG1, cmd }).waitFor();
			return new StringBuilder(filePath).append(".wav").toString();
		} catch (Exception e) {
			LOGGER.error("Exception occurs during using ffmpeg to transcode.", e);
			return "";
		}
	}

	private FFmpegUtils() {
	}
}
