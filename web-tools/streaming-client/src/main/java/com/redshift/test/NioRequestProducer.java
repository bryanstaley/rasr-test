package com.redshift.test;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;

public class NioRequestProducer implements HttpAsyncRequestProducer {

	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	public HttpHost getTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpRequest generateRequest() throws IOException, HttpException {
		// TODO Auto-generated method stub
		return null;
	}

	public void produceContent(ContentEncoder encoder, IOControl ioctrl)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void requestCompleted(HttpContext context) {
		// TODO Auto-generated method stub

	}

	public void failed(Exception ex) {
		// TODO Auto-generated method stub

	}

	public boolean isRepeatable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetRequest() throws IOException {
		// TODO Auto-generated method stub

	}

}
