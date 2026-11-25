package com.secops.service;

import com.secops.client.PresidioClient;
import com.secops.client.PresidioClient.Detection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class PiiTokenizationService {
	
	private final PresidioClient presidio;

	public PiiTokenizationService(PresidioClient presidio) {
		this.presidio = presidio;
	}

	@SuppressWarnings("null")
	public Tokenized tokenize(String input) {
		List<Detection> detections = removeOverlaps(presidio.analyze(input));
		StringBuilder out = new StringBuilder(input);
		Map<String, String> vault = new LinkedHashMap<>();
		Map<String, Integer> counts = new HashMap<>();
		
		List<Detection> reverse = new ArrayList<>(detections);
		reverse.sort(Comparator.comparingInt(Detection::start).reversed());
		
		for (Detection detection : reverse) {
			int n = counts.merge(detection.entity_type(), 1, Integer::sum);
			String token = "<PII_" + safeType(detection.entity_type()) + "_" + n + ">";
			String original = input.substring(detection.start(), detection.end());
			out.replace(detection.start(), detection.end(), token);
			vault.put(token, original);
		}
		
		return new Tokenized(out.toString(), Map.copyOf(vault),
				detections.stream().map(Detection::entity_type).distinct().toList());
	}

	public String restore(String text, Map<String, String> vault) {
		String r = text;
		
		for (var e : vault.entrySet()) {
			r = r.replace(e.getKey(), e.getValue());
		}
		
		return r;
	}

	private List<Detection> removeOverlaps(List<Detection> in) {
		List<Detection> sorted = new ArrayList<>(in);
		sorted.sort(Comparator.comparingDouble(Detection::score).reversed());
		
		List<Detection> keep = new ArrayList<Detection>();
		for (Detection detection : sorted) {
			if (keep.stream().noneMatch(x -> detection.start() < x.end() && x.start() < detection.end())) {
				keep.add(detection);
			}
		}
		
		return keep;
	}

	private String safeType(String s) {
		return s.replaceAll("[^A-Za-z0-9_]", "_");
	}

	public record Tokenized(String text, Map<String, String> vault, List<String> categories) {
	}
	
}
