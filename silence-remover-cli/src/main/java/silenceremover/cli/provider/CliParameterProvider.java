package silenceremover.cli.provider;

public interface CliParameterProvider {
	Object getDataHolder();
	void processArgs();
}
