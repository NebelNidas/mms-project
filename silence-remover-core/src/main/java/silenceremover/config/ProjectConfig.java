package silenceremover.config;

import java.nio.file.Path;

public class ProjectConfig {
	public static class Builder {
		protected final Path inputFile;
		protected final Path outputFile;
		protected double minSegmentMillis;
		protected double maxVolume;
		protected boolean audioOnly;
		protected int maxThreads;

		private Builder(Path inputFile, Path outputFile) {
			this.inputFile = inputFile;
			this.outputFile = outputFile;
		}

		public Builder minSegmentLength(double minSegmentMillis) {
			this.minSegmentMillis = minSegmentMillis;
			return this;
		}

		public Builder maxVolume(double maxVolume) {
			this.maxVolume = maxVolume;
			return this;
		}

		public Builder audioOnly(boolean audioOnly) {
			this.audioOnly = audioOnly;
			return this;
		}

		public Builder maxThreads(int maxThreads) {
			this.maxThreads = maxThreads;
			return this;
		}

		public ProjectConfig build() {
			return new ProjectConfig(
					inputFile,
					outputFile,
					minSegmentMillis,
					maxVolume,
					audioOnly,
					maxThreads);
		}
	}

	public static Builder builder(Path inputFile, Path outputFile) {
		return new Builder(inputFile, outputFile);
	}

	public final Path inputFile;
	public final Path outputFile;
	public final double minSegmentLength;
	public final double maxVolume;
	public final boolean audioOnly;
	public final int maxThreads;

	private ProjectConfig(
			Path inputFile,
			Path outputFile,
			double minSegmentLength,
			double maxVolume,
			boolean audioOnly,
			int maxThreads) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.minSegmentLength = minSegmentLength;
		this.maxVolume = maxVolume;
		this.audioOnly = audioOnly;
		this.maxThreads = maxThreads;
	}

	@Override
	public String toString() {
		return "{"
			+ " inputFile='" + inputFile + "'"
			+ ", outputFile='" + outputFile + "'"
			+ ", minSegmentLength='" + minSegmentLength + "'"
			+ ", maxVolume='" + maxVolume + "'"
			+ ", audioOnly='" + audioOnly + "'"
			+ ", maxThreads='" + maxThreads + "'"
			+ "}";
	}
}
