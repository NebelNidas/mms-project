package silenceremover.cli.provider;

/**
 * Top-level parameter provider.
 */
public interface CliParameterProvider {
	/**
	 * Instance of the class where {@link com.beust.jcommander.Parameter}
	 * annotations are placed into.
	 */
	Object getDataHolder();

	/**
	 * Verifies args and handles them.
	 */
	void processArgs();
}
