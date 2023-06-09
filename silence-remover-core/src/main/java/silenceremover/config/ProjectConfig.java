package silenceremover.config;

import java.nio.file.Path;

public class ProjectConfig {
	public static class Builder {
		protected final Path inputFile;
		protected final Path outputFile;
		protected final Path ffmpegExecutable;
		protected Double minSegmentLength;
		protected Integer maxNegativeVolumeDeviation;
		protected Double audibleSegmentPadding;
		protected Boolean audioOnly;
		protected Integer maxThreads = Runtime.getRuntime().availableProcessors() / 2;
		protected Integer threadsPerFfmpegInstance = maxThreads;
		protected Integer segmentsPerFfmpegInstance;

		private Builder(Path inputFile, Path outputFile, Path ffmpegExecutable) {
			this.inputFile = inputFile;
			this.outputFile = outputFile;
			this.ffmpegExecutable = ffmpegExecutable;
		}

		public Builder minSegmentLength(Double minSegmentLength) {
			this.minSegmentLength = minSegmentLength;
			return this;
		}

		public Builder maxNegativeVolumeDeviation(Integer maxNegVolumeDeviation) {
			this.maxNegativeVolumeDeviation = maxNegVolumeDeviation;
			return this;
		}

		public Builder audibleSegmentPadding(Double audibleSegmentPadding) {
			this.audibleSegmentPadding = audibleSegmentPadding;
			return this;
		}

		public Builder audioOnly(Boolean audioOnly) {
			this.audioOnly = audioOnly;
			return this;
		}

		public Builder maxThreads(Integer maxThreads) {
			this.maxThreads = maxThreads;
			return this;
		}

		public Builder threadsPerFfmpegInstance(Integer threadsPerFfmpegInstance) {
			this.threadsPerFfmpegInstance = threadsPerFfmpegInstance;
			return this;
		}

		public Builder segmentsPerFfmpegInstance(Integer segmentsPerFfmpegInstance) {
			this.segmentsPerFfmpegInstance = segmentsPerFfmpegInstance;
			return this;
		}

		public ProjectConfig build() {
			if (minSegmentLength == null) minSegmentLength = 0.4;
			if (maxNegativeVolumeDeviation == null) maxNegativeVolumeDeviation = 30;
			if (audibleSegmentPadding == null) audibleSegmentPadding = 0.25;
			if (audioOnly == null) audioOnly = false;
			if (maxThreads == null) maxThreads = Runtime.getRuntime().availableProcessors() / 2;
			if (threadsPerFfmpegInstance == null) threadsPerFfmpegInstance = maxThreads / 2;
			if (segmentsPerFfmpegInstance == null) segmentsPerFfmpegInstance = 4;

			return new ProjectConfig(
					inputFile,
					outputFile,
					ffmpegExecutable,
					minSegmentLength,
					maxNegativeVolumeDeviation,
					audibleSegmentPadding,
					audioOnly,
					maxThreads,
					threadsPerFfmpegInstance,
					segmentsPerFfmpegInstance);
		}
	}

	public static Builder builder(Path inputFile, Path outputFile, Path ffmpegExecutable) {
		return new Builder(inputFile, outputFile, ffmpegExecutable);
	}

	public final Path inputFile;
	public final Path outputFile;
	public final Path ffmpegExecutable;
	/**
	 * Minimum length in seconds a silent segment is allowed
	 * to have in order to be removed.
	 */
	public final double minSegmentLength;
	/**
	 * Padding in seconds that's added around audible segments,
	 * so the audio doesn't feel as cut off.
	 */
	public final double audibleSegmentPadding;
	/**
	 * Maximum negative volume deviation (in decibels) a segment is
	 * allowed to have in order to still be considered audible.
	 */
	public final int maxNegativeVolumeDeviation;
	public final boolean audioOnly;
	public final int maxThreads;
	public final int threadsPerFfmpegInstance;
	public final int segmentsPerFfmpegInstance;

	private ProjectConfig(
			Path inputFile,
			Path outputFile,
			Path ffmpegExecutable,
			double minSegmentLength,
			int maxNegativeVolumeDeviation,
			double audibleSegmentPadding,
			boolean audioOnly,
			int maxThreads,
			int threadsPerFfmpegInstance,
			int segmentsPerFfmpegInstance) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.ffmpegExecutable = ffmpegExecutable;
		this.minSegmentLength = minSegmentLength;
		this.maxNegativeVolumeDeviation = maxNegativeVolumeDeviation;
		this.audibleSegmentPadding = audibleSegmentPadding;
		this.audioOnly = audioOnly;
		this.maxThreads = maxThreads;
		this.threadsPerFfmpegInstance = threadsPerFfmpegInstance;
		this.segmentsPerFfmpegInstance = segmentsPerFfmpegInstance;
	}
}
