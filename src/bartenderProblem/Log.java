package bartenderProblem;

public class Log {
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
	
	public static void println(String message) {
		if (!muted) {
			System.out.println(message);
		}
	}
}
