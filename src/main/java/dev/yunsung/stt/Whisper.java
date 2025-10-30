package dev.yunsung.stt;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Whisper implements STT {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final HttpClient client = HttpClient.newBuilder()
		.version(HttpClient.Version.HTTP_1_1)
		.build();

	@Override
	public String transcribe(String path) throws IOException, InterruptedException {
		Map<String, String> requestBodyMap = Map.of("file_path", "/audio/" + path);
		String requestBodyJson = objectMapper.writeValueAsString(requestBodyMap);

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(System.getenv("WHISPER_API_URL")))
			.POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
			.header("Content-Type", "application/json")
			.header("Accept", "application/json")
			.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() != 200) {
			throw new IOException("Whisper API 호출 실패: " + response.statusCode());
		}

		try {
			var jsonNode = objectMapper.readTree(response.body());
			return jsonNode.get("transcription").asText();
		} catch (Exception e) {
			throw new IOException("JSON 파싱 실패: " + e.getMessage());
		}
	}
}
