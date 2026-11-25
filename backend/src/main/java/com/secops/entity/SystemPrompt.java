package com.secops.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_prompts")
public class SystemPrompt {
	
	@Id
	private UUID id;
	
	@Column(name = "version_no", unique = true)
	private int versionNo;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "content_sha256", length = 64, columnDefinition = "CHAR(64)")
	private String contentSha256;
	
	private boolean active;
	
	@Column(name = "created_by")
	private UUID createdBy;
	
	@Column(name = "created_at")
	private Instant createdAt;

	public SystemPrompt() {
		super();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public int getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentSha256() {
		return contentSha256;
	}

	public void setContentSha256(String contentSha256) {
		this.contentSha256 = contentSha256;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public UUID getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UUID createdBy) {
		this.createdBy = createdBy;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	@PrePersist
	void create() {
		id = id == null ? UUID.randomUUID() : id;
		createdAt = Instant.now();
	}
	
}
