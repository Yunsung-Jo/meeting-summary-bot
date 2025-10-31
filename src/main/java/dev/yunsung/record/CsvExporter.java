package dev.yunsung.record;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;

import dev.yunsung.util.TimeUtil;

public class CsvExporter {

	public static File saveAsCsv(TreeMap<Long, AudioText> texts, String folderName) throws IOException {
		File directory = new File("audio/" + folderName);
		File file = new File(directory, "script.csv");
		directory.mkdirs();

		StringBuilder content = new StringBuilder();
		content.append("Timestamp,Speaker,Sentence\n");

		for (Map.Entry<Long, AudioText> entry : texts.entrySet()) {
			content.append(TimeUtil.formatTimestamp(entry.getKey())).append(",")
				.append(escapeCsv(entry.getValue().name())).append(",")
				.append(escapeCsv(entry.getValue().sentence())).append("\n");
		}

		Files.writeString(file.toPath(), content.toString(), StandardCharsets.UTF_8);
		return file;
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
