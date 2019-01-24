package bartenderProblem;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.*;

public enum SoundHandler {
	OPENDOOR("audio/opendoor.wav"),
	CLOSEDOOR("audio/closedoor.wav"),
	OPENBEER("audio/openbeer2.wav"),
	FILL_UP("audio/dishes3.wav"),
	BACKGROUNDMUSIC("audio/bgmusic.wav");
	
	
	private static boolean muted;
	
	static {
		unmute();
	}
	
	public static void mute() {
		muted = true;
	}
	
	public static void unmute() {
		muted = false;
	}
	
	
	private String file;
	
	private SoundHandler(String file) {
		this.file = file;
	}
	
	public void randomPlay() {
		if (new Random().nextInt(100) < 10) {
			play();
		}
	}
	
	public void play() {
		if (!muted) {
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
}
