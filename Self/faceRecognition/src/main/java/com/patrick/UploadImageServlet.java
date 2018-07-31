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

	private static final String DEST = Config.getInstance().getDest();
	private static final String IMAGE_URI_PREFIX = Config.getInstance().getImageUriPrefix();

	private static final Logger LOGGER = LogManager.getLogger("FaceRecognitionlogger");

	private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2263169616776646228L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		LOGGER.info("Receive a new request.");
		if (DEST.isEmpty()) {
			LOGGER.error("Please check 'config.properties' because 'dest' is empty.");
			printResponse(resp, INTERNAL_SERVER_ERROR);
			return;
		}

		String filename = (new Date()).getTime() + ".png";
		String filePath = new StringBuilder(DEST).append(File.separatorChar).append(filename).toString();

		try (InputStream inputStream = req.getInputStream();
				OutputStream outputStream = new FileOutputStream(filePath)) {
			byte[] bytes = new byte[2048];
			int hasRead = 0;
			while ((hasRead = (inputStream.read(bytes))) > 0) {
				outputStream.write(bytes, 0, hasRead);
			}
		}

		LOGGER.debug("The image is saved to '" + filePath + "'.");

		String imageURI = new StringBuilder(IMAGE_URI_PREFIX).append(File.separatorChar).append(filename).toString();
		LOGGER.debug("imageURI is " + imageURI);
		JSONObject json = FaceRecognitionUtils.getPersonName(imageURI);

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
