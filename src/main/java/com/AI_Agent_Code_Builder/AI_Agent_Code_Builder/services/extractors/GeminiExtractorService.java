package com.AI_Agent_Code_Builder.AI_Agent_Code_Builder.services.extractors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class GeminiExtractorService {

    private final String apiUrl;
    private final List<String> apiKeys;
    private final RestClient.Builder restClientBuilder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GeminiExtractorService(
            RestClient.Builder restClientBuilder,
            @Value("${gemini.api.api-url}") String apiUrl,
            @Value("${gemini.api.api-key}") String apiKeys // comma-separated keys
    ) {
        this.apiUrl = apiUrl;
        this.apiKeys = Arrays.asList(apiKeys.split(","));
        this.restClientBuilder = restClientBuilder;
    }

    public String extractFromPrompt(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        for (String key : apiKeys) {
            try {
                String urlWithKey = apiUrl + "?key=" + key.trim();

                RestClient restClient = restClientBuilder
                        .baseUrl(urlWithKey)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build();

                Map<String, Object> response = restClient.post()
                        .body(requestBody)
                        .retrieve()
                        .body(Map.class);

                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    return (String) parts.get(0).get("text");
                }

            } catch (Exception e) {
                // Log or continue to next key
                logger.info("Key failed: " + key.trim() + " -> " + e.getMessage());
            }
        }

        return "All Gemini API keys failed.";
    }
}
