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
@Table(name = "refresh_tokens")
public class RefreshToken {
	
	@Id
	private UUID id;
	
	@Column(name = "user_id")
	private UUID userId;
	
	@Column(name = "family_id")
	private UUID familyId;
	
	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "token_hash", unique = true, length = 64, columnDefinition = "CHAR(64)")
	private String tokenHash;
	
	@Column(name = "expires_at")
	private Instant expiresAt;
	
	@Column(name = "revoked_at")
	private Instant revokedAt;
	
	@Column(name = "replaced_by")
	private UUID replacedBy;
	
	@Column(name = "created_at")
	private Instant createdAt;

	public RefreshToken() {
		super();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public UUID getFamilyId() {
		return familyId;
	}

	public void setFamilyId(UUID familyId) {
		this.familyId = familyId;
	}

	public String getTokenHash() {
		return tokenHash;
	}

	public void setTokenHash(String tokenHash) {
		this.tokenHash = tokenHash;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Instant getRevokedAt() {
		return revokedAt;
	}

	public void setRevokedAt(Instant revokedAt) {
		this.revokedAt = revokedAt;
	}

	public UUID getReplacedBy() {
		return replacedBy;
	}

	public void setReplacedBy(UUID replacedBy) {
		this.replacedBy = replacedBy;
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
