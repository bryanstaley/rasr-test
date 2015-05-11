package com.redshift.test;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

public class NioAsynchResponseConsumer extends
		AbstractAsyncResponseConsumer<HttpResponse> {

	@Override
	protected void onResponseReceived(HttpResponse response)
			throws HttpException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onContentReceived(ContentDecoder decoder, IOControl ioctrl)
			throws IOException {
		// TODO Auto-generated method stub
		ByteBuffer buf = ByteBuffer.allocate(1024);
		decoder.read(buf);
		byte[] yetAntherBuffer = new byte[1024];
		buf.get(yetAntherBuffer);
		String s = new String(yetAntherBuffer, "UTF-8");
		System.out.print(s);

	}

	@Override
	protected void onEntityEnclosed(HttpEntity entity, ContentType contentType)
			throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected HttpResponse buildResult(HttpContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void releaseResources() {
		// TODO Auto-generated method stub

	}

}
