import { inject, Injectable } from "@angular/core";
import { ApiService } from "./api.service";
import {
  AuditEvent,
  ConversationSummary,
  ConversationView,
  Page,
} from "../dtos/models";

@Injectable({ providedIn: "root" })
export class AuditService {

  private api = inject(ApiService);
  
  events() {
    return this.api.get<Page<AuditEvent>>("/api/v1/audit/events");
  }
  
  logs() {
    return this.api.get<Page<AuditEvent>>("/api/v1/audit/application-logs");
  }
  
  conversations() {
    return this.api.get<Page<ConversationSummary>>(
      "/api/v1/audit/conversations",
    );
  }
  
  conversation(id: string) {
    return this.api.get<ConversationView>(`/api/v1/audit/conversations/${id}`);
  }
  
}
