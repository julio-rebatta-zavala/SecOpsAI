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
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {

	@Id
	private UUID id;

	@Column(name = "conversation_id", nullable = false)
	private UUID conversationId;

	@Enumerated(EnumType.STRING)
	private MessageActor actor;

	@Column(name = "content_ciphertext", columnDefinition = "TEXT")
	private String contentCiphertext;

	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "content_sha256", nullable = false, length = 64, columnDefinition = "CHAR(64)")
	private String contentSha256;

	@Column(name = "policy_decision")
	private String policyDecision;

	@Column(name = "created_at")
	private Instant createdAt;

	public Message() {
		super();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getConversationId() {
		return conversationId;
	}

	public void setConversationId(UUID conversationId) {
		this.conversationId = conversationId;
	}

	public MessageActor getActor() {
		return actor;
	}

	public void setActor(MessageActor actor) {
		this.actor = actor;
	}

	public String getContentCiphertext() {
		return contentCiphertext;
	}

	public void setContentCiphertext(String contentCiphertext) {
		this.contentCiphertext = contentCiphertext;
	}

	public String getContentSha256() {
		return contentSha256;
	}

	public void setContentSha256(String contentSha256) {
		this.contentSha256 = contentSha256;
	}

	public String getPolicyDecision() {
		return policyDecision;
	}

	public void setPolicyDecision(String policyDecision) {
		this.policyDecision = policyDecision;
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
