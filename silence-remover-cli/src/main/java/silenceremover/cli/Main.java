package silenceremover.cli;

import silenceremover.cli.provider.builtin.ProcessCommandProvider;

public class Main {
	public static void main(String[] args) {
		// Instantiate CLI handler. We don't accept unknown parameters,
		// since this is the base implementation where only known
		// providers are registered.
		SilenceRemoverCli cli = new SilenceRemoverCli(false);

		// Register all default providers.
		cli.registerCommandProvider(new ProcessCommandProvider());

		// Parse, handle errors, delegate to correct provider.
		cli.processArgs(args);
	}
}
