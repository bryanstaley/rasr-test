package com.bws.test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.bws.test.MultipartFragment.Header;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private List<MultipartFragment> frags = new ArrayList<MultipartFragment>();
	private static final String boundary = "11223344556677889900";
	private Integer contentSize = 0;

	private String multipartMixedResponse = "HTTP/1.1 200 OK"
			+ "Date: Tue, 01 Dec 2009 23:27:30 GMT"
			+ "Vary: Accept-Encoding,User-Agent"
			+ "Content-Length: 681"
			+ "Content-Type: Multipart/mixed; boundary=\"sample_boundary\";"
			+ ""
			+ "Multipart not supported :("
			+ "--sample_boundary"
			+ "Content-Type: text/css; charset=utf-8"
			+ "Content-Location: http://localhost:2080/file.css"
			+ ""
			+ "body"
			+ "{"
			+ " background-color: yellow;"
			+ "}"
			+ "--sample_boundary"
			+ "Content-Type: application/x-javascript; charset=utf-8"
			+ "Content-Location: http://localhost:2080/file.js"
			+ ""
			+ "alert(\"Hello from a javascript!!!\");"
			+ ""
			+ "--sample_boundary"
			+ "Content-Type: text/html; charset=utf-8"
			+ "Content-Base: http://localhost:2080/"
			+ ""
			+ "<html>"
			+ "<head>"
			+ "    <link rel=\"stylesheet\" href=\"http://localhost:2080/file.css\">"
			+ "</head>" + "<body>" + " Hello from a html"
			+ "    <script type=\"text/javascript\""
			+ "src=\"http://localhost:2080/file.js\"></script>" + "</body>"
			+ "</html>" + "--sample_boundary--";

	private String multipartMixedResponse2 = "Multipart not supported :("
			+ "--sample_boundary"
			+ "Content-Type: text/css; charset=utf-8"
			+ "Content-Location: http://localhost:2080/file.css"
			+ ""
			+ "body"
			+ "{"
			+ " background-color: yellow;"
			+ "}"
			+ "--sample_boundary"
			+ "Content-Type: application/x-javascript; charset=utf-8"
			+ "Content-Location: http://localhost:2080/file.js"
			+ ""
			+ "alert(\"Hello from a javascript!!!\");"
			+ ""
			+ "--sample_boundary"
			+ "Content-Type: text/html; charset=utf-8"
			+ "Content-Base: http://localhost:2080/"
			+ ""
			+ "<html>"
			+ "<head>"
			+ "    <link rel=\"stylesheet\" href=\"http://localhost:2080/file.css\">"
			+ "</head>" + "<body>" + " Hello from a html"
			+ "    <script type=\"text/javascript\""
			+ "src=\"http://localhost:2080/file.js\"></script>" + "</body>"
			+ "</html>" + "--sample_boundary--";

	/**
	 * Default constructor.
	 */
	public TestServlet() {

	}

	private MultipartFragment buildJsonFrag(String boundary, JSONObject jsonVals)
			throws JSONException {
		MultipartFragment.Header header = new Header("Content-Type",
				"application/json");
		List<Header> headers = new ArrayList<MultipartFragment.Header>();
		headers.add(header);

		StringWriter out = new StringWriter();
		jsonVals.write(out);

		return new MultipartFragment(boundary, headers, out.toString());

	}

	private Integer getContentSize(List<MultipartFragment> frags) {
		int size = 0;

		for (MultipartFragment mpf : frags) {
			size += mpf.getBytesSizeOfOutput();
		}
		return size;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Map<String, String[]> queryParms = request.getParameterMap();
		List<MultipartFragment> frags = new ArrayList<MultipartFragment>();
		StringBuilder sb = new StringBuilder();

		if (queryParms.containsKey("separate")) {
			for (Entry<String, String[]> entry : queryParms.entrySet()) {
				JSONObject obj = new JSONObject(queryParms);
				Map<String, String[]> next = new HashMap();
				next.put(entry.getKey(), entry.getValue());
				try {
					frags.add(buildJsonFrag(boundary, new JSONObject(next)));
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} else {
			JSONObject obj = new JSONObject(queryParms);
			try {
				frags.add(buildJsonFrag(boundary, obj));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		sb.append("\n");

		for (MultipartFragment mpf : frags) {

			sb.append(mpf.toString());
			sb.append("\n");
		}

		String closingBoundary = "--" + boundary + "--";
		sb.append(closingBoundary);

		response.setHeader("Content-Type", "Multipart/mixed; boundary=\""
				+ boundary + "\"");
		// response.setHeader("Content-Length",
		// Integer.toString(sb.toString().getBytes().length));

		response.getOutputStream().write(sb.toString().getBytes());
		response.getOutputStream().close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, String[]> queryParms = request.getParameterMap();
		List<MultipartFragment> frags = new ArrayList<MultipartFragment>();
		StringBuilder sb = new StringBuilder();

		if (queryParms.containsKey("separate")) {
			for (Entry<String, String[]> entry : queryParms.entrySet()) {
				JSONObject obj = new JSONObject(queryParms);
				Map<String, String[]> next = new HashMap();
				next.put(entry.getKey(), entry.getValue());
				try {
					frags.add(buildJsonFrag(boundary, new JSONObject(next)));
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} else {
			JSONObject obj = new JSONObject(queryParms);
			try {
				frags.add(buildJsonFrag(boundary, obj));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		sb.append("\n");

		for (MultipartFragment mpf : frags) {

			sb.append(mpf.toString());
			sb.append("\n");
		}

		String closingBoundary = "--" + boundary + "--";
		sb.append(closingBoundary);

		response.setHeader("Content-Type", "Multipart/mixed; boundary=\""
				+ boundary + "\"");
		// response.setHeader("Content-Length",
		// Integer.toString(sb.toString().getBytes().length));

		response.getOutputStream().write(sb.toString().getBytes());
		response.getOutputStream().close();

	}

}
