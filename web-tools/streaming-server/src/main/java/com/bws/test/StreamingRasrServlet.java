package com.bws.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;

@WebServlet("/streaming")
public class StreamingRasrServlet extends HttpServlet implements
		ServletContextListener {

	private static final Class[] parameters = new Class[] { URL.class };
	private static RecognizerPool recognizerPool;
	private boolean hasUpdatedClassPath = false;
	private static URI custom;
	private static final String boundary = "112233445566778899001234567890";
	private static List<String> additionalClassPaths = new ArrayList<String>();

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
			for (String s : this.additionalClassPaths) {
				method.invoke(cl, new Object[] { new URI(s).toURL() });
			}
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
			custom = context.getResource("WEB-INF/test-config.xml").toURI();
			ConfigurationManager baseConfigManager = new ConfigurationManager(
					base);

			recognizerPool = new BlockingRecognizerPool(1, baseConfigManager,
					"wordRecognizer");

			this.additionalClassPaths.clear();
			Properties properties = new Properties();
			properties.load(context
					.getResourceAsStream("/WEB-INF/server.properties"));
			String uris = properties.getProperty("server_class_path_uris", "");
			for (String s : uris.split(","))
				this.additionalClassPaths.add(s);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
