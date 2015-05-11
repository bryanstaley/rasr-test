package com.redshift.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.HttpAsyncContentProducer;

public class NioContentProducer implements HttpAsyncContentProducer, HttpEntity {

	private InputStream stream;

	public NioContentProducer(InputStream stream) {
		this.stream = stream;
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	public void produceContent(ContentEncoder encoder, IOControl ioctrl)
			throws IOException {
		byte[] buff = new byte[2048];
		int read = stream.read(buff, 0, 2048);
		ByteBuffer bb = ByteBuffer.wrap(buff, 0, read);
		int wrote = encoder.write(bb);

	}

	public boolean isRepeatable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isChunked() {
		// TODO Auto-generated method stub
		return false;
	}

	public long getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Header getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Header getContentEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getContent() throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	public void writeTo(OutputStream outstream) throws IOException {
		// TODO Auto-generated method stub

	}

	public boolean isStreaming() {
		// TODO Auto-generated method stub
		return true;
	}

	public void consumeContent() throws IOException {
		// TODO Auto-generated method stub

	}

}
