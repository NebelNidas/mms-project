package silenceremover.server.cli;

import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import silenceremover.cli.provider.CliCommandProvider;
import silenceremover.server.SilenceRemoverServer;

public class StartServerCommandProvider implements CliCommandProvider {
	private static final String commandName = "start-server";
	private final StartServerCommand command = new StartServerCommand();

	@Parameters(commandNames = {commandName})
	class StartServerCommand {
		@Parameter(names = {ServerCliParameters.INPUT_DIR}, required = true)
		Path inputDir;

		@Parameter(names = {ServerCliParameters.OUTPUT_DIR}, required = true)
		Path outputDir;

		@Parameter(names = {ServerCliParameters.FFMPEG_EXECUTABLE}, required = true)
		Path ffmpegExecutable;

		@Parameter(names = {ServerCliParameters.MAX_THREADS})
		Integer maxThreads = Runtime.getRuntime().availableProcessors() / 2;

		@Parameter(names = {ServerCliParameters.THREADS_PER_FFMPEG_INSTANCE})
		Integer threadsPerFfmpegInstance = 2;

		@Parameter(names = {ServerCliParameters.SEGMENTS_PER_FFMPEG_INSTANCE})
		Integer segmentsPerFfmpegInstance = 2;
	}

	@Override
	public String getCommandName() {
		return commandName;
	}

	@Override
	public Object getDataHolder() {
		return command;
	}

	@Override
	public void processArgs() {
		validateArgs();

		SpringApplication.run(SilenceRemoverServer.class,
				command.inputDir.toString(),
				command.outputDir.toString(),
				command.ffmpegExecutable.toString(),
				command.maxThreads.toString(),
				command.threadsPerFfmpegInstance.toString(),
				command.segmentsPerFfmpegInstance.toString());
	}

	private void validateArgs() {
		if (!command.ffmpegExecutable.toFile().exists()) {
			throw new IllegalArgumentException("Passed FFmpeg executable doesn't exist!");
		}

		command.inputDir.toFile().mkdirs();
		command.outputDir.toFile().mkdirs();
	}
}
