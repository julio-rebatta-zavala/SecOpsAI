package com.secops.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "rag_documents")
public class RagDocument {
	
	@Id
	private UUID id;
	
	@Column(name = "original_name")
	private String originalName;
	
	@Column(name = "stored_name")
	private String storedName;
	
	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "content_sha256", unique = true, length = 64, columnDefinition = "CHAR(64)")
	private String contentSha256;
	
	@Column(name = "mime_type")
	private String mimeType;
	
	@Column(name = "size_bytes")
	private long sizeBytes;
	
	@Enumerated(EnumType.STRING)
	private DocumentStatus status;
	
	@Enumerated(EnumType.STRING)
	private DocumentSource source;
	
	private int chunks;
	
	@Column(name = "rejection_reason")
	private String rejectionReason;
	
	@Column(name = "uploaded_by")
	private UUID uploadedBy;
	
	@Column(name = "created_at")
	private Instant createdAt;
	
	@Column(name = "updated_at")
	private Instant updatedAt;

	public RagDocument() {
		super();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getStoredName() {
		return storedName;
	}

	public void setStoredName(String storedName) {
		this.storedName = storedName;
	}

	public String getContentSha256() {
		return contentSha256;
	}

	public void setContentSha256(String contentSha256) {
		this.contentSha256 = contentSha256;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public long getSizeBytes() {
		return sizeBytes;
	}

	public void setSizeBytes(long sizeBytes) {
		this.sizeBytes = sizeBytes;
	}

	public DocumentStatus getStatus() {
		return status;
	}

	public void setStatus(DocumentStatus status) {
		this.status = status;
	}

	public DocumentSource getSource() {
		return source;
	}

	public void setSource(DocumentSource source) {
		this.source = source;
	}

	public int getChunks() {
		return chunks;
	}

	public void setChunks(int chunks) {
		this.chunks = chunks;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public UUID getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(UUID uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	@PrePersist
	void create() {
		id = id == null ? UUID.randomUUID() : id;
		createdAt = updatedAt = Instant.now();
	}

	@PreUpdate
	void update() {
		updatedAt = Instant.now();
	}
}
