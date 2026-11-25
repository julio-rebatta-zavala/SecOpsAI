package com.secops.client;

import com.secops.config.SecOpsProperties;
import com.secops.exception.DependencyUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PresidioClient {
    
    private final RestClient analyzer, anonymizer;
    private final double threshold;

    @SuppressWarnings("null")
	public PresidioClient(RestClient.Builder builder, SecOpsProperties properties) {
        analyzer = builder.clone().baseUrl(properties.presidio().analyzerUrl()).build();
        anonymizer = builder.clone().baseUrl(properties.presidio().anonymizerUrl()).build();
        threshold = properties.presidio().threshold();
    }

    @SuppressWarnings("null")
	@CircuitBreaker(name = "presidio")
    @Retry(name = "presidio")
    public List<Detection> analyze(String text) {
        try {
            List<Map<String, Object>> rows = analyzer.post().uri("/analyze")
                    .body(Map.of("text", text, "language", "en", "score_threshold", threshold)).retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            /*
            if (rows == null) {
                return List.of();
            }
            */

            return rows.stream()
                    .map(x -> new Detection(String.valueOf(x.get("entity_type")), ((Number) x.get("start")).intValue(),
                            ((Number) x.get("end")).intValue(), ((Number) x.get("score")).doubleValue()))
                    .sorted(Comparator.comparingInt(Detection::start)).toList();
        } catch (Exception e) {
            throw new DependencyUnavailableException("Presidio analyzer no disponible", e);
        }
    }

    @SuppressWarnings("null")
	public String anonymizedPreview(String text) {
        try {
            Map<?, ?> r = anonymizer.post().uri("/anonymize")
                    .body(Map.of("text", text, "analyzer_results", analyze(text), "operators",
                            Map.of("DEFAULT", Map.of("type", "replace", "new_value", "<PII>"))))
                    .retrieve().body(Map.class);
            return r == null ? "" : String.valueOf(r.get("text"));
        } catch (Exception e) {
            return "[preview unavailable]";
        }
    }

    public record Detection(String entity_type, int start, int end, double score) {
    }

}
