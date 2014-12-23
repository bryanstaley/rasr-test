package com.bws.test.formatters;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.bws.test.formatters.HtmlMultipartFormatter.MultipartFragment.Header;

import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

public class HtmlMultipartFormatter implements OutputFormatter {

	public byte[] format(String data) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] format(Map m) throws UnsupportedEncodingException {
		JSONObject jsonObj = new JSONObject(m);

		try {
			String json = buildJsonFrag("112233445566778899001234567890",
					jsonObj).toString();
			System.out.println(json);
			return json.getBytes();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new byte[0];
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

	public static class MultipartFragment {

		public static class Header {
			public String name;
			public String value;

			public Header(String inName, String inValue) {
				name = inName;
				value = inValue;
			}
		};

		private String boundry;
		private List<Header> headers;
		private Object content;

		public MultipartFragment(String inBoundry, List<Header> inHeaders,
				Object content) {
			setBoundry(inBoundry);
			setHeaders(inHeaders);
			setContent(content);
		}

		private String buildOutput() {
			StringBuilder sb = new StringBuilder();

			sb.append("--" + boundry + '\n');
			for (Header h : headers) {
				sb.append(h.name).append(": ").append(h.value).append('\n');
			}
			sb.append(content.toString());
			return sb.toString();
		}

		public int getBytesSizeOfOutput() {
			return buildOutput().length();
		}

		@Override
		public String toString() {
			return buildOutput();
		}

		public String getBoundry() {
			return boundry;
		}

		public void setBoundry(String boundry) {
			this.boundry = boundry;
		}

		public Object getContent() {
			return content;
		}

		public void setContent(Object content) {
			this.content = content;
		}

		public List<Header> getHeaders() {
			return headers;
		}

		public void setHeaders(List<Header> headers) {
			this.headers = headers;
		}

	}

	public void newProperties(PropertySheet arg0) throws PropertyException {
		// TODO Auto-generated method stub

	}

}
