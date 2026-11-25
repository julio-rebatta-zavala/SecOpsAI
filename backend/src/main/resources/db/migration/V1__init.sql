CREATE TABLE app_users (
 id UUID PRIMARY KEY, username VARCHAR(80) UNIQUE NOT NULL, password_hash VARCHAR(255) NOT NULL,
 enabled BOOLEAN NOT NULL DEFAULT TRUE, must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
 failed_attempts INT NOT NULL DEFAULT 0, locked_until TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL,
 updated_at TIMESTAMPTZ NOT NULL, version BIGINT NOT NULL DEFAULT 0
);
CREATE TABLE user_roles (user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE, role VARCHAR(20) NOT NULL, PRIMARY KEY(user_id,role));
CREATE TABLE conversations (id UUID PRIMARY KEY, owner_id UUID NOT NULL REFERENCES app_users(id), title VARCHAR(160) NOT NULL, created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL, version BIGINT NOT NULL DEFAULT 0);
CREATE INDEX idx_conversation_owner_updated ON conversations(owner_id, updated_at DESC);
CREATE TABLE messages (id UUID PRIMARY KEY, conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE, actor VARCHAR(20) NOT NULL, content_ciphertext TEXT NOT NULL, content_sha256 CHAR(64) NOT NULL, policy_decision VARCHAR(40) NOT NULL, created_at TIMESTAMPTZ NOT NULL);
CREATE INDEX idx_message_conversation_created ON messages(conversation_id, created_at);
CREATE TABLE audit_events (id UUID PRIMARY KEY, occurred_at TIMESTAMPTZ NOT NULL, correlation_id VARCHAR(80) NOT NULL, actor_id UUID, actor_username VARCHAR(80), actor_roles VARCHAR(100), action VARCHAR(80) NOT NULL, resource_type VARCHAR(80), resource_id VARCHAR(100), decision VARCHAR(40) NOT NULL, categories VARCHAR(500), detail_safe VARCHAR(1000), event_hash CHAR(64) NOT NULL);
CREATE INDEX idx_audit_time ON audit_events(occurred_at DESC); CREATE INDEX idx_audit_correlation ON audit_events(correlation_id);
CREATE TABLE rag_documents (id UUID PRIMARY KEY, original_name VARCHAR(255) NOT NULL, stored_name VARCHAR(255) NOT NULL, content_sha256 CHAR(64) UNIQUE NOT NULL, mime_type VARCHAR(100) NOT NULL, size_bytes BIGINT NOT NULL, status VARCHAR(30) NOT NULL, source VARCHAR(30) NOT NULL, chunks INT NOT NULL DEFAULT 0, rejection_reason VARCHAR(500), uploaded_by UUID, created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL);
CREATE TABLE system_prompts (id UUID PRIMARY KEY, version_no INT UNIQUE NOT NULL, content TEXT NOT NULL, content_sha256 CHAR(64) NOT NULL, active BOOLEAN NOT NULL DEFAULT FALSE, created_by UUID, created_at TIMESTAMPTZ NOT NULL);
CREATE UNIQUE INDEX one_active_prompt ON system_prompts(active) WHERE active = TRUE;
CREATE TABLE refresh_tokens (id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE, family_id UUID NOT NULL, token_hash CHAR(64) UNIQUE NOT NULL, expires_at TIMESTAMPTZ NOT NULL, revoked_at TIMESTAMPTZ, replaced_by UUID, created_at TIMESTAMPTZ NOT NULL);
