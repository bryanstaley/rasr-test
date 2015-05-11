package com.bws.test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bws.test.RecognizerPool.UnrecognizedRecognizer;

import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;

@WebServlet("/streaming/duplex")
public class DuplexStreamingRasrServlet extends HttpServlet {

	private static final Class[] parameters = new Class[] { URL.class };
	private static RecognizerPool recognizerPool;
	private boolean hasUpdatedClassPath = false;
	private URI lasik;
	private static Map<String, DuplexIoStream> activeStreams;

	ResultListener resultListener;

	static {
		activeStreams = new HashMap<String, DuplexIoStream>();
	}

	public DuplexStreamingRasrServlet() throws URISyntaxException,
			PropertyException, MalformedURLException {
		URI base = new URI("file:///tmp/baseconfig.xml");
		lasik = new URI("file:///tmp/test-config.xml");
		ConfigurationManager baseConfigManager = new ConfigurationManager(
				base.toURL());

		recognizerPool = new BlockingRecognizerPool(1, baseConfigManager,
				"wordRecognizer");

		resultListener = new DumpResultListener();

	}

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int MAX_RETRIES = 5;
		int retries = 0;
		// Get requestId
		String requestId = request.getParameter("requestid");
		DuplexIoStream activeOutput = activeStreams.get(requestId);
		byte[] BUFFER = new byte[2048];

		// Lookup active outputstream
		while (activeOutput == null && ++retries < MAX_RETRIES)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			activeOutput = activeStreams.get(requestId);
		}
		if (activeOutput == null) {
			response.getOutputStream().write("requestid not found!".getBytes());
			response.getOutputStream().flush();
			return;
		}

		// while there's data, read/write to request outputstream
		int read = activeOutput.pis.read(BUFFER);

		while (read >= 0) {
			response.getOutputStream().write(BUFFER, 0, read);
			response.getOutputStream().flush();
			read = activeOutput.pis.read(BUFFER);
		}
		activeStreams.remove(requestId);
		activeOutput.pis.close();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Get requestId
		String requestId = request.getParameter("requestid");

		// Get outputstream
		if (activeStreams.containsKey(requestId)) {
			// No bueno
			throw new ServletException("The request id " + requestId
					+ " is active already!");
		}

		PipedOutputStream newActiveStream = new PipedOutputStream();
		activeStreams.put(requestId, new DuplexIoStream(newActiveStream));

		if (!hasUpdatedClassPath) {
			hasUpdatedClassPath = true;
			updateClassPath((URLClassLoader) request.getSession()
					.getServletContext().getClassLoader());
		}

		StreamingRecognizer recognizer = recognizerPool.checkout(lasik);
		recognizer.getSource().setInputStream(request.getInputStream(),
				requestId);
		recognizer.getResultsListener().setOutput(newActiveStream);

		Result r = recognizer.recognize();
		while (r != null) {
			r = recognizer.recognize();
		}
		try {
			recognizerPool.checkin(lasik, recognizer);
			activeStreams.get(requestId).pos.close();
		} catch (UnrecognizedRecognizer e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void updateClassPath(URLClassLoader cl) {
		try {

			final Class<URLClassLoader> sysclass = URLClassLoader.class;
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(cl, new Object[] { new URI(
					"file:///home/bstaley/git/rasr/rasr-data/").toURL() });
			method.invoke(cl, new Object[] { new URI(
					"file:///home/bstaley/git/sphinx4-data/").toURL() });
			method.invoke(cl, new Object[] { new URI(
					"file:///home/bstaley/git/rasr/rasr-data/phonemes/")
					.toURL() });
		} catch (final Throwable t) {
			t.printStackTrace();
		}
	}

}
