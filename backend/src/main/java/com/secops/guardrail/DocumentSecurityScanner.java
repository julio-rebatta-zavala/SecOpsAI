package com.secops.guardrail;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DocumentSecurityScanner {
	
    private static final List<String> BAD = List.of("ignore previous instructions",
            "ignora las instrucciones anteriores", "reveal the system prompt", "revela el prompt del sistema",
            "send secrets to", "webhook.site", "curl http", "BEGIN PRIVATE KEY", "exfiltrate");

    public ScanResult scan(String text) {
        String l = text.toLowerCase(Locale.ROOT);
        List<String> hits = BAD.stream().filter(x -> l.contains(x.toLowerCase(Locale.ROOT))).toList();
        return new ScanResult(hits.isEmpty(), hits);
    }

    public record ScanResult(boolean safe, List<String> reasons) {
    }
    
}
