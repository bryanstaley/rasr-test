package com.redshift.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sound.sampled.TargetDataLine;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class RasrStreamingRequest {

	private String uri;

	public RasrStreamingRequest(String uri) {

		this.uri = uri;

	}

	public void stream(TargetDataLine audioStream)
			throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(this.uri);
		// request.setHeader("Expect", "");
		request.setHeader("content-type", "audio/x-pcm");
		request.setEntity(new AudioStreamingHttpEntity(audioStream));

		DefaultHttpClient client = new DefaultHttpClient();
		CloseableHttpResponse response = client.execute(request);
		InputStream is = response.getEntity().getContent();

	}

	public void stream(InputStream stream) throws ClientProtocolException,
			IOException {
		HttpPost request = new HttpPost(this.uri);
		// request.setHeader("Expect", "");
		request.setHeader("content-type", "audio/x-pcm");
		request.setEntity(new InputStreamingEntity(stream));

		DefaultHttpClient client = new DefaultHttpClient();
		CloseableHttpResponse response = client.execute(request);
		InputStream is = response.getEntity().getContent();
	}

	private static class InputStreamingEntity extends AbstractHttpEntity {
		InputStream input;
		private static final int BUFFER_SIZE = 1024 * 10;

		public InputStreamingEntity(InputStream input) {
			this.input = input;
		}

		public InputStream getContent() throws IOException,
				IllegalStateException {
			return null;
		}

		public long getContentLength() {
			return -1;
		}

		public boolean isRepeatable() {
			return false;
		}

		public boolean isStreaming() {
			// TODO Auto-generated method stub
			return true;
		}

		public void writeTo(OutputStream arg0) throws IOException {

			byte[] data = new byte[BUFFER_SIZE];
			int numBytesRead = input.read(data, 0, data.length);
			while (numBytesRead != -1) {

				System.out.println("Writing bytes: " + numBytesRead);
				arg0.write(data, 0, numBytesRead);
				arg0.flush();
				numBytesRead = input.read(data, 0, data.length);
			}

		}
	}

	private static class AudioStreamingHttpEntity extends AbstractHttpEntity {
		TargetDataLine input;

		public AudioStreamingHttpEntity(TargetDataLine input) {
			this.input = input;
		}

		public InputStream getContent() throws IOException,
				IllegalStateException {
			return null;
		}

		public long getContentLength() {
			return -1;
		}

		public boolean isRepeatable() {
			return false;
		}

		public boolean isStreaming() {
			// TODO Auto-generated method stub
			return false;
		}

		public void writeTo(OutputStream arg0) throws IOException {
			int numBytesRead;
			byte[] data = new byte[input.getBufferSize() / 5];

			while (input.isOpen()) {
				numBytesRead = input.read(data, 0, data.length);
				arg0.write(data, 0, numBytesRead);
				arg0.flush();
			}

		}
	}

}
