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
@Table(name = "audit_events")
public class AuditEvent {
	
	@Id
	private UUID id;
	
	@Column(name = "occurred_at")
	private Instant occurredAt;
	
	@Column(name = "correlation_id")
	private String correlationId;
	
	@Column(name = "actor_id")
	private UUID actorId;
	
	@Column(name = "actor_username")
	private String actorUsername;
	
	@Column(name = "actor_roles")
	private String actorRoles;
	
	private String action;
	
	@Column(name = "resource_type")
	private String resourceType;
	
	@Column(name = "resource_id")
	private String resourceId;
	
	private String decision;
	
	private String categories;
	
	@Column(name = "detail_safe")
	private String detailSafe;
	
	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "event_hash", nullable = false, length = 64, columnDefinition = "CHAR(64)")
	private String eventHash;

	public AuditEvent() {
		super();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Instant getOccurredAt() {
		return occurredAt;
	}

	public void setOccurredAt(Instant occurredAt) {
		this.occurredAt = occurredAt;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public UUID getActorId() {
		return actorId;
	}

	public void setActorId(UUID actorId) {
		this.actorId = actorId;
	}

	public String getActorUsername() {
		return actorUsername;
	}

	public void setActorUsername(String actorUsername) {
		this.actorUsername = actorUsername;
	}

	public String getActorRoles() {
		return actorRoles;
	}

	public void setActorRoles(String actorRoles) {
		this.actorRoles = actorRoles;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public String getDetailSafe() {
		return detailSafe;
	}

	public void setDetailSafe(String detailSafe) {
		this.detailSafe = detailSafe;
	}

	public String getEventHash() {
		return eventHash;
	}

	public void setEventHash(String eventHash) {
		this.eventHash = eventHash;
	}

	@PrePersist
	void create() {
		id = id == null ? UUID.randomUUID() : id;
		occurredAt = occurredAt == null ? Instant.now() : occurredAt;
	}
}
