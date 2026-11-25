package com.secops.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ConversationDtos {
	
    private ConversationDtos() {
    }

    public record CreateConversationRequest(@NotBlank @Size(max = 160) String title) {
    }

    public record SendMessageRequest(@NotBlank @Size(max = 8000) String content) {
    }

    public record ConversationSummary(UUID id, String title, Instant createdAt, Instant updatedAt) {
    }

    public record MessageView(UUID id, String actor, String content, Instant createdAt, String policyDecision) {
    }

    public record ConversationView(UUID id, UUID ownerId, String title, List<MessageView> messages) {
    }

    public record SourceView(String type, String name, String reference, double score) {
    }

    public record ChatResponse(UUID messageId, String answer, List<SourceView> sources, List<String> degradedSources,
            String correlationId) {
    }
    
}
