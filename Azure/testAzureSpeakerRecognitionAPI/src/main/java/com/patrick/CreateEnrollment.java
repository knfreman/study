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
public class CreateEnrollment {

	public static void main(String[] args) {
		HttpClient httpclient = HttpClients.createDefault();
		String identificationProfileId = Authentication.IdentificationProfile.Mavis.getProfileId();

		try {
			URIBuilder builder = new URIBuilder(
					"https://westus.api.cognitive.microsoft.com/spid/v1.0/identificationProfiles/"
							+ identificationProfileId + "/enroll");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/octet-stream");
			request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);

			request.setEntity(new InputStreamEntity(new FileInputStream("audio/Mavis_9.wav")));

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
			e.printStackTrace();
		}
	}

}
