package silenceremover.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.DoubleConsumer;

import org.apache.commons.io.FileUtils;

import job4j.Job;

import silenceremover.Interval;
import silenceremover.SilenceRemover;
import silenceremover.SplitThread;
import silenceremover.config.ProjectConfig;

public class ProcessSegmentsJob extends Job<Path> {
	private final ProjectConfig config;
	private final Path tempDir;
	private final ExecutorService threadPool;
	private final List<Interval> intervals;
	private List<Interval> audibleIntervals;
	private Queue<List<Interval>> audibleIntervalGroups = new ConcurrentLinkedQueue<>();
	private List<Path> splitFiles = new ArrayList<>();

	public ProcessSegmentsJob(ProjectConfig config, List<Interval> intervals) {
		super(JobCategories.PROCESS_SEGMENTS);

		this.config = config;
		this.tempDir = config.outputFile.toAbsolutePath().getParent().resolve("silence-remover-temp");
		this.tempDir.toFile().mkdirs();
		this.threadPool = Executors.newFixedThreadPool(Math.max(1, config.maxThreads / config.threadsPerFfmpegInstance));
		this.intervals = intervals;
	}

	@Override
	protected Path execute(DoubleConsumer progressReceiver) throws Exception {
		process();
		return config.outputFile;
	}

	public void process() throws Exception {
		collectIntervalGroups();

		int id = 0;
		List<Future<List<Path>>> futures = new ArrayList<>();

		for (List<Interval> group : audibleIntervalGroups) {
			futures.add(threadPool.submit(new SplitThread(id, config, group, config.threadsPerFfmpegInstance,
					this::getTempFileForInterval, this::onSplitTaskProgressChange)::call));
			id += config.segmentsPerFfmpegInstance;
		}

		for (Future<List<Path>> future : futures) {
			splitFiles.addAll(future.get());
		}

		threadPool.shutdown();
		mergeSegments();
	}

	private void collectIntervalGroups() {
		List<Interval> intervalGroup = new ArrayList<>(config.segmentsPerFfmpegInstance);
		audibleIntervals = intervals.stream()
				.filter(interval -> !interval.isSilent())
				.toList();

		for (int i = 0; i < audibleIntervals.size(); i++) {
			intervalGroup.add(audibleIntervals.get(i));

			if ((i != 0 && i % config.segmentsPerFfmpegInstance == 0)
					|| i == audibleIntervals.size() - 1) {
				audibleIntervalGroups.add(intervalGroup);
				intervalGroup = new ArrayList<>(config.segmentsPerFfmpegInstance);
			}
		}
	}

	private Path getTempFileForInterval(Interval interval) {
		return tempDir.resolve(audibleIntervals.indexOf(interval) + ".mp4");
	}

	private void onSplitTaskProgressChange(double progress) {
		onOwnProgressChange(progress / audibleIntervals.size() / 2);
	}

	private void mergeSegments() throws IOException {
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

		List<String> lines = new ArrayList<>(splitFiles.size());

		for (Path path : splitFiles) {
			lines.add("file '" + path.toAbsolutePath().toString() + "'");
		}

		Path txtFile = tempDir.resolve("segments.txt");
		Files.write(txtFile, lines, StandardCharsets.UTF_8);

		command.add(txtFile.toString());
		command.add("-c");
		command.add("copy");
		command.add("-y");
		command.add("-threads");
		command.add(Integer.toString(config.maxThreads));
		command.add("-loglevel");
		command.add("verbose");
		command.add(config.outputFile.toAbsolutePath().toString());

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.redirectErrorStream(true);

			Process process = processBuilder.start();
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));

			int fileIndex = 1;
			String line;

			while ((line = stdOut.readLine()) != null) {
				SilenceRemover.LOGGER.info(line.toString());

				if (line.contains("Auto-inserting")) {
					onOwnProgressChange((double) fileIndex / audibleIntervals.size() / 2);
					fileIndex++;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			cleanUp();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void cleanUp() throws IOException {
		FileUtils.deleteDirectory(tempDir.toFile());
		threadPool.shutdownNow();
	}
}
