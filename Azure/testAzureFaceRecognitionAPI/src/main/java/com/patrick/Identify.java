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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author Patrick Pan
 *
 */
public class Identify {

	public static void main(String[] args) {
		HttpClient httpclient = HttpClients.createDefault();

		String personGroupId = Authentication.PersonGroup.Family.getPersonGroupId();
		String faceId = "86375d14-5eb5-4a6a-b825-35f2b0997b85";
		JSONArray faceIds = new JSONArray();
		faceIds.put(faceId);

		try {
			URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/face/v1.0/identify");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);

			// Request body
			JSONObject json = new JSONObject();
			json.put("faceIds", faceIds);
			json.put("personGroupId", personGroupId);
			json.put("maxNumOfCandidatesReturned", 1);
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
