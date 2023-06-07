package silenceremover;

public class Interval {
	private Double start;
	private Double end;
	private Double duration;
	private Boolean silent;

	public Interval(double start, double end, boolean silent) {
		this.start = start;
		this.end = end;
		this.silent = silent;
	}

	public Interval(double start, boolean silent) {
		this.start = start;
		this.silent = silent;
	}

	public Interval(double start) {
		this.start = start;
	}

	public Interval() {
	}

	public double getStart() {
		if (start == null) {
			throw new RuntimeException("'start' hasn't been set!");
		}

		return start;
	}

	public double getEnd() {
		if (end == null) {
			throw new RuntimeException("'end' hasn't been set!");
		}

		return end;
	}

	public void setEnd(double end) {
		this.end = end;
	}

	public double getDuration() {
		if (duration == null) {
			duration = getEnd() - getStart();
		}

		return duration;
	}

	public void addPadding(double padding, boolean isStartInterval, boolean isEndInterval) {
		padding = (silent ? -1 : 1) * padding;

		if (!isStartInterval) {
			start -= padding;
		}

		if (!isEndInterval) {
			end += padding;
		}
	}

	public Interval copy() {
		return new Interval(start, end, silent);
	}

	@Override
	public String toString() {
		return String.format("<Interval start=%.2f end=%.2f duration=%.2f isSilent=%b>", start, end, getDuration(), silent);
	}

	public Boolean isSilent() {
		return silent;
	}

	public void setSilent(Boolean silent) {
		this.silent = silent;
	}
}
