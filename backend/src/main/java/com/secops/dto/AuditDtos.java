package com.secops.dto;

import java.time.Instant;
import java.util.UUID;

public final class AuditDtos {
	
    private AuditDtos() {
    }

    public record AuditEventView(UUID id, Instant occurredAt, String correlationId, String actor, String roles,
            String action, String resourceType, String resourceId, String decision, String categories,
            String detailSafe) {
    }
    
}
