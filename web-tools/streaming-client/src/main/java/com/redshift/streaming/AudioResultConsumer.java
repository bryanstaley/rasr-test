package com.redshift.streaming;

import java.io.IOException;
import java.nio.ByteBuffer;


import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AudioResultConsumer implements HttpAsyncResponseConsumer<Integer> {

	boolean isClosed = false;
	MultipartDecoder multipartDecoder = new MultipartDecoder(
			"112233445566778899001234567890");

	private void processMime(String s)
	{
		//Lop off mime 
		int mimeEndIdx = s.indexOf('\n');
		if (mimeEndIdx == -1)
			return;
		String mimeType = s.substring(0, mimeEndIdx);
		String payload = s.substring(mimeEndIdx,s.length());
		
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject jsonObject = (JSONObject)parser.parse(payload);
			
			Iterator it = jsonObject.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry pair = (Map.Entry)it.next();
				
				String nextResult = pair.getValue().toString();
				
				if (!nextResult.contains("<"))
				{
					System.out.println(nextResult);
				}
				
				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void consumeContent(ContentDecoder decoder, IOControl ioctrl)
			throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(1024);
		int read = decoder.read(bb);
		bb.flip();

		String body = multipartDecoder.getNextBody(bb);
		processMime(body);

		if (body != null) {
			processMime(body);
		}

	}

	public void close() throws IOException {
		isClosed = false;
		// TODO Auto-generated method stub
		System.out.println("Consumer closed");

	}

	public boolean cancel() {
		// TODO Auto-generated method stub
		System.out.println("Consumer cancel");
		return false;
	}

	public void responseReceived(HttpResponse response) throws IOException,
			HttpException {
		// TODO Auto-generated method stub
		System.out.println("Consumer res received");

	}

	public void responseCompleted(HttpContext context) {
		// TODO Auto-generated method stub
		System.out.println("Consumer res complete");

	}

	public void failed(Exception ex) {
		// TODO Auto-generated method stub
		System.out.println("Consumer failed");

	}

	public Exception getException() {
		// TODO Auto-generated method stub
		System.out.println("Consumer get Exce");
		return null;
	}

	public Integer getResult() {
		// TODO Auto-generated method stub
		System.out.println("Consumer get res");
		return 1;
	}

	public boolean isDone() {
		// TODO Auto-generated method stub
		System.out.println("Consumer is done");
		return isClosed;
	}

}
