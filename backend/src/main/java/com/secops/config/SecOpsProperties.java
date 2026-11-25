package com.secops.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("secops")
public record SecOpsProperties(Jwt jwt, String encryptionKey, Qdrant qdrant, Nvd nvd, Presidio presidio,
        String corsOrigins, boolean seedDemoUsers, Policy policy) {

    public record Jwt(String secret, String issuer, String audience, long accessMinutes, long refreshHours) {
    }

    public record Qdrant(String url, String collection, int dimensions) {
    }

    public record Nvd(String url, String apiKey) {
    }

    public record Presidio(String analyzerUrl, String anonymizerUrl, double threshold) {
    }

    public record Policy(int maxInputChars, String protectedTerms, String injectionPatterns) {
        public List<String> protectedTermList() {
            return split(protectedTerms);
        }

        public List<String> injectionPatternList() {
            return split(injectionPatterns);
        }

        @SuppressWarnings("null")
        private static List<String> split(String s) {
            return s == null ? List.of()
                    : Arrays.stream(s.split(",")).map(String::trim).filter(x -> !x.isBlank()).toList();
        }
    }
    
}
