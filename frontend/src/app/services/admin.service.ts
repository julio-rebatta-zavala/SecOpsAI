import { inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { ApiService } from "./api.service";
import { Page, PromptView, RagDocument, Role, UserView } from "../dtos/models";

@Injectable({ providedIn: "root" })
export class AdminService {

  private api = inject(ApiService);
  private http = inject(HttpClient);
  
  documents() {
    return this.api.get<Page<RagDocument>>("/api/v1/admin/documents");
  }

  upload(file: File) {
    return this.api.upload<RagDocument>("/api/v1/admin/documents", file);
  }

  deleteDocument(id: string) {
    return this.http.delete<void>(`/api/v1/admin/documents/${id}`);
  }

  users() {
    return this.api.get<Page<UserView>>("/api/v1/admin/users");
  }

  createUser(username: string, temporaryPassword: string, roles: Role[]) {
    return this.api.post<UserView>("/api/v1/admin/users", {
      username,
      temporaryPassword,
      roles,
    });
  }
  
  updateUser(id: string, enabled: boolean, roles: Role[]) {
    return this.api.patch<UserView>(`/api/v1/admin/users/${id}`, {
      enabled,
      roles,
    });
  }
  
  prompt() {
    return this.api.get<PromptView>("/api/v1/admin/system-prompt");
  }
  
  savePrompt(content: string) {
    console.log("Saving prompt content:", content);
    return this.api.put<PromptView>("/api/v1/admin/system-prompt", { "content": content });
  }
  
}
