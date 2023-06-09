package silenceremover.config;

import java.nio.file.Path;

public class ProjectConfig {
	public static class Builder {
		protected final Path inputFile;
		protected final Path outputFile;
		protected Double minSegmentLength;
		protected Double maxVolume;
		protected Boolean audioOnly;
		protected Integer maxThreads = Runtime.getRuntime().availableProcessors() / 2;
		protected Integer threadsPerFfmpegInstance = maxThreads;
		protected Integer segmentsPerFfmpegInstance;

		private Builder(Path inputFile, Path outputFile) {
			this.inputFile = inputFile;
			this.outputFile = outputFile;
		}

		public Builder minSegmentLength(Double minSegmentLength) {
			this.minSegmentLength = minSegmentLength;
			return this;
		}

		public Builder maxVolume(Double maxVolume) {
			this.maxVolume = maxVolume;
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
			if (minSegmentLength == null) minSegmentLength = 0.2;
			if (maxVolume == null) maxVolume = 0.3;
			if (audioOnly == null) audioOnly = false;
			if (maxThreads == null) maxThreads = Runtime.getRuntime().availableProcessors() / 2;
			if (threadsPerFfmpegInstance == null) threadsPerFfmpegInstance = maxThreads / 2;
			if (segmentsPerFfmpegInstance == null) segmentsPerFfmpegInstance = 4;

			return new ProjectConfig(
					inputFile,
					outputFile,
					minSegmentLength,
					maxVolume,
					audioOnly,
					maxThreads,
					threadsPerFfmpegInstance,
					segmentsPerFfmpegInstance);
		}
	}

	public static Builder builder(Path inputFile, Path outputFile) {
		return new Builder(inputFile, outputFile);
	}

	public final Path inputFile;
	public final Path outputFile;
	/**
	 * Minimum length in minutes a silent segment is allowed
	 * to have in order to be removed.
	 */
	public final double minSegmentLength;
	/**
	 * Maximum volume (in a range from 0 to 1) a segment is
	 * allowed to have in order to still be considered silent.
	 */
	public final double maxVolume;
	public final boolean audioOnly;
	public final int maxThreads;
	public final int threadsPerFfmpegInstance;
	public final int segmentsPerFfmpegInstance;

	private ProjectConfig(
			Path inputFile,
			Path outputFile,
			double minSegmentLength,
			double maxVolume,
			boolean audioOnly,
			int maxThreads,
			int threadsPerFfmpegInstance,
			int segmentsPerFfmpegInstance) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.minSegmentLength = minSegmentLength;
		this.maxVolume = maxVolume;
		this.audioOnly = audioOnly;
		this.maxThreads = maxThreads;
		this.threadsPerFfmpegInstance = threadsPerFfmpegInstance;
		this.segmentsPerFfmpegInstance = segmentsPerFfmpegInstance;
	}
}
