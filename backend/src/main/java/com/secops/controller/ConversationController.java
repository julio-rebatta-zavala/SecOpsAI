package com.secops.controller;

import com.secops.dto.ConversationDtos.ChatResponse;
import com.secops.dto.ConversationDtos.ConversationSummary;
import com.secops.dto.ConversationDtos.ConversationView;
import com.secops.dto.ConversationDtos.CreateConversationRequest;
import com.secops.dto.ConversationDtos.SendMessageRequest;
import com.secops.security.SecurityContextService;
import com.secops.service.ConversationService;
import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conversations")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class ConversationController {
	
	private final ConversationService svc;
	private final SecurityContextService ctx;

	public ConversationController(ConversationService service, SecurityContextService context) {
		svc = service;
		ctx = context;
	}

	@PostMapping
	ConversationSummary create(@Valid @RequestBody CreateConversationRequest request) {
		return svc.create(ctx.current(), request.title());
	}

	@GetMapping
	Page<ConversationSummary> list(
			@PageableDefault(size = 20, sort = "updatedAt", direction = Direction.DESC) Pageable pageable) {
		return svc.listOwn(ctx.current(), pageable);
	}

	@GetMapping("/{id}")
	ConversationView get(@PathVariable UUID id) {
		return svc.getOwn(ctx.current(), id);
	}

	@PostMapping("/{id}/messages")
	ChatResponse send(@PathVariable UUID id, @Valid @RequestBody SendMessageRequest request) {
		return svc.send(ctx.current(), id, request.content());
	}

}
