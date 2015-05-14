package com.redshift.test;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpClientEventHandler;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.HttpAsyncClientExchangeHandler;

public class NioOutputEventHandler implements NHttpClientEventHandler {

	public static final String HTTP_HANDLER = "http.nio.exchange-handler";
	public static final String HTTP_EXCHANGE_STATE = "http.nio.http-exchange-state";

	public NioOutputEventHandler() {
	}

	public void connected(NHttpClientConnection conn, Object attachment)
			throws IOException, HttpException {
		System.out.println("Client Event Handler - connected");

	}

	public void requestReady(NHttpClientConnection conn) throws IOException,
			HttpException {

		System.out.println("Client Event Handler - requestReady");
		HttpAsyncClientExchangeHandler handler = getHandler(conn);
		final HttpRequest request = handler.generateRequest();
		conn.submitRequest(request);
		handler.requestCompleted();

	}

	public void responseReceived(NHttpClientConnection conn)
			throws IOException, HttpException {
		System.out.println("Client Event Handler - responseReceived");

	}

	public void inputReady(NHttpClientConnection conn, ContentDecoder decoder)
			throws IOException, HttpException {
		System.out.println("Client Event Handler - inputReady");

		final HttpAsyncClientExchangeHandler handler = getHandler(conn);
		handler.consumeContent(decoder, conn);

	}

	public void outputReady(NHttpClientConnection conn, ContentEncoder encoder)
			throws IOException, HttpException {
		// System.out.println("Client Event Handler - outputReady");

		final HttpAsyncClientExchangeHandler handler = getHandler(conn);
		handler.produceContent(encoder, conn);
	}

	public void endOfInput(NHttpClientConnection conn) throws IOException {
		System.out.println("Client Event Handler - endOfInput");

	}

	public void timeout(NHttpClientConnection conn) throws IOException,
			HttpException {
		System.out.println("Client Event Handler - timeout");

	}

	public void closed(NHttpClientConnection conn) {
		System.out.println("Client Event Handler - closed");

	}

	public void exception(NHttpClientConnection conn, Exception ex) {
		System.out.println("Client Event Handler - exception");

	}

	private HttpAsyncClientExchangeHandler getHandler(final NHttpConnection conn) {
		return (HttpAsyncClientExchangeHandler) conn.getContext().getAttribute(
				HTTP_HANDLER);
	}

}
