package dev.yunsung.record;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

import dev.yunsung.util.TimeUtil;

public class CsvExporter {

	public static File saveAsCsv(
		TreeMap<LocalDateTime, AudioData> archiveAudios,
		String folderName
	) throws IOException {
		File directory = new File("audio/" + folderName);
		File file = new File(directory, "script.csv");
		directory.mkdirs();

		StringBuilder content = new StringBuilder();
		content.append("StartTime,EndTime,Speaker,Sentence\n");

		for (Map.Entry<LocalDateTime, AudioData> entry : archiveAudios.entrySet()) {
			AudioData audioData = entry.getValue();
			String startTime = TimeUtil.formatTimestamp(audioData.getStartTime());
			String endTime = TimeUtil.formatTimestamp(audioData.getEndTime());
			String speaker = escapeCsv(audioData.getSpeaker());
			String sentence = escapeCsv(audioData.getSentence());

			content.append(startTime).append(",")
				.append(endTime).append(",")
				.append(speaker).append(",")
				.append(sentence).append("\n");
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
