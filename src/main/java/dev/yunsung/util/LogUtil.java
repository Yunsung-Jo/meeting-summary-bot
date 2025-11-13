package dev.yunsung.util;

public class LogUtil {

	private Long startTime;

	public static void info(String msg) {
		System.out.println("[INFO] " + msg);
	}

	public static void error(String msg) {
		System.out.println("[ERROR] " + msg);
	}

	public static void error(String msg, Throwable e) {
		error(msg);
		e.printStackTrace(System.err);
	}

	public void start(String msg) {
		startTime = System.currentTimeMillis();
		info(msg);
	}

	public void record(String msg) {
		if (startTime == null) {
			return;
		}
		long endTime = System.currentTimeMillis();
		long ms = endTime - startTime;
		info(msg + " (실행 시간: " + ms + "ms)");
	}
}
