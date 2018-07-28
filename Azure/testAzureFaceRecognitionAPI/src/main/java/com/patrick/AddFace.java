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
public class AddFace {

	public static void main(String[] args) {
		Authentication.PersonGroup group = Authentication.PersonGroup.Family;
		String personGroupId = group.getPersonGroupId();

		String personName = "Patrick";
		String personId = getPersonId(group, personName);

		if (personId.isEmpty()) {
			System.out.println("Person id cannot be found.");
			return;
		}

		String url = "http://***.***.***.***/images/Patrick_2.jpg";

		HttpClient httpclient = HttpClients.createDefault();

		try {
			URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/face/v1.0/persongroups/"
					+ personGroupId + "/persons/" + personId + "/persistedFaces");

			// builder.setParameter("userData", "{string}");
			// builder.setParameter("targetFace", "{string}");

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
			System.out.println(response.getStatusLine().getStatusCode());
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				System.out.println(EntityUtils.toString(entity));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static String getPersonId(Authentication.PersonGroup group, String personName) {
		Person[] persons = group.getPersons();

		for (Person person : persons) {
			if (person.getName().equalsIgnoreCase(personName)) {
				return person.getId();
			}
		}

		return "";
	}
}
