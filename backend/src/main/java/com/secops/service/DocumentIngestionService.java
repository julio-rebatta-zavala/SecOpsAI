package com.secops.service;

import com.secops.dto.AdminDtos.DocumentView;
import com.secops.entity.DocumentSource;
import com.secops.entity.DocumentStatus;
import com.secops.entity.RagDocument;
import com.secops.exception.ConflictException;
import com.secops.exception.DependencyUnavailableException;
import com.secops.exception.NotFoundException;
import com.secops.exception.PolicyViolationException;
import com.secops.guardrail.DocumentSecurityScanner;
import com.secops.guardrail.DocumentSecurityScanner.ScanResult;
import com.secops.mapper.DocumentMapper;
import com.secops.repository.RagDocumentRepository;
import com.secops.security.SecOpsPrincipal;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import jakarta.annotation.PostConstruct;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentIngestionService {

	private static final Set<String> TYPES = Set.of("text/markdown", "text/plain", "application/pdf",
			"application/octet-stream");
	private final RagDocumentRepository ragRepository;
	private final HashingService hashingService;
	private final DocumentSecurityScanner scanner;
	private final RagService ragService;
	private final DocumentMapper mapper;
	private final AuditService auditService;

	public DocumentIngestionService(RagDocumentRepository ragRepository, HashingService hashingService,
			DocumentSecurityScanner scanner, RagService ragService, DocumentMapper mapper, AuditService auditService) {
		this.ragRepository = ragRepository;
		this.hashingService = hashingService;
		this.scanner = scanner;
		this.ragService = ragService;
		this.mapper = mapper;
		this.auditService = auditService;
	}

	@PostConstruct
	void startup() {
		try {
			for (Resource resource : new PathMatchingResourcePatternResolver().getResources("classpath:/docs/*.md")) {
				ingest(resource.getFilename(), "text/markdown", resource.getInputStream().readAllBytes(),
						DocumentSource.STARTUP, null, false);
			}
		} catch (Exception e) {
			throw new IllegalStateException("Falló ingesta automática", e);
		}
	}

	@SuppressWarnings("null")
	public Page<DocumentView> list(Pageable pageable) {
		return ragRepository.findAll(pageable).map(mapper::toView);
	}

	@SuppressWarnings("null")
	@Transactional
	public DocumentView upload(SecOpsPrincipal actor, MultipartFile file) {
		try {
			String name = Optional.ofNullable(file.getOriginalFilename()).orElse("document");

			if (file.isEmpty() || file.getSize() > 5 * 1024 * 1024) {
				throw new PolicyViolationException("INVALID_FILE", "Archivo vacío o mayor a 5 MiB.");
			}

			if (!name.toLowerCase(Locale.ROOT).matches(".*\\.(md|txt|pdf)$")
					|| !TYPES.contains(Optional.ofNullable(file.getContentType()).orElse("application/octet-stream"))) {
				throw new PolicyViolationException("INVALID_FILE_TYPE", "Solo se permiten MD, TXT y PDF.");
			}

			return mapper.toView(ingest(name, file.getContentType(), file.getBytes(), DocumentSource.MANUAL, actor, true));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("null")
	@Transactional
	public void delete(SecOpsPrincipal actor, UUID id) {
		RagDocument document = ragRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Documento no encontrado"));
		ragService.delete(document.getId());

		document.setStatus(DocumentStatus.DELETED);
		document.setChunks(0);

		ragRepository.save(document);
		auditService.record(actor, "DOCUMENT_DELETED", "RAG_DOCUMENT", id.toString(), "ALLOW", "RAG",
				"Vectores eliminados y baja lógica aplicada");
	}

	@Transactional
	public RagDocument ingest(String name, String mime, byte[] bytes, DocumentSource source, SecOpsPrincipal actor,
			boolean showError) {
		String digest = hashingService.sha256(bytes);
		if (ragRepository.findByContentSha256(digest).isPresent()) {
			if (showError) {
				auditService.record(actor, "DOCUMENT_DUPLICATE", "RAG_DOCUMENT", null, "DENY", "SHA256",
						"Documento duplicado");
				throw new ConflictException("El documento ya fue cargado (mismo SHA-256).");
			} else {
				return null;
			}
		}

		RagDocument document = new RagDocument();
		document.setOriginalName(name);
		document.setStoredName(UUID.randomUUID() + extension(name));
		document.setContentSha256(digest);
		document.setMimeType(mime == null ? "application/octet-stream" : mime);
		document.setSizeBytes(bytes.length);
		document.setSource(source);
		document.setStatus(DocumentStatus.PENDING);

		if (actor != null) {
			document.setUploadedBy(actor.userId());
		}

		document = ragRepository.save(document);

		try {
			String text = parse(bytes);
			ScanResult scan = scanner.scan(text);

			if (!scan.safe()) {
				document.setStatus(DocumentStatus.QUARANTINED);
				document.setRejectionReason(String.join(",", scan.reasons()));
				auditService.record(actor, "DOCUMENT_QUARANTINED", "RAG_DOCUMENT", document.getId().toString(), "DENY",
						"INDIRECT_PROMPT_INJECTION", "Documento en cuarentena");
				return ragRepository.save(document);
			}

			Document lc = dev.langchain4j.data.document.Document.from(text);
			List<String> chunks = DocumentSplitters.recursive(1200, 200).split(lc).stream().map(x -> x.text())
					.filter(x -> !x.isBlank()).toList();

			ragService.index(document.getId(), name, chunks);
			document.setChunks(chunks.size());
			document.setStatus(DocumentStatus.INDEXED);
			auditService.record(actor, "DOCUMENT_INDEXED", "RAG_DOCUMENT", document.getId().toString(), "ALLOW", "RAG",
					"chunks=" + chunks.size());
			return ragRepository.save(document);
		} catch (Exception e) {
			document.setStatus(DocumentStatus.FAILED);
			document.setRejectionReason("Error de procesamiento seguro");
			ragRepository.save(document);
			throw new DependencyUnavailableException("No se pudo indexar el documento", e);
		}
	}

	private String parse(byte[] bytes) {
		try {
			return new ApacheTikaDocumentParser().parse(new ByteArrayInputStream(bytes)).text();
		} catch (Exception e) {
			return new String(bytes, StandardCharsets.UTF_8);
		}
	}

	private String extension(String extension) {
		int i = extension.lastIndexOf('.');
		return i < 0 ? "" : extension.substring(i).toLowerCase(Locale.ROOT);
	}

}
