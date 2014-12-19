package com.bws.test.formatters;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

public class BasicFormatter implements OutputFormatter {

	public byte[] format(String data) throws UnsupportedEncodingException {
		return data.getBytes("UTF-8");
	}

	public byte[] format(Map m) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return new byte[0];
	}

	public void newProperties(PropertySheet arg0) throws PropertyException {
		// TODO Auto-generated method stub

	}

}
