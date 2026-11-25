import { inject, Injectable } from "@angular/core";
import { ApiService } from "./api.service";
import {
  ChatResponse,
  ConversationSummary,
  ConversationView,
  Page,
} from "../dtos/models";

@Injectable({ providedIn: "root" })
export class ChatService {

  private api = inject(ApiService);
  
  list() {
    return this.api.get<Page<ConversationSummary>>("/api/v1/conversations");
  }
  
  create(title: string) {
    return this.api.post<ConversationSummary>("/api/v1/conversations", {
      title,
    });
  }
  
  get(id: string) {
    return this.api.get<ConversationView>(`/api/v1/conversations/${id}`);
  }
  
  send(id: string, content: string) {
    return this.api.post<ChatResponse>(`/api/v1/conversations/${id}/messages`, {
      content,
    });
  }
  
}
