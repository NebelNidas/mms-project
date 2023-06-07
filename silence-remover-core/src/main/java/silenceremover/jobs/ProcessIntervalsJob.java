package silenceremover.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;

import job4j.Job;
import job4j.JobState;

import silenceremover.Interval;

public class ProcessIntervalsJob extends Job<Void> {
	private final List<Interval> intervals;

	public ProcessIntervalsJob(List<Interval> intervals) {
		super(JobCategories.PROCESS_INTERVALS);

		this.intervals = intervals;
	}

	@Override
	protected Void execute(DoubleConsumer progressReceiver) throws Exception {
		combineIntervals(0.3);
		progressReceiver.accept(0.5);
		if (state == JobState.CANCELING) return null;

		addPaddingToAudibleSegments(0.25);
		return null;
	}

	private void combineIntervals(double shortIntervalThreshold) {
		List<Interval> combinedIntervals = new ArrayList<>();
		Interval combinedInterval = new Interval(0);

		for (Interval interval : intervals) {
			if (interval.getDuration() <= shortIntervalThreshold || combinedInterval.isSilent() == interval.isSilent()) {
				combinedInterval.setEnd(interval.getEnd());
			} else {
				if (combinedInterval.isSilent() == null) {
					combinedInterval.setSilent(interval.isSilent());
					combinedInterval.setEnd(interval.getEnd());
				} else {
					combinedIntervals.add(combinedInterval);
					combinedInterval = interval.copy();
				}
			}
		}

		if (combinedInterval.isSilent()) {
			combinedInterval.setSilent(false);
		}

		combinedIntervals.add(combinedInterval);
		intervals.clear();
		intervals.addAll(combinedIntervals);
	}

	private void addPaddingToAudibleSegments(double padding) {
		for (int i = 0; i < intervals.size(); i++) {
			Interval interval = intervals.get(i);

			if (!interval.isSilent()) {
				interval.addPadding(padding, i == 0, i == intervals.size() - 1);
			}
		}
	}
}
