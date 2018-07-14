package com.patrick;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author Patrick Pan
 *
 */
public class GetOperationStatus {

	public static void main(String[] args) {
		HttpClient httpclient = HttpClients.createDefault();
		String operationId = "3c8c70bc-213f-4fa9-9bf5-d0393a25cf1f";

		try {
			URIBuilder builder = new URIBuilder(
					"https://westus.api.cognitive.microsoft.com/spid/v1.0/operations/" + operationId);

			URI uri = builder.build();
			HttpGet request = new HttpGet(uri);
			request.setHeader("Ocp-Apim-Subscription-Key", Authentication.SUBSCRIPTION_KEY);

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
