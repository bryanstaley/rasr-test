package com.redshift.test.Async;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.HttpAsyncContentProducer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

public class AudioRequestProducer implements HttpAsyncRequestProducer {

	private final HttpHost target;
	private final HttpEntityEnclosingRequest request;

	public AudioRequestProducer(final HttpHost target,
			final HttpEntityEnclosingRequest request) {
		Args.notNull(target, "HTTP host");
		Args.notNull(request, "HTTP request");
		this.target = target;
		this.request = request;
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Producer closed");

	}

	public HttpHost getTarget() {
		// TODO Auto-generated method stub
		return this.target;
	}

	public HttpRequest generateRequest() throws IOException, HttpException {
		// TODO Auto-generated method stub
		return this.request;
	}

	public void produceContent(ContentEncoder encoder, IOControl ioctrl)
			throws IOException {
		((HttpAsyncContentProducer) this.request.getEntity()).produceContent(
				encoder, ioctrl);

		System.out.println("Producer content");
	}

	public void requestCompleted(HttpContext context) {
		// TODO Auto-generated method stub
		System.out.println("Producer request complete");

	}

	public void failed(Exception ex) {
		// TODO Auto-generated method stub
		System.out.println("Producer fail");

	}

	public boolean isRepeatable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetRequest() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Producer reset");

	}

}
