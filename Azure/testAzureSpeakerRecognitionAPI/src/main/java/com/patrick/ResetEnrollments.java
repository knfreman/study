package com.patrick;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author Patrick Pan
 *
 */
public class ResetEnrollments {

	public static void main(String[] args) {
		HttpClient httpclient = HttpClients.createDefault();
		String identificationProfileId = Authentication.IdentificationProfile.Patrick.getProfileId();

		try {
			URIBuilder builder = new URIBuilder(
					"https://westus.api.cognitive.microsoft.com/spid/v1.0/identificationProfiles/"
							+ identificationProfileId + "/reset");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);

			HttpResponse response = httpclient.execute(request);
			System.out.println("StatusCode:" + response.getStatusLine().getStatusCode());

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				System.out.println(EntityUtils.toString(entity));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
