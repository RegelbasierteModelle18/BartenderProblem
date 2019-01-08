package bartenderProblem;

import java.io.File;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public enum SoundHandler {
	OPENDOOR("audio/opendoor.mp3"),
	CLOSEDOOR("audio/closedoor.mp3"),
	BACKGROUNDMUSIC("audio/bgmusic.mp3");
	private String file;
	
	static {
		JFXPanel panel = new JFXPanel();
	}
	
	private SoundHandler(String file) {
		this.file = file;
	}
	
	public void play() {
		Media hit = new Media(new File(file).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(hit);
		mediaPlayer.play();
	}
}
