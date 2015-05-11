package com.bws.test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class DuplexIoStream {
	
	public PipedInputStream pis;
	public PipedOutputStream pos;
	
	public DuplexIoStream(PipedOutputStream out) throws IOException
	{
		pos = out;
		pis = new PipedInputStream(out);
	}
	
	public DuplexIoStream(PipedInputStream in) throws IOException
	{
		pis = in;
		pos = new PipedOutputStream(in);
	}


}
