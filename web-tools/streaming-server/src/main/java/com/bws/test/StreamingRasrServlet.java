package com.bws.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

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

@WebServlet("/streaming")
public class StreamingRasrServlet extends HttpServlet {

	private static final Class[] parameters = new Class[] { URL.class };
	private static RecognizerPool recognizerPool;
	private boolean hasUpdatedClassPath = false;
	private URI lasik;
	ResultListener resultListener;

	public StreamingRasrServlet() throws URISyntaxException, PropertyException,
			MalformedURLException {
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
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (!hasUpdatedClassPath) {
			hasUpdatedClassPath = true;
			updateClassPath((URLClassLoader) request.getSession()
					.getServletContext().getClassLoader());
		}

		response.setContentLength(-1); //chunked.
		response.setHeader("Transfer-Encoding", "chunked");
		StreamingRecognizer recognizer = recognizerPool.checkout(lasik);
		recognizer.getSource().setInputStream(request.getInputStream(), "");
		recognizer.getResultsListener().setOutput(response.getOutputStream());
		Result r = recognizer.recognize();
		while (r != null)
		{
			r = recognizer.recognize();
		}
		try {
			recognizerPool.checkin(lasik, recognizer);
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
