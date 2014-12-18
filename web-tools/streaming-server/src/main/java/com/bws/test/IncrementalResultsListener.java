package com.bws.test;

import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

public class IncrementalResultsListener implements ResultListener {

	@Override
	public void newProperties(PropertySheet ps) throws PropertyException {
		// TODO Auto-generated method stub

	}

	@Override
	public void newResult(Result result) {
		System.out.println("BWS ===> " + result.toString());
	}

}
