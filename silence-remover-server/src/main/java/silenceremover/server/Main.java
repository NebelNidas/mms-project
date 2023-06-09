package silenceremover.server;

import silenceremover.cli.SilenceRemoverCli;
import silenceremover.server.cli.StartServerCommandProvider;

public class Main {
	public static void main(String[] args) {
		SilenceRemoverCli cli = new SilenceRemoverCli(false);
		cli.registerCommandProvider(new StartServerCommandProvider());
		cli.processArgs(args);
	}
}
