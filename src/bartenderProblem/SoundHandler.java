package bartenderProblem;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

public enum SoundHandler {
	OPENDOOR("audio/opendoor.wav"),
	CLOSEDOOR("audio/closedoor.wav"),
	BACKGROUNDMUSIC("audio/bgmusic.wav");
	
	
	private String file;
	
	private SoundHandler(String file) {
		this.file = file;
	}
	
	public void play() {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(file));
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
	        clip.start();
		} catch (UnsupportedAudioFileException e) {
		} catch (IOException e) {
		} catch (LineUnavailableException e) {
		}
	}
}
