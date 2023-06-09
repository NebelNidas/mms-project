package silenceremover.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import job4j.Job;

import silenceremover.Interval;
import silenceremover.SilenceRemover;
import silenceremover.config.ProjectConfig;

public class DetectSilenceJob extends Job<List<Interval>> {
	private static final Pattern durationPattern = Pattern.compile("Duration: ([0-9:]+.?[0-9]*)");
	private static final Pattern silencedetectPattern = Pattern.compile("\\[silencedetect @ [0-9xa-f]+] silence_([a-z]+): (-?[0-9]+.?[0-9]*[e-]*[0-9]*)");
	private final ProjectConfig config;

	public DetectSilenceJob(ProjectConfig config) {
		super(JobCategories.DETECT_INTERVALS);
		this.config = config;
	}

	@Override
	protected List<Interval> execute(DoubleConsumer progressReceiver) throws IOException {
		// http://underpop.online.fr/f/ffmpeg/help/silencedetect.htm.gz
		ProcessBuilder processBuilder = new ProcessBuilder(
				"ffmpeg",
				"-i", config.inputFile.toString(),
				"-vn",
				"-af",
				"silencedetect=noise=-" + (config.maxNegativeVolumeDeviation - 1) + "dB:d=" + config.minSegmentLength,
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
			SilenceRemover.LOGGER.trace(line.toString());

			if (line.contains("Duration")) {
				Matcher matcher = durationPattern.matcher(line);

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
				Matcher matcher = silencedetectPattern.matcher(line);

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
