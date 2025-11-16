package dev.yunsung.util;

public class EnvUtil {

	public static String getenv(String name) {
		return getenv(name, null);
	}

	public static String getenv(String name, String def) {
		String env = System.getenv(name);
		if (env == null || env.isEmpty()) {
			LogUtil.info("'" + name + "'를 찾을 수 없어 기본값 '" + def + "'을 적용합니다.");
			return def;
		}
		return env;
	}

	public static int getenv(String name, int def) {
		try {
			return Integer.parseInt(getenv(name));
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static boolean getenv(String name, boolean def) {
		String env = getenv(name, def ? "true" : "false");
		return env.equals("true");
	}
}
