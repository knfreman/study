package com.patrick;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
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
public class CreatePersonGroup {

	public static void main(String[] args) {
		Authentication.PersonGroup group = Authentication.PersonGroup.Colleague;
		String personGroupId = group.getPersonGroupId();
		HttpClient httpclient = HttpClients.createDefault();

		try {
			URIBuilder builder = new URIBuilder(
					"https://westus.api.cognitive.microsoft.com/face/v1.0/persongroups/" + personGroupId);

			URI uri = builder.build();
			HttpPut request = new HttpPut(uri);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);

			JSONObject json = new JSONObject();
			json.put("name", group.name());
			// Request body
			StringEntity reqEntity = new StringEntity(json.toString());
			request.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(request);
			System.out.println(response.getStatusLine().getStatusCode());
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				System.out.println(EntityUtils.toString(entity));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
