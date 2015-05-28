package com.bws.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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
public class StreamingRasrServlet extends HttpServlet implements ServletContextListener {

	private static final Class[] parameters = new Class[] { URL.class };
	private static RecognizerPool recognizerPool;
	private boolean hasUpdatedClassPath = false;
	private static URI custom;
	private String boundary = "112233445566778899001234567890";
	private boolean initialized = false;

	public StreamingRasrServlet() throws URISyntaxException, PropertyException,
			MalformedURLException {
		
		
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

		StreamingRecognizer recognizer = null;
		try {
			response.setContentLength(-1); // chunked.
			response.setHeader("Transfer-Encoding", "chunked");
			response.setHeader("Content-Type", "Multipart/mixed; boundary=\""
					+ boundary + "\"");
			recognizer = recognizerPool.checkout(custom);
			recognizer.getSource().setInputStream(request.getInputStream(), "");
			recognizer.getResultsListener().setOutput(
					response.getOutputStream());
			Result r = recognizer.recognize();
			while (r != null) {
				r = recognizer.recognize();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				recognizerPool.checkin(custom, recognizer);
			} catch (Exception e) {
				e.printStackTrace();
			}
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


	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		URL base;
		try {
			base = context.getResource("WEB-INF/baseconfig.xml");
			URL test = context.getResource("WEB-INF/test-config.xml");
			custom = context.getResource("WEB-INF/test-config.xml").toURI();
			ConfigurationManager baseConfigManager = new ConfigurationManager(
					base);

			recognizerPool = new BlockingRecognizerPool(1, baseConfigManager,
					"wordRecognizer");
			
			initialized = true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
