package silenceremover;

import java.util.function.DoubleConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import silenceremover.config.ProjectConfig;

public class SilenceRemover {
	public static final Logger LOGGER = LoggerFactory.getLogger("Silence Remover");

	public void process(ProjectConfig config, DoubleConsumer processReceiver) {
		// TODO
		LOGGER.info(config.toString());
	}
}
