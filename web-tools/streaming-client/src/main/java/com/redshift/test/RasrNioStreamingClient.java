package com.redshift.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;

import com.redshift.test.Async.AudioRequestProducer;
import com.redshift.test.Async.AudioResultConsumer;
import com.redshift.test.Async.AudioStreamingEntity;
import com.redshift.test.Async.ReactorRunner;

public class RasrNioStreamingClient {

	private static Options options;

	private static CommandLineParser parser;

	private static final String FILE_SHORT_OPT = "f";
	private static final String FILE_LONG_OPT = "file";

	private static final String DELAY_SHORT_OPT = "d";
	private static final String DELAY_LONG_OPT = "delay-msecs";

	private static final String BYTES_SHORT_OPT = "b";
	private static final String BYTES_LONG_OPT = "bytes-len";

	private static final String LOOPS_SHORT_OPT = "l";
	private static final String LOOPS_LONG_OPT = "loop-file";

	private static final String MARK_SHORT_OPT = "m";
	private static final String MARK_LONG_OPT = "mark-file";

	static {
		options = new Options();

		options.addOption(FILE_SHORT_OPT, FILE_LONG_OPT, true,
				"The file to stream")
				.addOption(DELAY_SHORT_OPT, DELAY_LONG_OPT, true,
						"delay in microsecods")
				.addOption(BYTES_SHORT_OPT, BYTES_LONG_OPT, true,
						"bytes between delay")
				.addOption(LOOPS_SHORT_OPT, LOOPS_LONG_OPT, true,
						"bytes between delay")
				.addOption(MARK_SHORT_OPT, MARK_LONG_OPT, true,
						"bytes between delay");

		parser = new BasicParser();
	}

	public RasrNioStreamingClient() throws IOReactorException,
			InterruptedException {

		// NByteArrayEntity nbae = new NByteArrayEntity(null,
		// ContentType.create("audio/pcm"));
		// nbae.produceContent(null, session.get);
	}

	public static void main(String[] args) throws InterruptedException,
			IOException, HttpException, ExecutionException {

		int BYTE_BUFFER = 1024 * 10;
		int DELAY_MSECS = 1; // 1/1000 of a second
		int LOOPS = 1;
		int MARK_SAVE_BYTES = 1000000;
		ExecutorService executor = Executors.newSingleThreadExecutor();
		String url = "/rasr-streaming-server/streaming";

		DefaultConnectingIOReactor dcior = new DefaultConnectingIOReactor();
		SessionRequest session = dcior.connect(new InetSocketAddress(
				"127.0.0.1", 8080), null, null, null);
		Thread runner = new Thread(new ReactorRunner(dcior));
		runner.start();
		session.waitFor();

		// DefaultHttpAsyncClient dhac = new DefaultHttpAsyncClient();
		// dhac.start();
		DataInputStream inputStream = null;
		IOSession iosession = session.getSession();
		DefaultNHttpClientConnection dnhcc = new DefaultNHttpClientConnection(
				iosession, 2048);

		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption(BYTES_LONG_OPT)) {
				BYTE_BUFFER = Integer.parseInt(cmd
						.getOptionValue(BYTES_LONG_OPT));
			}

			if (cmd.hasOption(LOOPS_LONG_OPT)) {
				System.out.print(cmd.getOptionValue(LOOPS_LONG_OPT));
				LOOPS = Integer.parseInt(cmd.getOptionValue(LOOPS_LONG_OPT));
			}
			if (cmd.hasOption(DELAY_LONG_OPT)) {
				DELAY_MSECS = Integer.parseInt(cmd
						.getOptionValue(DELAY_LONG_OPT));
			}
			if (cmd.hasOption(MARK_LONG_OPT)) {
				MARK_SAVE_BYTES = Integer.parseInt(cmd
						.getOptionValue(MARK_LONG_OPT));
			}

			if (cmd.hasOption(FILE_LONG_OPT)) {
				File inputFile = new File(cmd.getOptionValue(FILE_LONG_OPT));
				inputStream = new DataInputStream(
						new FileInputStream(inputFile));
				byte[] buffer = new byte[BYTE_BUFFER];
				PipedOutputStream output = new PipedOutputStream();
				PipedInputStream input = new PipedInputStream(BYTE_BUFFER);
				input.connect(output);

				HttpProcessor processor = HttpProcessorBuilder.create()
						.add(new RequestContent()).add(new RequestTargetHost())
						.add(new RequestConnControl())
						.add(new RequestUserAgent("MyAgent-HTTP/1.1"))
						.add(new RequestExpectContinue()).build();

				HttpAsyncRequester requester = new HttpAsyncRequester(processor);
				BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
						"POST", url);
				request.setEntity(new AudioStreamingEntity(input));

				Future<Integer> future = requester.execute(
						new AudioRequestProducer(new HttpHost("127.0.0.1"),
								request), new AudioResultConsumer(), dnhcc);

				int loop_number = 1;
				int read = 0;

				while (true) {

					// Send the bytes!
					read = inputStream.read(buffer);

					if (read == -1) // reached EOF
					{
						loop_number++;
						if (loop_number > LOOPS) {
							break;
						}
						inputStream = new DataInputStream(new FileInputStream(
								inputFile));

						read = inputStream.read(buffer);
					}
					System.out.println("Writing bytes:" + read);
					output.write(buffer, 0, read);
					Thread.sleep(DELAY_MSECS);
				}
				System.out.println("Done with test");
				executor.awaitTermination(10, TimeUnit.SECONDS);

				output.close();
				input.close();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
		}

	}
}
