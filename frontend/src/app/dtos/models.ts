export type Role = "USER" | "AUDIT" | "ADMIN";

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  expiresInSeconds: number;
  username: string;
  roles: Role[];
  mustChangePassword: boolean;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ConversationSummary {
  id: string;
  title: string;
  createdAt: string;
  updatedAt: string;
}

export interface MessageView {
  id: string;
  actor: "USER" | "ASSISTANT" | "SYSTEM";
  content: string;
  createdAt: string;
  policyDecision: string;
}

export interface ConversationView {
  id: string;
  ownerId: string;
  title: string;
  messages: MessageView[];
}

export interface ChatResponse {
  messageId: string;
  answer: string;
  sources: { type: string; name: string; reference: string; score: number }[];
  degradedSources: string[];
  correlationId: string;
}

export interface AuditEvent {
  id: string;
  occurredAt: string;
  correlationId: string;
  actor: string;
  roles: string;
  action: string;
  resourceType: string;
  resourceId: string;
  decision: string;
  categories: string;
  detailSafe: string;
}

export interface RagDocument {
  id: string;
  name: string;
  hash: string;
  status: string;
  source: string;
  chunks: number;
  rejectionReason?: string;
  createdAt: string;
}

export interface UserView {
  id: string;
  username: string;
  enabled: boolean;
  mustChangePassword: boolean;
  roles: Role[];
  createdAt: string;
}

export interface PromptView {
  id: string;
  version: number;
  content: string;
  hash: string;
  createdAt: string;
}

export interface ProblemDetails {
  title: string;
  status: number;
  detail: string;
  errorCode: string;
  correlationId: string;
}
