package com.patrick;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * 
 * @author Patrick Pan
 *
 */
public class Detect {

	public static void main(String[] args) {
		HttpClient httpclient = HttpClients.createDefault();
		String url = "http://***.***.***.***/images/Patrick_4.jpg";

		try {
			URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/face/v1.0/detect");

			// builder.setParameter("returnFaceId", "true");
			// builder.setParameter("returnFaceLandmarks", "false");
			// builder.setParameter("returnFaceAttributes", "{string}");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);

			// Request body
			JSONObject json = new JSONObject();
			json.put("url", url);
			StringEntity reqEntity = new StringEntity(json.toString());
			request.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				System.out.println(EntityUtils.toString(entity));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
