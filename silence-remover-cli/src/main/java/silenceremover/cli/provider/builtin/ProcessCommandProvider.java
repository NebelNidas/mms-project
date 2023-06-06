package silenceremover.cli.provider.builtin;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import job4j.Job;

import silenceremover.SilenceRemover;
import silenceremover.cli.SilenceRemoverCli;
import silenceremover.cli.provider.CliCommandProvider;
import silenceremover.config.ProjectConfig;

public class ProcessCommandProvider implements CliCommandProvider {
	private static final String commandName = "process";
	private final ProcessCommand command = new ProcessCommand();

	@Parameters(commandNames = {commandName})
	class ProcessCommand {
		@Parameter(names = {BuiltinCliParameters.INPUT_FILE}, required = true)
		Path inputFile;

		@Parameter(names = {BuiltinCliParameters.OUTPUT_FILE}, required = true)
		Path outputFile;

		@Parameter(names = {BuiltinCliParameters.MIN_SEGMENT_LENGTH})
		double minSegmentLength = 0.2;

		@Parameter(names = {BuiltinCliParameters.MAX_VOLUME})
		double maxVolume = 0.3;

		@Parameter(names = {BuiltinCliParameters.AUDIO_ONLY})
		boolean audioOnly = false;

		@Parameter(names = {BuiltinCliParameters.MAX_THREADS})
		int maxThreads = Runtime.getRuntime().availableProcessors() / 2;
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

		ProjectConfig config = ProjectConfig.builder(command.inputFile, command.outputFile)
				.minSegmentLength(command.minSegmentLength)
				.maxVolume(command.maxVolume)
				.audioOnly(command.audioOnly)
				.maxThreads(command.maxThreads)
				.build();
		SilenceRemover silenceRemover = new SilenceRemover(config);
		AtomicInteger lastPrintedPercentage = new AtomicInteger(-1);

		Job<?> job = silenceRemover.process();
		job.addProgressListener((progress) -> {
			int percentage = (int) (progress * 100);

			if (percentage != lastPrintedPercentage.get() && percentage % 10 == 0) {
				SilenceRemoverCli.LOGGER.info("{}% ... ", percentage);
				lastPrintedPercentage.set(percentage);
			}
		});
		job.runAndAwait();
		SilenceRemoverCli.LOGGER.info("Done!");
	}

	private void validateArgs() {
		if (!command.inputFile.toFile().exists()) {
			throw new IllegalArgumentException("Passed input file doesn't exist!");
		}

		if (command.outputFile.toFile().exists()) {
			throw new IllegalArgumentException("Output file already exist!");
		}
	}
}
