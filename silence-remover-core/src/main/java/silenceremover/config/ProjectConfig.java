package silenceremover.config;

import java.nio.file.Path;

public class ProjectConfig {
	public static class Builder {
		protected final Path inputFile;
		protected final Path outputFile;
		protected int minSegmentMillis;
		protected int maxVolume;
		protected float targetSpeed;
		protected boolean audioOnly;
		protected int maxThreads;

		private Builder(Path inputFile, Path outputFile) {
			this.inputFile = inputFile;
			this.outputFile = outputFile;
		}

		public Builder minSegmentMillis(int minSegmentMillis) {
			this.minSegmentMillis = minSegmentMillis;
			return this;
		}

		public Builder maxVolume(int maxVolume) {
			this.maxVolume = maxVolume;
			return this;
		}

		public Builder targetSpeed(float targetSpeed) {
			this.targetSpeed = targetSpeed;
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
					targetSpeed,
					audioOnly,
					maxThreads);
		}
	}

	public static Builder builder(Path inputFile, Path outputFile) {
		return new Builder(inputFile, outputFile);
	}

	public final Path inputFile;
	public final Path outputFile;
	public final int minSegmentMillis;
	public final int maxVolume;
	public final float targetSpeed;
	public final boolean audioOnly;
	public final int maxThreads;

	private ProjectConfig(
			Path inputFile,
			Path outputFile,
			int minSegmentMillis,
			int maxVolume,
			float targetSpeed,
			boolean audioOnly,
			int maxThreads) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.minSegmentMillis = minSegmentMillis;
		this.maxVolume = maxVolume;
		this.targetSpeed = targetSpeed;
		this.audioOnly = audioOnly;
		this.maxThreads = maxThreads;
	}

	@Override
	public String toString() {
		return "{"
			+ " inputFile='" + inputFile + "'"
			+ ", outputFile='" + outputFile + "'"
			+ ", minSegmentMillis='" + minSegmentMillis + "'"
			+ ", maxVolume='" + maxVolume + "'"
			+ ", targetSpeed='" + targetSpeed + "'"
			+ ", audioOnly='" + audioOnly + "'"
			+ ", maxThreads='" + maxThreads + "'"
			+ "}";
	}
}
