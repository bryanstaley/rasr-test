package streamingclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpHost;
import org.apache.http.impl.nio.DefaultNHttpClientConnection;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.junit.Test;

import com.redshift.streaming.AudioRequestProducer;
import com.redshift.streaming.AudioResultConsumer;
import com.redshift.test.Async.AudioStreamingEntity;
import com.redshift.test.Async.ReactorRunner;

public class HttpAsyncRequestorTest {

	@Test
	public void test() {

		try {
			ConnectingIOReactor reactor = new DefaultConnectingIOReactor();
			SessionRequest sessionRequest = reactor.connect(
					new InetSocketAddress("www.google.com", 80), null, null,
					null);

			Thread runner = new Thread(new ReactorRunner(reactor));
			runner.start();

			sessionRequest.setConnectTimeout(100);
			sessionRequest.waitFor();

			if (sessionRequest.getException() != null) {
				// TODO
			}

			IOSession session = sessionRequest.getSession();
			NHttpClientConnection connection = new DefaultNHttpClientConnection(
					session, 1024);

			HttpProcessor processor = HttpProcessorBuilder.create()
					.add(new RequestContent()).add(new RequestTargetHost())
					.add(new RequestConnControl())
					.add(new RequestExpectContinue(true)).build();

			HttpAsyncRequestExecutor exec = new HttpAsyncRequestExecutor();
			HttpAsyncRequester requester = new HttpAsyncRequester(processor);
			BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
					"POST", "/");
			request.setEntity(new AudioStreamingEntity(new InputStream() {

				@Override
				public int read() throws IOException {
					// TODO Auto-generated method stub
					return 0;
				}
			}));
			Future<Integer> future = requester.execute(
					new AudioRequestProducer(new HttpHost("www.google.com"),
							request), new AudioResultConsumer(), connection);
			Integer res = future.get();

		} catch (IOReactorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
