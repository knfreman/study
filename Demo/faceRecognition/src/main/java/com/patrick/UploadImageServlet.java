package com.patrick;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Patrick Pan
 *
 */
@WebServlet("/upload")
public class UploadImageServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger("faceRecognitionlogger");

	/**
	 * 
	 */
	private static final long serialVersionUID = 2263169616776646228L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		LOGGER.info("Receive a new request.");
		resp.setContentType("application/json");

		try {
			identifyByFace(req, resp);
		} catch (IOException e) {
			LOGGER.error("Exception occurs in UploadImageServlet.identifyByFace.", e);
			resp.setStatus(500);
			printResponse(resp, FaceRecognitionUtils.INTERNAL_SERVER_ERROR);
		}
	}

	private void identifyByFace(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		JSONObject json = null;

		try (InputStream inputStream = req.getInputStream()) {
			json = FaceRecognitionUtils.getPersonName(inputStream);
		}

		if (json == null) {
			LOGGER.warn("JSONObject is null.");
			json = new JSONObject();
			json.put("msg", FaceRecognitionUtils.INTERNAL_SERVER_ERROR);
			resp.setStatus(500);
			return;
		}

		if (json.getBoolean("isSuccess")) {
			printResponse(resp, new StringBuilder("Hello, I think you are ").append(json.getString("msg"))
					.append(", right?").toString());
		} else {
			String msg = json.getString("msg");

			if (msg.contentEquals(FaceRecognitionUtils.INTERNAL_SERVER_ERROR)) {
				resp.setStatus(500);
			}

			printResponse(resp, msg);
		}
	}

	private void printResponse(HttpServletResponse resp, String msg) {
		PrintWriter pw = null;
		try {
			pw = resp.getWriter();
		} catch (IOException e) {
			LOGGER.error("Exception occurs in UploadImageServlet.printResponse.", e);
			return;
		}

		JSONObject json = new JSONObject();
		json.put("msg", msg);
		pw.println(json.toString());
		pw.flush();
	}
}
