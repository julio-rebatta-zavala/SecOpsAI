package com.secops.service;

import com.secops.dto.ConversationDtos.ChatResponse;
import com.secops.dto.ConversationDtos.ConversationSummary;
import com.secops.dto.ConversationDtos.ConversationView;
import com.secops.dto.ConversationDtos.MessageView;
import com.secops.entity.Conversation;
import com.secops.entity.Message;
import com.secops.entity.MessageActor;
import com.secops.entity.Role;
import com.secops.exception.NotFoundException;
import com.secops.exception.PolicyViolationException;
import com.secops.guardrail.PolicyGuardrailService;
import com.secops.mapper.ConversationMapper;
import com.secops.orchestration.SecOpsOrchestrator;
import com.secops.orchestration.SecOpsOrchestrator.Result;
import com.secops.repository.ConversationRepository;
import com.secops.repository.MessageRepository;
import com.secops.security.SecOpsPrincipal;
import com.secops.service.PiiTokenizationService.Tokenized;

import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ConversationService {

	private final ConversationRepository conversationRepository;
	private final MessageRepository messageRepository;
	private final ConversationMapper mapper;
	private final PolicyGuardrailService policy;
	private final PiiTokenizationService piiService;
	private final SecOpsOrchestrator orchestrator;
	private final EncryptionService cryptoService;
	private final HashingService hashingService;
	private final AuditService auditService;

	public ConversationService(ConversationRepository conversationRepository, MessageRepository messageRepository,
			ConversationMapper mapper, PolicyGuardrailService policy, PiiTokenizationService piiService,
			SecOpsOrchestrator orchestrator, EncryptionService cryptoService, HashingService hashingService,
			AuditService auditService) {
		this.conversationRepository = conversationRepository;
		this.messageRepository = messageRepository;
		this.mapper = mapper;
		this.policy = policy;
		this.piiService = piiService;
		this.orchestrator = orchestrator;
		this.cryptoService = cryptoService;
		this.hashingService = hashingService;
		this.auditService = auditService;
	}

	@Transactional
	public ConversationSummary create(SecOpsPrincipal p, String title) {
		Conversation c = new Conversation();
		c.setOwnerId(p.userId());
		c.setTitle(title.strip());
		c = conversationRepository.save(c);

		auditService.record(p, "CONVERSATION_CREATED", "CONVERSATION", c.getId().toString(), "ALLOW", "",
				"Conversación creada");

		return mapper.toSummary(c);
	}

	public Page<ConversationSummary> listOwn(SecOpsPrincipal p, Pageable page) {
		return conversationRepository.findByOwnerId(p.userId(), page).map(mapper::toSummary);
	}

	public ConversationView getOwn(SecOpsPrincipal p, UUID id) {
		var c = authorize(p, id);
		return view(c);
	}

	@SuppressWarnings("null")
	public ConversationView getForAudit(SecOpsPrincipal p, UUID id) {
		Conversation c = conversationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Conversación no encontrada"));

		auditService.record(p, "AUDIT_READ", "CONVERSATION", id.toString(), "ALLOW", "AUDIT",
				"Acceso auditable a conversación");
		return view(c);
	}

	public ChatResponse send(SecOpsPrincipal principal, UUID conversationId, String raw) {
		authorize(principal, conversationId);

		final String safe;
		try {
			safe = policy.validate(raw);
		} catch (PolicyViolationException e) {
			auditService.record(principal, "INPUT_BLOCKED", "CONVERSATION", conversationId.toString(), "DENY", e.code(),
					"Entrada bloqueada antes de herramientas");
			throw e;
		}

		Tokenized tokenized = piiService.tokenize(safe);
		auditService.record(principal, "PII_TOKENIZED", "CONVERSATION", conversationId.toString(), "ALLOW",
				String.join(",", tokenized.categories()), "PII types=" + tokenized.categories().size());
		save(conversationId, MessageActor.USER, raw, "APPROVED");

		Result result = orchestrator.answer(tokenized.text());
		String restored = piiService.restore(result.answer(), tokenized.vault());
		Message answer = save(conversationId, MessageActor.ASSISTANT, restored, "APPROVED");
		
		auditService.record(principal, "RAG_QUERY", "CONVERSATION", conversationId.toString(), "ALLOW", "RAG",
				"Consulta ejecutada con texto tokenizado");
		
		if (result.sources().stream().anyMatch(s -> s.type().equals("NVD"))
				|| result.degradedSources().stream().anyMatch(s -> s.startsWith("NVD"))) {
			auditService.record(principal, "NVD_CALL", "CONVERSATION", conversationId.toString(), "ALLOW", "NVD",
					"Solo se envió un identificador CVE validado");
		}
		
		auditService.record(principal, "MODEL_CALL", "CONVERSATION", conversationId.toString(), "ALLOW", "MODEL",
				"Payload tokenizado; sources=" + result.sources().size());
		auditService.record(principal, "ORCHESTRATION_COMPLETED", "CONVERSATION", conversationId.toString(), "ALLOW",
				"RAG,NVD,MODEL", "sources=" + result.sources().size() + ", degraded=" + result.degradedSources());
		
		return new ChatResponse(answer.getId(), restored, result.sources(), result.degradedSources(),
				MDC.get("correlationId"));
	}

	@SuppressWarnings("null")
	private Conversation authorize(SecOpsPrincipal principal, UUID id) {
		if (principal.roles().contains(Role.ADMIN)) {
			return conversationRepository.findById(id)
					.orElseThrow(() -> new NotFoundException("Conversación no encontrada"));
		}
		
		return conversationRepository.findByIdAndOwnerId(id, principal.userId())
				.orElseThrow(() -> new NotFoundException("Conversación no encontrada"));
	}

	private Message save(UUID id, MessageActor actor, String content, String decision) {
		Message message = new Message();
		message.setConversationId(id);
		message.setActor(actor);
		message.setContentCiphertext(cryptoService.encrypt(content));
		message.setContentSha256(hashingService.sha256(content));
		message.setPolicyDecision(decision);
		return messageRepository.save(message);
	}

	private ConversationView view(Conversation c) {
		List<MessageView> list = messageRepository.findByConversationIdOrderByCreatedAtAsc(c.getId()).stream()
				.map(m -> new MessageView(m.getId(), m.getActor().name(),
						cryptoService.decrypt(m.getContentCiphertext()), m.getCreatedAt(), m.getPolicyDecision()))
				.toList();
		
		return new ConversationView(c.getId(), c.getOwnerId(), c.getTitle(), list);
	}

	@SuppressWarnings("null")
	public Page<ConversationSummary> listAll(Pageable pageable) {
		return conversationRepository.findAll(pageable).map(mapper::toSummary);
	}
	
}
