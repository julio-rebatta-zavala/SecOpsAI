package com.secops.client;

import com.secops.config.SecOpsProperties;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NvdClient {

    private final RestClient client;

    @SuppressWarnings("null")
	public NvdClient(RestClient.Builder builder, SecOpsProperties properties) {
        RestClient.Builder spec = builder.clone().baseUrl(properties.nvd().url());

        if (properties.nvd().apiKey() != null && !properties.nvd().apiKey().isBlank()) {
            spec.defaultHeader("apiKey", properties.nvd().apiKey());
        }

        client = spec.build();
    }

    @SuppressWarnings("unchecked")
	@CircuitBreaker(name = "nvd")
    @Retry(name = "nvd")
    @RateLimiter(name = "nvd")
    @Bulkhead(name = "nvd")
    public Map<String, Object> getCve(String id) {
        return client.get()
                        .uri(u -> u.path("/rest/json/cves/2.0")
                        .queryParam("cveId", id)
                        .build())
                    .retrieve()
                    .body(Map.class);
    }

}
