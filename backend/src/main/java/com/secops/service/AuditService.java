package com.secops.service;

import com.secops.entity.AuditEvent;
import com.secops.repository.AuditEventRepository;
import com.secops.security.SecOpsPrincipal;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;

@Service
public class AuditService {
	
	private final AuditEventRepository repo;
	private final HashingService hash;

	public AuditService(AuditEventRepository auditEventRepository, HashingService hashingService) {
		this.repo = auditEventRepository;
		this.hash = hashingService;
	}

	@Transactional
	public void record(SecOpsPrincipal actor, String action, String type, String id, String decision, String categories,
			String safeDetail) {
		AuditEvent event = new AuditEvent();
		event.setOccurredAt(Instant.now());
		event.setCorrelationId(Optional.ofNullable(MDC.get("correlationId")).orElse("system"));
		
		if (actor != null) {
			event.setActorId(actor.userId());
			event.setActorUsername(actor.username());
			event.setActorRoles(actor.roles().toString());
		}
		
		event.setAction(action);
		event.setResourceType(type);
		event.setResourceId(id);
		event.setDecision(decision);
		event.setCategories(categories);
		event.setDetailSafe(sanitize(safeDetail));
		event.setEventHash(hash.sha256(event.getCorrelationId() + "|" + action + "|" + decision + "|" + event.getOccurredAt()));
		repo.save(event);
	}

	@SuppressWarnings("null")
	public Page<AuditEvent> list(Pageable pageable) {
		return repo.findAll(pageable);
	}

	private String sanitize(String text) {
		if (text == null) {
			return null;
		}
		
		String clean = text.replaceAll("(?i)(bearer\\s+|api[_-]?key[=:]\\s*)[^\\s,;]+", "$1[REDACTED]")
				.replaceAll("[\\r\\n]", " ");
		
		return clean.substring(0, Math.min(1000, clean.length()));
	}
	
}
