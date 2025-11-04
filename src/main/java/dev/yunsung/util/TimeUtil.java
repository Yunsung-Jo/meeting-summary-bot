package dev.yunsung.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

	private static final DateTimeFormatter TIMESTAMP_FORMATTER =
		DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static String formatTimestamp(LocalDateTime time) {
		return time.format(TIMESTAMP_FORMATTER);
	}
}
