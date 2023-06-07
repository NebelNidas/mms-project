package silenceremover;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;

import silenceremover.config.ProjectConfig;

public class SplitThread implements Callable<List<Path>> {
	private final int id;
	private final ProjectConfig config;
	private final List<Interval> intervals;
	private final DoubleConsumer progressReceiver;
	private final Function<Interval, Path> tempFileGenerator;

	public SplitThread(int id, ProjectConfig config, List<Interval> intervals,
			Function<Interval, Path> tempFileGenerator,	DoubleConsumer progressReceiver) {
		this.id = id;
		this.config = config;
		this.tempFileGenerator = tempFileGenerator;
		this.intervals = intervals;
		this.progressReceiver = progressReceiver;
	}

	@Override
	public List<Path> call() {
		boolean applyFilter = true;
		double minimumIntervalDuration = config.minSegmentLength;
		List<Path> outputPaths = new ArrayList<>(intervals.size());

		for (Interval interval : intervals) {
			outputPaths.add(tempFileGenerator.apply(interval));
		}

		String[] command = generateCommand(outputPaths, applyFilter, minimumIntervalDuration);
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);
		Process process;

		try {
			process = processBuilder.start();

			BufferedReader stream = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;

			while ((line = stream.readLine()) != null) {
				SilenceRemover.LOGGER.info(line.toString());
			}

			int exitCode = process.waitFor();

			if (exitCode != 0) {
				throw new RuntimeException("Error while splitting the video");
			}
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}

		return outputPaths;
	}

	private String[] generateCommand(List<Path> outputFiles, boolean applyFilter, double minimumIntervalDuration) {
		List<String> commandParts = new ArrayList<>();
		commandParts.add("ffmpeg");

		for (Interval interval : intervals) {
			commandParts.add("-i");
			commandParts.add(config.inputFile.toString());
		}

		for (int i = 0; i < intervals.size(); i++) {
			Interval interval = intervals.get(i);
			Path outputFile = outputFiles.get(i);

			commandParts.add("-map");
			commandParts.add(Integer.toString(i));
			commandParts.add("-ss");
			commandParts.add(Double.toString(interval.getStart()));
			commandParts.add("-to");
			commandParts.add(Double.toString(interval.getEnd()));
			commandParts.add("-vsync");
			commandParts.add("1");
			commandParts.add("-async");
			commandParts.add("1");
			commandParts.add("-safe");
			commandParts.add("0");
			commandParts.add("-ignore_unknown");
			commandParts.add("-y");

			// if (applyFilter) {
			// 	String[] complexFilter;

			// 	double currentSpeed;
			// 	double currentVolume;

			// 	if (interval.isSilent()) {
			// 		currentSpeed = renderOptions.silent_speed;
			// 		currentVolume = renderOptions.silent_volume;
			// 	} else {
			// 		currentSpeed = renderOptions.audible_speed;
			// 		currentVolume = renderOptions.audible_volume;
			// 	}

			// 	currentSpeed = clampSpeed(interval.getDuration(), currentSpeed, minimumIntervalDuration);

			// 	if (!renderOptions.audio_only) {
			// 		complexFilter = new String[]{
			// 				String.format("[0:v]setpts=%.4f*PTS[v]", 1 / currentSpeed)
			// 		};
			// 	} else {
			// 		complexFilter = new String[0];
			// 	}

			// 	complexFilter = Arrays.copyOf(complexFilter, complexFilter.length + 1);
			// 	complexFilter[complexFilter.length - 1] = String.format("[0:a]atempo=%.4f,volume=%.4f[a]", currentSpeed, currentVolume);

			// 	command = Arrays.copyOf(command, command.length + 3);
			// 	command[command.length - 3] = "-filter_complex";
			// 	command[command.length - 2] = String.join(";", complexFilter);

			// 	if (!renderOptions.audio_only) {
			// 		command[command.length - 1] = "-map";
			// 	} else {
			// 		command = Arrays.copyOf(command, command.length + 2);
			// 		command[command.length - 2] = "-map";
			// 		command[command.length - 1] = "[v]";
			// 	}

			// 	command = Arrays.copyOf(command, command.length + 2);
			// 	command[command.length - 2] = "-map";
			// 	command[command.length - 1] = "[a]";
			// } else {
			// 	if (renderOptions.audio_only) {
			// 		command = Arrays.copyOf(command, command.length + 1);
			// 		command[command.length - 1] = "-vn";
			// 	}
			// }

			commandParts.add(outputFile.toString());
		}

		return commandParts.toArray(String[]::new);
	}
}
