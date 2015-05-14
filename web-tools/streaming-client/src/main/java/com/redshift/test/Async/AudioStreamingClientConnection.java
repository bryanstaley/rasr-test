package com.redshift.test.Async;

import org.apache.http.impl.nio.DefaultNHttpClientConnection;
import org.apache.http.nio.reactor.IOSession;

public class AudioStreamingClientConnection extends
		DefaultNHttpClientConnection {

	public AudioStreamingClientConnection(IOSession session, int buffersize) {
		super(session, buffersize);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void suspendOutput() {
	};

	@Override
	public void suspendInput() {
	}

}
