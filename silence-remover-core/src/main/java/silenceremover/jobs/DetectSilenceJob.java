package silenceremover.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import job4j.Job;

import silenceremover.Interval;
import silenceremover.SilenceRemover;

public class DetectSilenceJob extends Job<List<Interval>> {
	private final Path inputFile;
	private final double silenceLevel;
	private final double silenceTimeThreshold;

	public DetectSilenceJob(Path inputFile, double silenceLevel, double silenceTimeThreshold) {
		super(JobCategories.DETECT_INTERVALS);

		this.inputFile = inputFile;
		this.silenceLevel = silenceLevel;
		this.silenceTimeThreshold = silenceTimeThreshold;
	}

	@Override
	protected List<Interval> execute(DoubleConsumer progressReceiver) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder(
				"ffmpeg.exe",
				"-i", inputFile.toString(),
				"-vn",
				"-af",
				"silencedetect=noise=" + silenceLevel + "dB:d=" + silenceTimeThreshold,
				"-f", "null",
				"-"
		);

		Process process = processBuilder.start();

		// FFmpeg writes to stderr instead of stdout for whatever reason...
		BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		List<Interval> intervals = new ArrayList<>();
		Interval currentInterval = new Interval(0, false);
		double mediaDuration = 0;
		String line;

		while ((line = stdErr.readLine()) != null) {
			SilenceRemover.LOGGER.info(line.toString());

			if (line.contains("Duration")) {
				Pattern pattern = Pattern.compile("Duration: ([0-9:]+.?[0-9]*)");
				Matcher matcher = pattern.matcher(line);

				if (!matcher.find()) {
					continue;
				}

				String[] timeParts = matcher.group(1).split(":");
				int hour = Integer.parseInt(timeParts[0]);
				int minute = Integer.parseInt(timeParts[1]);
				String[] secondMillisecondParts = timeParts[2].split("\\.");
				int second = Integer.parseInt(secondMillisecondParts[0]);
				int millisecond = Integer.parseInt(secondMillisecondParts[1]);
				mediaDuration = Double.parseDouble(second + 60 * (minute + 60 * hour) + "." + millisecond);
			} else if (line.contains("[silencedetect")) {
				Pattern pattern = Pattern.compile("\\[silencedetect @ [0-9xa-f]+] silence_([a-z]+): (-?[0-9]+.?[0-9]*[e-]*[0-9]*)");
				Matcher matcher = pattern.matcher(line);

				if (!matcher.find()) {
					continue;
				}

				String event = matcher.group(1);
				double time = Double.parseDouble(matcher.group(2));

				progressReceiver.accept(time / mediaDuration);

				if (event.equals("start")) {
					if (currentInterval.getStart() != time) {
						currentInterval.setEnd(time);
						intervals.add(currentInterval);
					}

					currentInterval = new Interval(time, true);
				}

				if (event.equals("end")) {
					currentInterval.setEnd(time);
					intervals.add(currentInterval);

					currentInterval = new Interval(time, false);
				}
			}
		}

		currentInterval.setEnd(mediaDuration);
		intervals.add(currentInterval);

		progressReceiver.accept(1);
		return intervals;
	}
}
