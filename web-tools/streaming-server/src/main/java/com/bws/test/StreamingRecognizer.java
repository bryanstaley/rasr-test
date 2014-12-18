package com.bws.test;

import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4Component;

public class StreamingRecognizer extends Recognizer {

	private StreamDataSource source;
	@S4Component(type = StreamDataSource.class)
	public final static String PROP_INPUT_SOURCE = "streamDataSource";

	@Override
	public void newProperties(PropertySheet ps) throws PropertyException {
		// TODO Auto-generated method stub
		super.newProperties(ps);
		setSource((StreamDataSource) ps.getComponent(PROP_INPUT_SOURCE));
	}

	public StreamDataSource getSource() {
		return source;
	}

	private void setSource(StreamDataSource source) {
		this.source = source;
	}
}
