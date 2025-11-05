package dev.yunsung.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpUtil {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final HttpClient client = HttpClient.newBuilder()
		.version(HttpClient.Version.HTTP_1_1)
		.build();

	public static JsonNode post(
		String uri,
		Map<?, ?> requestBody
	) throws IOException, InterruptedException {
		return post(uri, requestBody, Map.of());
	}

	public static JsonNode post(
		String uri,
		Map<?, ?> requestBody,
		Map<String, String> headers
	) throws IOException, InterruptedException {
		String requestBodyJson = objectMapper.writeValueAsString(requestBody);
		HttpRequest.Builder builder = HttpRequest.newBuilder()
			.uri(URI.create(uri))
			.POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
			.header("Content-Type", "application/json")
			.header("Accept", "application/json");
		return request(builder, headers);
	}

	public static JsonNode get(
		String uri,
		Map<String, String> headers
	) throws IOException, InterruptedException {
		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(uri));
		return request(builder, headers);
	}

	private static JsonNode request(
		HttpRequest.Builder builder,
		Map<String, String> headers
	) throws IOException, InterruptedException {
		for (Map.Entry<String, String> header : headers.entrySet()) {
			builder.header(header.getKey(), header.getValue());
		}

		HttpRequest request = builder.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() / 100 != 2) {
			throw new IOException("API 호출 실패: " + response.body());
		}
		return objectMapper.readTree(response.body());
	}
}
