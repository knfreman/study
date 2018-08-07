package com.patrick;

import java.io.FileInputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author Patrick Pan
 *
 */
public class AddFace {

	public static void main(String[] args) {
		Authentication.PersonGroup group = Authentication.PersonGroup.Colleague;
		String personGroupId = group.getPersonGroupId();

		String personName = "Bella";
		String personId = getPersonId(group, personName);

		if (personId.isEmpty()) {
			System.out.println("Person id cannot be found.");
			return;
		}

		HttpClient httpclient = HttpClients.createDefault();

		try {
			URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/face/v1.0/persongroups/"
					+ personGroupId + "/persons/" + personId + "/persistedFaces");

			// builder.setParameter("userData", "{string}");
			// builder.setParameter("targetFace", "{string}");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/octet-stream");
			request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);

			// Request body
			request.setEntity(new InputStreamEntity(new FileInputStream("images/5.png")));

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
