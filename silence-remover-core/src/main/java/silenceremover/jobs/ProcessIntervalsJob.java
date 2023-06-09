package silenceremover.jobs;

import java.util.List;
import java.util.function.DoubleConsumer;

import job4j.Job;

import silenceremover.Interval;
import silenceremover.config.ProjectConfig;

public class ProcessIntervalsJob extends Job<Void> {
	private ProjectConfig config;
	private final List<Interval> intervals;

	public ProcessIntervalsJob(ProjectConfig config, List<Interval> intervals) {
		super(JobCategories.PROCESS_INTERVALS);

		this.config = config;
		this.intervals = intervals;
	}

	@Override
	protected Void execute(DoubleConsumer progressReceiver) throws Exception {
		addPaddingToAudibleSegments();
		return null;
	}

	private void addPaddingToAudibleSegments() {
		for (int i = 0; i < intervals.size(); i++) {
			Interval interval = intervals.get(i);

			if (!interval.isSilent()) {
				interval.addPadding(config.audibleSegmentPadding, i == 0, i == intervals.size() - 1);
			}
		}
	}
}
