package com.redshift.streaming;

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
	boolean isClosed = false;

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
		isClosed = true;
		System.out.println("Producer closed");
		((HttpAsyncContentProducer) this.request.getEntity()).close();

	}

	public HttpHost getTarget() {
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
	}

	public void requestCompleted(HttpContext context) {
		System.out.println("Producer request complete");

	}

	public void failed(Exception ex) {
		// TODO Auto-generated method stub
		System.out.println("Producer fail");
		try {
			((HttpAsyncContentProducer) this.request.getEntity()).close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean isRepeatable() {
		return false;
	}

	public void resetRequest() throws IOException {
		System.out.println("Producer reset");

	}

}
