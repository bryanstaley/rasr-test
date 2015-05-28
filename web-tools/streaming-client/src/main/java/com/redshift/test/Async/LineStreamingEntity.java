package com.redshift.test.Async;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.TargetDataLine;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.HttpAsyncContentProducer;

public class LineStreamingEntity extends AbstractHttpEntity implements
		HttpAsyncContentProducer {

	private final TargetDataLine stream;
	private static final int BUFFER_SIZE = 2048;

	public LineStreamingEntity(TargetDataLine stream) {
		this.setChunked(true);
		this.stream = stream;
	}

	public boolean isRepeatable() {
		return false;
	}

	public long getContentLength() {
		return -1;
	}

	public InputStream getContent() throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	public void writeTo(OutputStream arg0) throws IOException {
		int numBytesRead;
		byte[] data = new byte[stream.getBufferSize() / 5];

		while (stream.isOpen()) {
			numBytesRead = stream.read(data, 0, data.length);
			arg0.write(data, 0, numBytesRead);
			arg0.flush();
		}
	}

	public boolean isStreaming() {
		return true;
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Leaving so soon?");
	}

	public void produceContent(ContentEncoder encoder, IOControl ioctrl)
			throws IOException {
		byte[] data = new byte[BUFFER_SIZE];
		int numBytesRead = stream.read(data, 0, data.length);

		if (numBytesRead < 0) {
			encoder.complete();
			return;
		}
		ByteBuffer nextBuffer = ByteBuffer.wrap(data);
		int wrote = encoder.write(nextBuffer);
		// System.out.println("Produced " + wrote + " bytes");

	}
}
