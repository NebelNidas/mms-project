package silenceremover.cli.provider.builtin;

/**
 * All CLI parameters the CLI module can handle by default.
 */
public class BuiltinCliParameters {
	public static final String INPUT_FILE = "--input-file";
	public static final String OUTPUT_FILE = "--output-file";
	public static final String FFMPEG_EXECUTABLE = "--ffmpeg-executable";
	public static final String MIN_SEGMENT_LENGTH = "--min-segment-length";
	public static final String MAX_NEGATIVE_VOLUME_DEVIATION = "--max-negative-volume-deviation";
	public static final String AUDIBLE_SEGMENT_PADDING = "--audible-segment-padding";
	public static final String AUDIO_ONLY = "--audio-only";
	public static final String MAX_THREADS = "--max-threads";
	public static final String THREADS_PER_SEGMENT = "--threads-per-segment";
	public static final String SEGMENTS_PER_FFMPEG_INSTANCE = "--segments-per-ffmpeg-instance";
}
