package com.redshift.test.Async;

import java.io.IOException;

import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.DefaultHttpClientIODispatch;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactor;

public class ReactorRunner implements Runnable {
	IOReactor reactor;

	public ReactorRunner(IOReactor reactor) {
		this.reactor = reactor;

	}

	public void run() {
		IOEventDispatch ioEventDispatch = new DefaultHttpClientIODispatch(
				new HttpAsyncRequestExecutor(), ConnectionConfig.DEFAULT);
		try {
			System.out.println("Starting reactor");
			reactor.execute(ioEventDispatch);
			System.out.println("Finished reactor");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
