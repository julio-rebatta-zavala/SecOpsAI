package com.secops.controller;

import com.secops.dto.AdminDtos.PromptRequest;
import com.secops.dto.AdminDtos.PromptView;
import com.secops.entity.SystemPrompt;
import com.secops.security.SecOpsPrincipal;
import com.secops.security.SecurityContextService;
import com.secops.service.AuditService;
import com.secops.service.SystemPromptService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping ("/api/v1/admin/system-prompt")
@PreAuthorize ("hasRole('ADMIN')")
public class AdminPromptController {

    private final SystemPromptService svc;
    private final SecurityContextService ctx;
    private final AuditService audit;

    public AdminPromptController(SystemPromptService systemPromptService, SecurityContextService securityContextService, AuditService auditService) {
        this.svc = systemPromptService;
        this.ctx = securityContextService;
        this.audit = auditService;
    }

    @GetMapping 
    PromptView current() {
        SystemPrompt principal = svc.current();
        return new PromptView(principal.getId(), principal.getVersionNo(), principal.getContent(), principal.getContentSha256(), principal.getCreatedAt());
    }

    @PutMapping 
    PromptView update(@Valid @RequestBody PromptRequest request) {
        SecOpsPrincipal actor = ctx.current();
        SystemPrompt prompt = svc.update(request.content(), actor.userId());
        audit.record(actor, "PROMPT_CHANGED", "SYSTEM_PROMPT", prompt.getId().toString(), "ALLOW", "ADMIN",
                "version=" + prompt.getVersionNo());
        return new PromptView(prompt.getId(), prompt.getVersionNo(), prompt.getContent(), prompt.getContentSha256(), prompt.getCreatedAt());
    }

}
