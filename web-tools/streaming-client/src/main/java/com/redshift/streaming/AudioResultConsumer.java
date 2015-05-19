package com.redshift.streaming;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

public class AudioResultConsumer implements HttpAsyncResponseConsumer<Integer> {

	boolean isClosed = false;
	MultipartDecoder multipartDecoder = new MultipartDecoder(
			"112233445566778899001234567890");

	public void consumeContent(ContentDecoder decoder, IOControl ioctrl)
			throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(1024);
		int read = decoder.read(bb);
		bb.flip();

		String body = multipartDecoder.getNextBody(bb);

		if (body != null) {
			System.out.println("Got a body " + body);
		}

	}

	public void close() throws IOException {
		isClosed = false;
		// TODO Auto-generated method stub
		System.out.println("Consumer closed");

	}

	public boolean cancel() {
		// TODO Auto-generated method stub
		System.out.println("Consumer cancel");
		return false;
	}

	public void responseReceived(HttpResponse response) throws IOException,
			HttpException {
		// TODO Auto-generated method stub
		System.out.println("Consumer res received");

	}

	public void responseCompleted(HttpContext context) {
		// TODO Auto-generated method stub
		System.out.println("Consumer res complete");

	}

	public void failed(Exception ex) {
		// TODO Auto-generated method stub
		System.out.println("Consumer failed");

	}

	public Exception getException() {
		// TODO Auto-generated method stub
		System.out.println("Consumer get Exce");
		return null;
	}

	public Integer getResult() {
		// TODO Auto-generated method stub
		System.out.println("Consumer get res");
		return 1;
	}

	public boolean isDone() {
		// TODO Auto-generated method stub
		System.out.println("Consumer is done");
		return isClosed;
	}

}
