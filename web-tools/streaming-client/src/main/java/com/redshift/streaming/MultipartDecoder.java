package com.redshift.streaming;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class MultipartDecoder {

	String boundary;
	StringBuffer message = new StringBuffer();

	public MultipartDecoder(String boundary) {
		this.boundary = "--" + boundary;
	}

	public String getNextBody(ByteBuffer moreBytes)
			throws UnsupportedEncodingException {
		// Drain bytes to end of
		int len = moreBytes.remaining();
		byte buff[] = new byte[len];
		moreBytes.get(buff);
		String payload = new String(buff, "UTF-8");
		if (payload != null)
			message = message.append(payload);

		if (message.length() < 2)
			return null;

		int firstBoundary = message.indexOf(this.boundary);

		if (firstBoundary < 0) {
			// Clear message
			message = new StringBuffer();
			return null;
		}

		// Lop off first boundary
		message = message.delete(0, firstBoundary + boundary.length());
		int secondBoundary = message.indexOf(this.boundary);

		if (secondBoundary < 0) {
			// Add back first boundary
			message.insert(0, this.boundary);
			return null;
		}
		char characters[] = new char[secondBoundary];
		message.getChars(0, secondBoundary, characters, 0);
		message.delete(0, secondBoundary);

		return new String(characters);
	}
}
