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
		RasrStreamingClient.main(args.toArray(new String[0]));
	}
}
