package com.secops.client;

import com.secops.config.SecOpsProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class QdrantClient {

    private final RestClient client;
    private final SecOpsProperties.Qdrant cfg;

    @SuppressWarnings("null")
	public QdrantClient(RestClient.Builder builder, SecOpsProperties properties) {
        this.cfg = properties.qdrant();
        this.client = builder.clone().baseUrl(cfg.url()).build();
    }

    @SuppressWarnings("null")
	@CircuitBreaker(name = "qdrant")
    public void ensureCollection() {
        try {
            client.get()
                .uri("/collections/{c}", cfg.collection())
                .retrieve()
                .toBodilessEntity();
        } catch (Exception e) {
            client.put()
                .uri("/collections/{c}", cfg.collection())
                .body(Map.of("vectors", Map.of("size", cfg.dimensions(), "distance", "Cosine")))
                .retrieve()
                .toBodilessEntity();
        }
    }

    @SuppressWarnings("null")
	@CircuitBreaker(name = "qdrant")
    public void upsert(UUID docId, List<Point> points) {
        ensureCollection();
        client.put()
            .uri("/collections/{c}/points?wait=true", cfg.collection())
            .body(Map.of("points", points))
            .retrieve()
            .toBodilessEntity();
    }

    @SuppressWarnings({ "null", "unchecked" })
	@CircuitBreaker(name = "qdrant")
    public List<Hit> search(float[] vector, int limit, double threshold) {
        List<Double> v = new ArrayList<>();
        for (float x : vector) {
            v.add((double) x);
        }
        
		Map<?, ?> r = client.post()
                        .uri("/collections/{c}/points/search", cfg.collection())
                        .body(Map.of("vector", v, "limit", limit, "score_threshold", threshold, "with_payload", true))
                        .retrieve()
                        .body(Map.class);

        if (r == null || !(r.get("result") instanceof List<?> rows)) {
            return List.of();
        }

        List<Hit> out = new ArrayList<Hit>();
        for (Object o : rows) {
            Map<?, ?> m = (Map<?, ?>) o;
            out.add(new Hit(((Number) m.get("score")).doubleValue(), (Map<String, Object>) m.get("payload")));
        }

        return out;
    }

    @SuppressWarnings("null")
	@CircuitBreaker(name = "qdrant")
    public void deleteDocument(UUID docId) {
        client.post()
                .uri("/collections/{c}/points/delete?wait=true", cfg.collection())
                .body(Map.of("filter", Map.of("must", List.of(Map.of("key", "documentId", "match", Map.of("value", docId.toString()))))))
                .retrieve()
                .toBodilessEntity();
    }

    public record Point(String id, List<Double> vector, Map<String, Object> payload) {
    }

    public record Hit(double score, Map<String, Object> payload) {
    }
    
}
