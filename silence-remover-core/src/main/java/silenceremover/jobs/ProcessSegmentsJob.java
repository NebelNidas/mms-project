package silenceremover.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.DoubleConsumer;

import org.apache.commons.io.FileUtils;

import job4j.Job;

import silenceremover.Interval;
import silenceremover.SplitTask;
import silenceremover.config.ProjectConfig;

public class ProcessSegmentsJob extends Job<Path> {
	private static final int intervalsPerFfmpegInstance = 2;
	private static final int threadsPerFfmpegInstance = 4;
	private final ProjectConfig config;
	private final Path tempDir;
	private final ExecutorService threadPool;
	private final List<Interval> intervals;
	private final AtomicInteger splitsCompleted = new AtomicInteger();
	private List<Interval> audibleIntervals;
	private Queue<List<Interval>> intervalGroups = new ConcurrentLinkedQueue<>();
	private List<Path> splitFiles = new ArrayList<>();

	public ProcessSegmentsJob(ProjectConfig config, List<Interval> intervals) {
		super(JobCategories.PROCESS_SEGMENTS);

		this.config = config;
		this.tempDir = config.outputFile.resolve("silence-remover-temp");
		this.threadPool = Executors.newFixedThreadPool(Math.max(1, config.maxThreads / threadsPerFfmpegInstance));
		this.intervals = intervals;
	}

	@Override
	protected Path execute(DoubleConsumer progressReceiver) {
		process();
		return config.outputFile;
	}

	public void process() {
		collectIntervalGroups();

		int id = 0;

		for (List<Interval> group : intervalGroups) {
			threadPool.submit(new SplitTask(id, config, group, this::getTempFileForInterval,
					this::onSplitTaskProgressChange, this::onSplitTaskCompleted)::call);
			id += intervalsPerFfmpegInstance;
		}
	}

	private void collectIntervalGroups() {
		List<Interval> intervalGroup = new ArrayList<>(intervalsPerFfmpegInstance);
		audibleIntervals = intervals.stream()
				.filter(interval -> !interval.isSilent())
				.toList();

		for (int i = 0; i < audibleIntervals.size(); i++) {
			intervalGroup.add(audibleIntervals.get(i));

			if ((i != 0 && i % intervalsPerFfmpegInstance == 0)
					|| i == audibleIntervals.size() - 1) {
				intervalGroups.add(intervalGroup);
				intervalGroup = new ArrayList<>(intervalsPerFfmpegInstance);
			}
		}
	}

	private Path getTempFileForInterval(Interval interval) {
		return tempDir.resolve(audibleIntervals.indexOf(interval) + ".mp4");
	}

	private void onSplitTaskProgressChange(double progress) {
		onOwnProgressChange(progress / audibleIntervals.size() / 2);
	}

	private void onSplitTaskCompleted(List<Path> splitFiles) {
		synchronized (splitFiles) {
			this.splitFiles.addAll(splitFiles);
			splitsCompleted.incrementAndGet();
		}

		if (splitsCompleted.get() == audibleIntervals.size()) {
			mergeSegments();
		}
	}

	private void mergeSegments() {
		splitFiles.sort(Comparator.naturalOrder());

		// try {
		// 	FFmpeg ffmpeg = new FFmpeg("ffmpeg.exe");
		// 	FFprobe ffprobe = new FFprobe("ffprobe.exe");
		// 	FFmpegProbeResult probeResult = ffprobe.probe(config.inputFile.toString());

		// 	FFmpegBuilder builder = new FFmpegBuilder()
		// 			.setInput(probeResult)
		// 			.overrideOutputFiles(true)
		// 			.addExtraArgs("-vn")
		// 			.addOutput(config.outputFile.toString())
		// 			.setTargetSize(250_000)
		// 			.done();

		// 	FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

		// 	executor.createJob(builder).run();
		// } catch (Throwable e) {
		// 	throw new RuntimeException(e);
		// }

		List<String> command = new ArrayList<>();
		command.add("ffmpeg");
		command.add("-f");
		command.add("concat");
		command.add("-safe");
		command.add("0");
		command.add("-i");
		command.add(concat_file.toString());
		command.add("-c");
		command.add("copy");
		command.add("-y");
		command.add("-loglevel");
		command.add("verbose");
		command.add(config.outputFile.toString());

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();
		BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));

		int fileIndex = 1;
		String line;

		while ((line = stdOut.readLine()) != null) {
			if (line.contains("Auto-inserting")) {
				onOwnProgressChange((double) fileIndex / audibleIntervals.size() / 2);
			}

			fileIndex++;
		}

		cleanUp();
	}

	private void cleanUp() throws IOException {
		FileUtils.deleteDirectory(tempDir.toFile());
	}
}
