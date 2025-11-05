package dev.yunsung.stt;

import java.io.IOException;
import java.util.Map;

import dev.yunsung.util.HttpUtil;

public class Whisper implements STT {

	@Override
	public String transcribe(String path) throws IOException, InterruptedException {
		Map<String, String> requestBody = Map.of("file_path", "/audio/" + path);

		var jsonNode = HttpUtil.post(
			System.getenv("WHISPER_API_URL"),
			requestBody
		);
		return jsonNode.get("transcription").asText();
	}
}
