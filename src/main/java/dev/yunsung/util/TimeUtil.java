package dev.yunsung.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

	private static final DateTimeFormatter TIMESTAMP_FORMATTER =
		DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static String formatTimestamp(LocalDateTime time) {
		return time.format(TIMESTAMP_FORMATTER);
	}

	public static String formatDuration(LocalDateTime startTime, LocalDateTime endTime) {
		if (startTime == null || endTime == null) {
			return "00:00:00";
		}

		Duration duration = Duration.between(startTime, endTime);
		if (duration.isNegative()) {
			duration = duration.abs();
		}

		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();
		long seconds = duration.toSecondsPart();

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
}
