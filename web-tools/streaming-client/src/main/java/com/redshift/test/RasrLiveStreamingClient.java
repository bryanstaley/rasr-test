package com.redshift.test;

import static org.junit.Assert.fail;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.client.protocol.RequestExpectContinue;
import org.apache.http.impl.nio.DefaultNHttpClientConnection;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;

import com.redshift.streaming.AudioRequestProducer;
import com.redshift.streaming.AudioResultConsumer;
import com.redshift.test.Async.AudioStreamingClientConnection;
import com.redshift.test.Async.AudioStreamingEntity;
import com.redshift.test.Async.LineStreamingEntity;
import com.redshift.test.Async.ReactorRunner;

public class RasrLiveStreamingClient {

	private static Options options;

	private static CommandLineParser parser;

	private static final String URL_SHORT_OPT = "u";
	private static final String URL_LONG_OPT = "url";

	private static final String HOST_SHORT_OPT = "h";
	private static final String HOST_LONG_OPT = "host";

	private static final String PORT_SHORT_OPT = "p";
	private static final String PORT_LONG_OPT = "port";

	static {
		options = new Options();

		options.addOption(URL_SHORT_OPT, URL_LONG_OPT, true, "The context URL");
		options.addOption(HOST_SHORT_OPT, HOST_LONG_OPT, true, "The web server host");
		options.addOption(PORT_SHORT_OPT, PORT_LONG_OPT, true, "The web server port");

		parser = new BasicParser();
	}

	public RasrLiveStreamingClient() throws IOReactorException, InterruptedException {
	}

	static public TargetDataLine getAudioStream() {
		
		try {
			AudioFormat format = new AudioFormat((float) 16000, 16, 1, true, false);
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

			Mixer.Info[] mixers = AudioSystem.getMixerInfo();
			Mixer mixer = AudioSystem.getMixer(mixers[2]);
			
			TargetDataLine line = null;

			Line.Info[] targetLineInfo = mixer.getTargetLineInfo();
			System.out.println(targetLineInfo[0]);

			if (targetLineInfo.length > 0) {
				line = (TargetDataLine)AudioSystem.getLine(info);
			} else {
				fail("couldn't find a record mixer");
			}

			line.open(format);
			line.start();
			return line;
		}catch(Exception e)
		{
			System.out.println("Unable to get audio line due to " + e.getMessage());
			return null;
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException, HttpException, ExecutionException {

		String url = "/rasr-streaming-server/streaming";
		String host = "localhost";
		int port = 8080;
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		if (cmd.hasOption(URL_LONG_OPT)) {
			url = cmd.getOptionValue(URL_LONG_OPT);
		}
		if (cmd.hasOption(HOST_LONG_OPT)) {
			host = cmd.getOptionValue(HOST_LONG_OPT);
		}
		if (cmd.hasOption(PORT_LONG_OPT)) {
			port = Integer.parseInt(cmd.getOptionValue(PORT_LONG_OPT));
		}

		Executors.newSingleThreadExecutor();

		DefaultConnectingIOReactor dcior = new DefaultConnectingIOReactor();
		SessionRequest session = dcior.connect(new InetSocketAddress(host, port), null, null, null);
		Thread runner = new Thread(new ReactorRunner(dcior));
		runner.start();
		session.waitFor();

		IOSession iosession = session.getSession();
		DefaultNHttpClientConnection dnhcc = new AudioStreamingClientConnection(iosession, 2048);

		System.out.println("The io mask " + iosession.getEventMask());

		HttpProcessor processor = HttpProcessorBuilder.create().add(new RequestContent())
				.add(new RequestTargetHost()).add(new RequestConnControl())
				.add(new RequestUserAgent("MyAgent-HTTP/1.1")).add(new RequestExpectContinue()).build();

		HttpAsyncRequester requester = new HttpAsyncRequester(processor);
		BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST", url);
		request.addHeader(HTTP.CONTENT_TYPE, "audio/x-pcm");
		request.setEntity(new LineStreamingEntity(getAudioStream()));

		Future<Integer> future = requester.execute(new AudioRequestProducer(new HttpHost("127.0.0.1"), request),
				new AudioResultConsumer(), dnhcc);
		try {
			Integer result = future.get(100, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
