package silenceremover;

import java.io.IOException;

public class Util {
	public static void killProcess(Process process) {
		try {
			process.destroy();

			// https://stackoverflow.com/a/18313604
			process.getInputStream().close();
			process.getOutputStream().close();
			process.getErrorStream().close();

			process.destroyForcibly();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
