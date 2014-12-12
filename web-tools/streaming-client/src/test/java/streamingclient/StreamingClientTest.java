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

import com.redshift.teset.RasrStreamingRequest;

public class StreamingClientTest {

	AudioFormat format = new AudioFormat((float) 44100, 16, 2, true, false);
	Mixer.Info[] mixers = AudioSystem.getMixerInfo();
	Mixer mixer = AudioSystem.getMixer(mixers[2]);

	@Test
	public void test() throws LineUnavailableException {
		TargetDataLine line = null;
		
	     Line.Info[] targetLineInfo = mixer.getTargetLineInfo();
	     System.out.println(targetLineInfo[0]);
	     
	     if (targetLineInfo.length > 0) {
	            line = (TargetDataLine) mixer.getLine(targetLineInfo[0]);
	     }
	     else
	     {
	    	 fail("couldn't find a record mixer");
	     }
	    
		try {
			
			line.open();
			line.start();
			RasrStreamingRequest request = new RasrStreamingRequest(
					"http://192.168.1.119:8080/rasr-ws/rasr/control/42");
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
