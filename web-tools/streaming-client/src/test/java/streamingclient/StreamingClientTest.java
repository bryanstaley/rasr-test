package streamingclient;

import static org.junit.Assert.fail;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.junit.Test;

import com.redshift.test.RasrStreamingRequest;

public class StreamingClientTest {

	//AudioFormat format = new AudioFormat((float) 44100, 16, 2, true, false);
	AudioFormat format = new AudioFormat((float) 16000, 16,1, true, false);
	DataLine.Info info = new DataLine.Info(TargetDataLine.class,format);
	

	Mixer.Info[] mixers = AudioSystem.getMixerInfo();
	Mixer mixer = AudioSystem.getMixer(mixers[2]);

	@Test
	public void test() throws LineUnavailableException {
		
		if (!AudioSystem.isLineSupported(info))
		{
			System.out.println("Damn");
		}
		TargetDataLine line = null;

		Line.Info[] targetLineInfo = mixer.getTargetLineInfo();
		System.out.println(targetLineInfo[0]);

		if (targetLineInfo.length > 0) {
			line = (TargetDataLine)AudioSystem.getLine(info);
		} else {
			fail("couldn't find a record mixer");
		}

		try {

			line.open(format);
			line.start();
			RasrStreamingRequest request = new RasrStreamingRequest(
					//"http://192.168.1.104:8080/rasr-ws/rasr/control/42");
					"http://192.168.1.104:8080/rasr-streaming-server/stream");
			request.stream(line);
		} catch (Exception e) {
			System.out.println(e);
			fail(e.getMessage());
		} finally {
			if (line != null)
				line.close();
		}

	}

}
