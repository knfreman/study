package com.patrick;

import java.io.FileInputStream;
import java.net.URI;

import org.apache.http.Header;
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
public class Identification {

	public static void main(String[] args) {
		HttpClient httpclient = HttpClients.createDefault();
		String identificationProfileIds = buildIdentificationProfileIds();
		System.out.println("identificationProfileIds: " + identificationProfileIds);

		try {
			URIBuilder builder = new URIBuilder(new StringBuilder(
					"https://westus.api.cognitive.microsoft.com/spid/v1.0/identify?identificationProfileIds=")
							.append(identificationProfileIds).append("&shortAudio=").append(true).toString());

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/octet-stream");
			request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);

			request.setEntity(new InputStreamEntity(new FileInputStream("audio/Anonymous_4.wav")));

			HttpResponse response = httpclient.execute(request);
			System.out.println(response.getStatusLine().getStatusCode());

			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				System.out.println(header.getName() + ":" + header.getValue());
			}

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				System.out.println(EntityUtils.toString(entity));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static String buildIdentificationProfileIds() {
		StringBuilder identificationProfileIds = new StringBuilder();

		Authentication.IdentificationProfile[] identificationProfile = Authentication.IdentificationProfile.values();

		String delimiter = "";
		for (int i = 0; i < identificationProfile.length; i++) {
			identificationProfileIds.append(delimiter).append(identificationProfile[i].getProfileId());
			delimiter = ",";
		}

		return identificationProfileIds.toString();
	}
}
