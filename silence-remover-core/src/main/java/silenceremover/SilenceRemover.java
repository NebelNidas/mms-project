package silenceremover;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import job4j.Job;

import silenceremover.config.ProjectConfig;
import silenceremover.jobs.DetectSilenceJob;
import silenceremover.jobs.JobCategories;
import silenceremover.jobs.ProcessIntervalsJob;
import silenceremover.jobs.ProcessSegmentsJob;

public class SilenceRemover {
	public static final Logger LOGGER = LoggerFactory.getLogger("Silence Remover");
	private final ProjectConfig config;
	private final List<Interval> intervals = new ArrayList<>();

	public SilenceRemover(ProjectConfig config) {
		this.config = config;
	}

	public Job<File> process() {
		var detectIntervalsJob = new DetectSilenceJob(config.inputFile, config.noiseTolerance, config.minSegmentLength);
		var processIntervalsJob = new ProcessIntervalsJob(config, intervals);
		var processSegmentsJob = new ProcessSegmentsJob(config, intervals);

		return new Job<File>(JobCategories.PROCESS_FILE, config.inputFile.toFile().getName()) {
			@Override
			protected void registerSubJobs() {
				addSubJob(detectIntervalsJob, true);
				addSubJob(processIntervalsJob, false);
				addSubJob(processSegmentsJob, true);
			}

			@Override
			protected File execute(DoubleConsumer progressReceiver) {
				intervals.addAll(detectIntervalsJob.runAndAwait().getResult().get());
				processIntervalsJob.run();
				processSegmentsJob.run();
				return config.outputFile.toFile();
			}
		};
	}
}
