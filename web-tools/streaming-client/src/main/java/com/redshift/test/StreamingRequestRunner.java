package com.redshift.test;

import java.io.InputStream;

public class StreamingRequestRunner implements Runnable {

	private RasrStreamingRequest request;
	private InputStream input;

	public StreamingRequestRunner(RasrStreamingRequest request,
			InputStream input) {
		this.request = request;
		this.input = input;

	}

	public void run() {

		try {
			request.stream(input);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
