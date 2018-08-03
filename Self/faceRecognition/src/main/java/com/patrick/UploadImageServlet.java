package com.patrick;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * 
 * @author Patrick Pan
 *
 */
@WebServlet("/upload")
public class UploadImageServlet extends HttpServlet {

	private static final Logger LOGGER = LogManager.getLogger("FaceRecognitionlogger");

	private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2263169616776646228L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		LOGGER.info("Receive a new request.");

		JSONObject json = null;

		try (InputStream inputStream = req.getInputStream()) {
			json = FaceRecognitionUtils.getPersonName(inputStream);
		}

		if (json == null) {
			LOGGER.warn("JSONObject is null.");
			json = new JSONObject();
			json.put("msg", INTERNAL_SERVER_ERROR);
		}

		if (json.getBoolean("isSuccess")) {
			printResponse(resp, "Hello, I think you are " + json.getString("msg") + ", right?");
		} else {
			printResponse(resp, json.getString("msg"));
		}
	}

	private void printResponse(HttpServletResponse resp, String msg) throws IOException {
		resp.setContentType("application/json");
		PrintWriter pw = resp.getWriter();
		JSONObject json = new JSONObject();
		json.put("msg", msg);
		pw.println(json.toString());
		pw.flush();
	}
}
