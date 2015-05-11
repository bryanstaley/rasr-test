package com.redshift.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;

public class NioResponseHandler implements AsyncResponseHandler {

	HttpResponse response;

	public NioResponseHandler(HttpResponse response) {
		this.response = response;
	}

	public void run() {

		try {

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
