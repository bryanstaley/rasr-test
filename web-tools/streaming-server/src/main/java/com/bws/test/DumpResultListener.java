package com.bws.test;

import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

public class DumpResultListener implements ResultListener {

	public void newProperties(PropertySheet arg0) throws PropertyException {
		// TODO Auto-generated method stub

	}

	public void newResult(Result result) {
		System.out.println("BWS ===> " + result.toString());// TODO
															// Auto-generated
															// method stub

	}

}
