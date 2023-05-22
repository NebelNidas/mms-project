package job4j;

import java.io.PrintWriter;
import java.io.StringWriter;

class Util {
	/** Max accepted float rounding error. */
	public static final float floatError = 1e-5f;

	public static String getStacktrace(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
}
