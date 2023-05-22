package silenceremover.cli;

import silenceremover.cli.provider.builtin.ProcessCommandProvider;

public class Main {
	public static void main(String[] args) {
		SilenceRemoverCli cli = new SilenceRemoverCli(false);
		cli.registerCommandProvider(new ProcessCommandProvider());
		cli.processArgs(args);
	}
}
