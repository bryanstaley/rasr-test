package streamingclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpException;
import org.junit.Test;

import com.redshift.test.RasrNioStreamingClient;

public class RasrNioStreamingClientTest {

	@Test
	public void test() {
		List<String> args = new ArrayList();
		args.add("-f");
		args.add("/home/bstaley/git/sphinx4/src/apps/edu/cmu/sphinx/demo/lattice/10001-90210-01803.wav");
		args.add("-l");
		args.add("10");
		args.add("-d");
		args.add("1000");
		try {
			RasrNioStreamingClient.main(args.toArray(new String[0]));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
