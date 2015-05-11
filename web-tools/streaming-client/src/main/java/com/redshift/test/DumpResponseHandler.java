package com.redshift.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DumpResponseHandler implements AsyncResponseHandler {

	InputStream response;

	public void run() {

		InputStreamReader isr = new InputStreamReader(response);
		BufferedReader br = new BufferedReader(isr);

		String next;
		try {
			next = br.readLine();

			while (next != null) {
				System.out.println(next);
				next = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void initialize(InputStream response) {
		this.response = response;

	}

}
