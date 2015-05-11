package com.bws.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.bws.test.formatters.OutputFormatter;

import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.frontend.endpoint.SpeechMarker;
import edu.cmu.sphinx.result.ConfidenceResult;
import edu.cmu.sphinx.result.ConfidenceScorer;
import edu.cmu.sphinx.result.Path;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.props.Configurable;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4Component;

public class OutStreamResultListener implements ResultListener, Configurable {

	private OutputStream output;

	private ConfidenceScorer scorer;

	private OutputFormatter formatter;
	
	@S4Component(type = ConfidenceScorer.class)
	public final static String CONF_SCORER_SOURCE = "scorer";

	@S4Component(type = OutputFormatter.class)
	public final static String OUTPUT_FORMATTER = "outputFormatter";

	public void newProperties(PropertySheet arg0) throws PropertyException {
		setScorer((ConfidenceScorer) arg0.getComponent(CONF_SCORER_SOURCE));
		setFormatter((OutputFormatter) arg0.getComponent(OUTPUT_FORMATTER));

	}

	public void newResult(Result result) {
		ConfidenceResult cr = scorer.score(result);
		Path runningPath = cr.getBestHypothesis();

		Map<String, String> runningWords = new HashMap<String, String>();
		int i = 0;
		for (WordResult wr : runningPath.getWords()) {
			runningWords.put("Result" + i++, wr.getPronunciation().getWord()
					.toString());
		}
		try {
			output.write(formatter.format(runningWords));
			output.flush();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// for (WordResult wr : runningPath.getWords()) {
		// try {
		//
		// output.write(wr.getPronunciation().getWord().getSpelling()
		// .getBytes());
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}

	public OutputStream getOutput() {
		return output;
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}

	public ConfidenceScorer getScorer() {
		return scorer;
	}

	public void setScorer(ConfidenceScorer scorer) {
		this.scorer = scorer;
	}

	public OutputFormatter getFormatter() {
		return formatter;
	}

	public void setFormatter(OutputFormatter formatter) {
		this.formatter = formatter;
	}
}
