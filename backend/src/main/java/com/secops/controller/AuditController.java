package com.secops.controller;

import com.secops.dto.AuditDtos.AuditEventView;
import com.secops.dto.ConversationDtos.ConversationSummary;
import com.secops.dto.ConversationDtos.ConversationView;
import com.secops.entity.AuditEvent;
import com.secops.security.SecurityContextService;
import com.secops.service.AuditService;
import com.secops.service.ConversationService;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit")
@PreAuthorize("hasAnyRole('AUDIT','ADMIN')")
public class AuditController {
	
	private final AuditService audit;
	private final ConversationService conversations;
	private final SecurityContextService ctx;

	public AuditController(AuditService auditService, ConversationService conversationService, SecurityContextService context) {
		audit = auditService;
		conversations = conversationService;
		ctx = context;
	}

	@GetMapping("/events")
	Page<AuditEventView> events(
			@PageableDefault(size = 50, sort = "occurredAt", direction = Direction.DESC) Pageable pageable) {
		return audit.list(pageable).map(this::view);
	}

	@GetMapping("/application-logs")
	Page<AuditEventView> logs(
			@PageableDefault(size = 50, sort = "occurredAt", direction = Direction.DESC) Pageable pageable) {
		return audit.list(pageable).map(this::view);
	}

	@GetMapping("/conversations")
	Page<ConversationSummary> conversations(
			@PageableDefault(size = 50, sort = "updatedAt", direction = Direction.DESC) Pageable pageable) {
		return conversations.listAll(pageable);
	}

	@GetMapping("/conversations/{id}")
	ConversationView conversation(@PathVariable UUID id) {
		return conversations.getForAudit(ctx.current(), id);
	}

	private AuditEventView view(AuditEvent event) {
		return new AuditEventView(event.getId(), event.getOccurredAt(), event.getCorrelationId(), event.getActorUsername(),
				event.getActorRoles(), event.getAction(), event.getResourceType(), event.getResourceId(), event.getDecision(),
				event.getCategories(), event.getDetailSafe());
	}
	
}
