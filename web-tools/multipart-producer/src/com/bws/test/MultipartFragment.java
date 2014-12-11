package com.bws.test;

import java.util.List;

public class MultipartFragment {

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
