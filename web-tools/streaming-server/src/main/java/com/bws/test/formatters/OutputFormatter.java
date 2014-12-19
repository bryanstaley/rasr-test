package com.bws.test.formatters;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import edu.cmu.sphinx.util.props.Configurable;

public interface OutputFormatter extends Configurable {

	public byte[] format(String data) throws UnsupportedEncodingException;

	public byte[] format(Map m) throws UnsupportedEncodingException;

}
