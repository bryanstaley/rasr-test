package streamingclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpException;
import org.junit.Test;

import com.redshift.test.RasrLiveStreamingClient;
import com.redshift.test.RasrNioStreamingClient;

public class RasrLiveStreamingClientTest {

	@Test
	public void test() {
		List<String> args = new ArrayList();
		args.add("-u");
		args.add("/rasr-streaming-server/stream");
		args.add("-h");
		args.add("192.168.1.104");
		args.add("-p");
		args.add("8080");
		try {
			RasrLiveStreamingClient.main(args.toArray(new String[0]));
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
