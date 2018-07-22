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

import org.json.JSONObject;

/**
 * 
 * @author Patrick Pan
 *
 */
@WebServlet("/identification")
public class IdentificationServlet extends HttpServlet {

	private static final String DEST = Config.getInstance().getDest();

	/**
	 * 
	 */
	private static final long serialVersionUID = 858849523449741737L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		if (DEST.isEmpty()) {
			printResponse(resp, "Internal Server Error");
			return;
		}

		String filename = (new Date()).getTime() + "";
		String filePath = new StringBuilder(DEST).append(File.separatorChar).append(filename).toString();

		try (InputStream inputStream = req.getInputStream();
				OutputStream outputStream = new FileOutputStream(filePath)) {
			byte[] bytes = new byte[2048];
			int hasRead = 0;
			while ((hasRead = (inputStream.read(bytes))) > 0) {
				outputStream.write(bytes, 0, hasRead);
			}
		}

		if (!FFmpegUtils.transcode(filename)) {
			printResponse(resp, "Internal Server Error");
			return;
		}

		String wavFilePath = new StringBuilder(filePath).append(".wav").toString();

		JSONObject json = SpeakerRecognitionUtils.getIdentification(wavFilePath);
		if (!json.has("status") || (!"succeeded".equalsIgnoreCase(json.getString("status")))) {
			printResponse(resp, "Internal Server Error");
			return;
		}

		JSONObject processingResult = json.getJSONObject("processingResult");
		String identificationProfileName = getIdentificationProfileName(
				processingResult.getString("identifiedProfileId"));

		if (identificationProfileName.isEmpty()) {
			printResponse(resp, "Sorry, I don't know who you are. Please ask Patrick to introduce you to me.");
			return;
		}

		printResponse(resp, "Hello, I think you are " + identificationProfileName + ", right?");
	}

	private void printResponse(HttpServletResponse resp, String msg) throws IOException {
		resp.setContentType("application/json");
		PrintWriter pw = resp.getWriter();
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
