package streamingclient;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.redshift.test.RasrStreamingClient;

public class RasrStreamingClientTest {

	@Test
	public void test() {
		List<String> args = new ArrayList();
		args.add("-f");
		args.add("/home/bstaley/git/rasr/rasr-sr/test/A4.wav");
		args.add("-l");
		args.add("5");
		args.add("-d");
		args.add("250");
		RasrStreamingClient.main(args.toArray(new String[0]));
	}
}
