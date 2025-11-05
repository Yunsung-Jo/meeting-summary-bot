package dev.yunsung.summary;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

import dev.yunsung.record.AudioData;

public class SummaryService {

	private static final Map<String, Summarizer> strategies = Map.of(
		"gemini", GeminiSummarizer.getInstance()
	);

	private final Summarizer summarizer;

	public SummaryService() {
		String modelName = System.getenv("MODEL_NAME");
		this.summarizer = strategies.entrySet().stream()
			.filter(entry -> modelName.startsWith(entry.getKey()))
			.map(Map.Entry::getValue)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 모델입니다: " + modelName));
	}

	public String summarize(TreeMap<LocalDateTime, AudioData> archiveAudios) {
		return summarizer.summarize(archiveAudios);
	}
}
