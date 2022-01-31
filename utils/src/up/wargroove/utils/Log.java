package up.wargroove.utils;

import java.io.PrintStream;

public class Log {

	private static PrintStream out = System.out;

	static enum Status {

		ERROR(-1, "[X]"),
		SUCCESS(0, "[OK]"),
		WARN(1, "[!]"),
		INFO(2, "[i]");

		int code;
		String str;

		Status(int code, String str) {

			this.code = code;
			this.str = str;

		}

	}

	public static void print(String message) {

		print(Status.INFO, message);	

	}

	public static void print(Status status, String message) {

		out.println(status.str + " " + message);

	}

	public void setOutputStream(PrintStream ps) {

		this.out = ps;

	}

}
