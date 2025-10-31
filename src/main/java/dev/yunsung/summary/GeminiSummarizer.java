package dev.yunsung.summary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeMap;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import dev.yunsung.record.AudioText;
import dev.yunsung.util.TimeUtil;

public class GeminiSummarizer implements Summarizer {

	/**
	 * %attendees: 참석자<br>
	 * %timestamp: 일시<br>
	 * %script: 대본
	 */
	private static final String PROMPT;
	private final Client client = new Client();
	private final String modelName = System.getenv("MODEL_NAME");

	static {
		try {
			PROMPT = new String(Files.readAllBytes(Paths.get("PROMPT")));
		} catch (IOException e) {
			throw new RuntimeException("프롬프트를 읽을 수 없습니다.", e);
		}
	}

	@Override
	public String summarize(TreeMap<Long, AudioText> audioTexts) {
		if (audioTexts.isEmpty()) {
			return "녹음된 음성이 없어 회의록을 생성할 수 없습니다.";
		}

		List<String> attendees = getAttendees(audioTexts);
		String timestamp = TimeUtil.formatTimestamp(audioTexts.firstKey());
		String script = getScript(audioTexts);

		String prompt = PROMPT
			.replace("%attendees", String.join(",", attendees))
			.replace("%timestamp", timestamp)
			.replace("%script", script);

		try {
			GenerateContentResponse response = client.models.generateContent(modelName, prompt, null);
			return response.text();
		} catch (Exception e) {
			return "Gemini API 요약 실패";
		}
	}

	private List<String> getAttendees(TreeMap<Long, AudioText> audioTexts) {
		return audioTexts.values().stream()
			.map(AudioText::name)
			.distinct()
			.toList();
	}

	private String getScript(TreeMap<Long, AudioText> audioTexts) {
		StringBuilder sb = new StringBuilder();
		for (AudioText audioText : audioTexts.values()) {
			sb.append(audioText.toString()).append("\n");
		}
		return sb.toString();
	}
}
