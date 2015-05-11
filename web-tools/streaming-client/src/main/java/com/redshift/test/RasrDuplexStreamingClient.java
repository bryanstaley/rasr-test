package com.redshift.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class RasrDuplexStreamingClient {

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

	public RasrDuplexStreamingClient() {

	}

	public static void main(String[] args) {

		ExecutorService executor = Executors.newFixedThreadPool(4);
		int BYTE_BUFFER = 1024 * 10;
		int DELAY_MSECS = 1; // 1/1000 of a second
		int LOOPS = 1;

		DataInputStream inputStream = null;
		String recognizeUrl = "http://localhost:8080/rasr-streaming-server/streaming/duplex?requestid=HUMPTY_DUMPTY_HAD_A_GREAT_FALL";
		RasrStreamingRequest request = new RasrStreamingRequest(
		// "http://localhost:8080/rasr-ws/rasr/control/42");
				recognizeUrl);

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

			if (cmd.hasOption(FILE_LONG_OPT)) {
				File inputFile = new File(cmd.getOptionValue(FILE_LONG_OPT));
				inputStream = new DataInputStream(
						new FileInputStream(inputFile));
				PipedOutputStream output = new PipedOutputStream();
				PipedInputStream input = new PipedInputStream(BYTE_BUFFER);
				input.connect(output);
				byte[] buffer = new byte[BYTE_BUFFER];

				// Start the streaming!
				executor.execute(new StreamingRequestRunner(request, input));
				Thread.currentThread();
				// Start the result processing
				Thread.sleep(4000); // Lame way to ensure request is sent before
									// response-request is sent
				executor.execute(new DuplexResponseHandler(recognizeUrl));

				int loop_number = 1;
				int read = 0;

				while (true) {

					// Send the bytes!
					read = inputStream.read(buffer);

					if (read == -1) // reached EOF
					{
						loop_number++;
						if (loop_number > LOOPS) {
							input.close();
							output.close();
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
