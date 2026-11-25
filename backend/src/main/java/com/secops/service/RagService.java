package com.secops.service;

import com.secops.client.QdrantClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class RagService {

	private final EmbeddingModel embeddings;
	private final QdrantClient qdrant;

	public RagService(EmbeddingModel embeddings, QdrantClient qdrant) {
		this.embeddings = embeddings;
		this.qdrant = qdrant;
	}

	@SuppressWarnings("null")
	@CircuitBreaker(name = "ai")
	@Bulkhead(name = "ai")
	public List<ContextChunk> retrieve(String query) {
		float[] v = embeddings.embed(query);
		return qdrant.search(v, 6, 0.72).stream().map(h -> new ContextChunk(String.valueOf(h.payload().get("text")),
				String.valueOf(h.payload().get("source")), h.score())).toList();
	}

	@SuppressWarnings("null")
	public void index(UUID doc, String source, List<String> chunks) {
		List<QdrantClient.Point> points = new ArrayList<QdrantClient.Point>();
		
		for (int i = 0; i < chunks.size(); i++) {
			float[] f = embeddings.embed(chunks.get(i));
			List<Double> vec = new ArrayList<Double>();
			
			for (float x : f) {
				vec.add((double) x);
			}
			
			points.add(new QdrantClient.Point(UUID.randomUUID().toString(), vec, Map.of("documentId", doc.toString(),
					"chunk", i, "source", source, "classification", "INTERNAL", "text", chunks.get(i))));
		}
		qdrant.upsert(doc, points);
	}

	public void delete(UUID doc) {
		qdrant.deleteDocument(doc);
	}

	public record ContextChunk(String text, String source, double score) {
	}

}
