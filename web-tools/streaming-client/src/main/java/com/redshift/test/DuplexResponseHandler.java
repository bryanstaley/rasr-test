package com.redshift.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class DuplexResponseHandler implements AsyncResponseHandler {

	String url;

	public DuplexResponseHandler(String url) {
		this.url = url;
	}

	public void run() {

		try {
			HttpGet getRequest = new HttpGet(this.url);
			DefaultHttpClient client = new DefaultHttpClient();
			CloseableHttpResponse response = client.execute(getRequest);

			InputStreamReader is = new InputStreamReader(response.getEntity()
					.getContent());
			BufferedReader br = new BufferedReader(is);

			String next;

			next = br.readLine();

			while (next != null) {
				System.out.println(next);
				next = br.readLine();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void initialize(InputStream response) {
		// TODO Auto-generated method stub

	}

}
