package dev.yunsung.backup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import dev.yunsung.util.HttpUtil;

public class Confluence implements BackupStrategy {

	private final String space = System.getenv("CONFLUENCE_SPACE");
	private final String parentId = System.getenv("CONFLUENCE_PARENT_ID");
	private final String baseUri = "https://" + System.getenv("CONFLUENCE_DOMAIN") + ".atlassian.net/wiki";
	private final Map<String, String> headers = Map.of(
		"Authorization", "Basic " + Base64.getEncoder().encodeToString(
			(System.getenv("CONFLUENCE_EMAIL") + ":" + System.getenv("CONFLUENCE_API_TOKEN"))
				.getBytes(StandardCharsets.UTF_8)
		)
	);

	@Override
	public String backup(String title, String content) throws IOException, InterruptedException {
		var spaceId = HttpUtil.get(baseUri + "/rest/api/space?spaceKey=" + space, headers)
			.get("results").get(0).get("id");

		// Markdown -> HTML
		Parser parser = Parser.builder().build();
		Node document = parser.parse(content);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		String html = renderer.render(document);

		var jsonNode = HttpUtil.post(
			baseUri + "/api/v2/pages",
			Map.of(
				"spaceId", spaceId,
				"title", title,
				"parentId", parentId,
				"body", Map.of(
					"representation", "storage",
					"value", html
				)
			),
			headers
		);

		var links = jsonNode.get("_links");
		return links.get("base").asText() + links.get("webui").asText();
	}
}
