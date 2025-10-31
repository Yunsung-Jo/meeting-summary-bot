package dev.yunsung.record;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

public class CsvExporter {

	private static final DateTimeFormatter CSV_TIMESTAMP_FORMATTER =
		DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static File saveAsCsv(TreeMap<Long, AudioText> texts, String folderName) throws IOException {
		File directory = new File("audio/" + folderName);
		File file = new File(directory, "script.csv");
		directory.mkdirs();

		StringBuilder content = new StringBuilder();
		content.append("Timestamp,Speaker,Sentence\n");

		for (Map.Entry<Long, AudioText> entry : texts.entrySet()) {
			content.append(formatTimestamp(entry.getKey())).append(",")
				.append(escapeCsv(entry.getValue().name())).append(",")
				.append(escapeCsv(entry.getValue().sentence())).append("\n");
		}

		Files.writeString(file.toPath(), content.toString(), StandardCharsets.UTF_8);
		return file;
	}

	private static String formatTimestamp(long timestamp) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
			.format(CSV_TIMESTAMP_FORMATTER);
	}

	private static String escapeCsv(String data) {
		if (data == null) {
			return "";
		}
		if (data.contains(",") || data.contains("\"") || data.contains("\n")) {
			return "\"" + data.replace("\"", "\"\"") + "\"";
		}
		return data;
	}
}
