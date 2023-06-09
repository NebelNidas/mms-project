package silenceremover.server;

import java.nio.file.Path;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SilenceRemoverServer {
	public static Path inputDir;
	public static Path outputDir;
	public static Path ffmpegExecutable;
	public static int maxThreads;
	public static int threadsPerFfmpegInstance;
	public static int segmentsPerFfmpegInstance;

	public static void main(String[] args) {
		inputDir = Path.of(args[0]);
		outputDir = Path.of(args[1]);
		ffmpegExecutable = Path.of(args[2]);
		maxThreads = Integer.parseInt(args[3]);
		threadsPerFfmpegInstance = Integer.parseInt(args[4]);
		segmentsPerFfmpegInstance = Integer.parseInt(args[5]);
	}
}
