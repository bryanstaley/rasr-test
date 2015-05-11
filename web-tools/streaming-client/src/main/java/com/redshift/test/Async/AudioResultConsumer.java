package com.redshift.test.Async;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

public class AudioResultConsumer implements HttpAsyncResponseConsumer<Integer> {

	public void consumeContent(ContentDecoder decoder, IOControl ioctrl)
			throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(40);

		int read = decoder.read(bb);

		System.out.println("Read " + read + "bytes!!");
		ioctrl.requestInput();
		ioctrl.requestOutput();

	}

	public void close() throws IOException {
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
		return false;
	}

}
