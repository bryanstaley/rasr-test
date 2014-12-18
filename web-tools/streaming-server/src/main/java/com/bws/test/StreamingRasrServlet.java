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

import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;

@WebServlet("/streaming")
public class StreamingRasrServlet extends HttpServlet {

	private Recognizer recognizer;
	private ConfigurationManager config;
	private static final Class[] parameters = new Class[] { URL.class };

	public StreamingRasrServlet() throws URISyntaxException, PropertyException,
			MalformedURLException {
		URI base = new URI(
				"file:///home/bstaley/git/rasr/rasr-ws/src/main/webapp/config/baseconfig.xml");
		URI lasik = new URI(
				"file:///home/bstaley/git/rasr/rasr-ws/src/main/webapp/config/lasik-config.xml");
		ConfigurationManager baseConfigManager = new ConfigurationManager(
				base.toURL());

		config = new TwoTierConfigurationManager(baseConfigManager,
				lasik.toURL());

	}

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		StreamDataSource source;
		try {
			final URLClassLoader sysloader = (URLClassLoader) this
					.getServletContext().getClassLoader();
			final Class<URLClassLoader> sysclass = URLClassLoader.class;
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { new URI(
					"file:///home/bstaley/git/rasr/rasr-data/").toURL() });
			method.invoke(sysloader, new Object[] { new URI(
					"file:///home/bstaley/git/sphinx4-data/").toURL() });
			method.invoke(sysloader, new Object[] { new URI(
					"file:///home/bstaley/git/rasr/rasr-data/phonemes/")
					.toURL() });
		} catch (final Throwable t) {
			t.printStackTrace();
		}
		recognizer = (Recognizer) config.lookup("wordRecognizer");
		recognizer.allocate();
		recognizer.addResultListener(new IncrementalResultsListener());
		source = (StreamDataSource) config.lookup("streamDataSource");

		source.setInputStream(request.getInputStream(), "");
		Result r = recognizer.recognize();
	}

}
