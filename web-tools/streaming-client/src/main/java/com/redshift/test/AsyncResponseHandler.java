package com.redshift.test;

import java.io.InputStream;

public interface AsyncResponseHandler extends Runnable {

	void initialize(InputStream response);

}
