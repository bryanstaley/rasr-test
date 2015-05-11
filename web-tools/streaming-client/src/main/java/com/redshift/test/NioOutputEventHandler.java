package com.redshift.test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpException;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpClientEventHandler;

public class NioOutputEventHandler implements NHttpClientEventHandler {

	private InputStream stream;

	public NioOutputEventHandler(InputStream stream) {
		this.stream = stream;
	}

	public void connected(NHttpClientConnection conn, Object attachment)
			throws IOException, HttpException {
		// TODO Auto-generated method stub

	}

	public void requestReady(NHttpClientConnection conn) throws IOException,
			HttpException {
		// TODO Auto-generated method stub

	}

	public void responseReceived(NHttpClientConnection conn)
			throws IOException, HttpException {
		// TODO Auto-generated method stub

	}

	public void inputReady(NHttpClientConnection conn, ContentDecoder decoder)
			throws IOException, HttpException {

	}

	public void outputReady(NHttpClientConnection conn, ContentEncoder encoder)
			throws IOException, HttpException {
	}

	public void endOfInput(NHttpClientConnection conn) throws IOException {
		// TODO Auto-generated method stub

	}

	public void timeout(NHttpClientConnection conn) throws IOException,
			HttpException {
		// TODO Auto-generated method stub

	}

	public void closed(NHttpClientConnection conn) {
		// TODO Auto-generated method stub

	}

	public void exception(NHttpClientConnection conn, Exception ex) {
		// TODO Auto-generated method stub

	}

}
