package com.patrick;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Patrick Pan
 *
 */
@WebServlet("/identification")
public class IdentificationServlet extends HttpServlet {

	private static final String DEST = Config.getInstance().getDest();
	private static final Logger LOGGER = LoggerFactory.getLogger("speakerRecognitionLogger");
	private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

	/**
	 * 
	 */
	private static final long serialVersionUID = 858849523449741737L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		LOGGER.info("Receive a new request.");
		resp.setContentType("application/json");
		if (DEST.isEmpty()) {
			printResponse(resp, 500, INTERNAL_SERVER_ERROR);
			return;
		}

		String filename = (new Date()).getTime() + "";
		String filePath = new StringBuilder(DEST).append(File.separatorChar).append(filename).toString();

		try {
			saveAudio(req, filePath);
		} catch (IOException e) {
			LOGGER.error("Exception occurs in IdentificationServlet.saveAudio.", e);
			printResponse(resp, 500, INTERNAL_SERVER_ERROR);
			return;
		}

		LOGGER.debug("The audio file is saved to '{}'.", filePath);
		LOGGER.info("Use FFmpeg to transcode.");

		String wavFilePath = FFmpegUtils.transcode(filename);

		if (wavFilePath.isEmpty()) {
			printResponse(resp, 500, INTERNAL_SERVER_ERROR);
			return;
		}

		LOGGER.debug("The audio file '{}' is transcoded to wav and new audio file is '{}'.", filePath, wavFilePath);
		LOGGER.info("Invoke Microsoft Cognitive Services Speaker Recognition API to identify.");

		try {
			buildResponse(resp, wavFilePath);
		} catch (JSONException e) {
			LOGGER.error("Exception occurs in IdentificationServlet.buildResponse.", e);
			printResponse(resp, 500, INTERNAL_SERVER_ERROR);
		}
	}

	private void buildResponse(HttpServletResponse resp, String wavFilePath) {
		JSONObject json = SpeakerRecognitionUtils.getIdentification(wavFilePath);
		if (!json.has("status") || (!"succeeded".equalsIgnoreCase(json.getString("status")))) {
			printResponse(resp, 500, INTERNAL_SERVER_ERROR);
			return;
		}

		JSONObject processingResult = json.getJSONObject("processingResult");
		String identificationProfileName = getIdentificationProfileName(
				processingResult.getString("identifiedProfileId"));

		if (identificationProfileName.isEmpty()) {
			printResponse(resp, 200, "Sorry, I don't know who you are. Please ask Patrick to introduce you to me.");
			return;
		}

		printResponse(resp, 200, new StringBuilder("Hello, I think you are ").append(identificationProfileName)
				.append(", right?").toString());
	}

	private void saveAudio(HttpServletRequest req, String filePath) throws IOException {
		try (InputStream inputStream = req.getInputStream();
				OutputStream outputStream = new FileOutputStream(filePath)) {
			byte[] bytes = new byte[2048];
			int hasRead = 0;
			while ((hasRead = (inputStream.read(bytes))) > 0) {
				outputStream.write(bytes, 0, hasRead);
			}
		}
	}

	private void printResponse(HttpServletResponse resp, int statusCode, String msg) {
		PrintWriter pw = null;

		try {
			pw = resp.getWriter();
		} catch (IOException e) {
			LOGGER.error("Exception occurs in IdentificationServlet.printResponse.", e);
			resp.setStatus(500);
			return;
		}

		resp.setStatus(statusCode);
		JSONObject json = new JSONObject();
		json.put("msg", msg);
		pw.println(json.toString());
		pw.flush();
	}

	private String getIdentificationProfileName(String identifiedProfileId) {
		Authentication.IdentificationProfile[] identificationProfiles = Authentication.IdentificationProfile.values();
		for (Authentication.IdentificationProfile identificationProfile : identificationProfiles) {
			if (identificationProfile.getProfileId().equalsIgnoreCase(identifiedProfileId)) {
				return identificationProfile.name();
			}
		}

		return "";
	}
}
