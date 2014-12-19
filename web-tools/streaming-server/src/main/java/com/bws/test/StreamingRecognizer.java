package com.bws.test;

import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4Component;

public class StreamingRecognizer extends Recognizer {

	private OutStreamResultListener resultsListener;
	private StreamDataSource source;
	@S4Component(type = StreamDataSource.class)
	public final static String PROP_INPUT_SOURCE = "streamDataSource";

	@S4Component(type = OutStreamResultListener.class)
	public final static String RESULT_LISTENER_SOURCE = "resultListenerSource";

	@Override
	public void newProperties(PropertySheet ps) throws PropertyException {
		// TODO Auto-generated method stub
		super.newProperties(ps);
		setSource((StreamDataSource) ps.getComponent(PROP_INPUT_SOURCE));
		setResultsListener((OutStreamResultListener) ps
				.getComponent(RESULT_LISTENER_SOURCE));
	}

	public StreamDataSource getSource() {
		return source;
	}

	private void setSource(StreamDataSource source) {
		this.source = source;
	}

	public OutStreamResultListener getResultsListener() {
		return resultsListener;
	}

	public void setResultsListener(OutStreamResultListener resultsListener) {
		this.resultsListener = resultsListener;
		if (resultsListener != null)
			this.removeResultListener(this.resultsListener);
		this.addResultListener(this.resultsListener);
	}
}
