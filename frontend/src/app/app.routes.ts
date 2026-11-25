import { Routes } from "@angular/router";
import { authGuard } from "./guards/auth.guard";
import { roleGuard } from "./guards/role.guard";
export const routes: Routes = [
  {
    path: "login",
    loadComponent: () =>
      import("./pages/login/login.page").then((m) => m.LoginPage),
  },
  {
    path: "",
    canActivate: [authGuard],
    loadComponent: () =>
      import("./layout/app-layout.component").then((m) => m.AppLayoutComponent),
    children: [
      { path: "", pathMatch: "full", redirectTo: "conversations" },
      {
        path: "conversations",
        canActivate: [roleGuard],
        data: { roles: ["USER", "ADMIN"] },
        loadComponent: () =>
          import("./pages/conversations/conversations.page").then(
            (m) => m.ConversationsPage,
          ),
      },
      {
        path: "chat/:id",
        canActivate: [roleGuard],
        data: { roles: ["USER", "ADMIN"] },
        loadComponent: () =>
          import("./pages/chat/chat.page").then((m) => m.ChatPage),
      },
      {
        path: "documents",
        canActivate: [roleGuard],
        data: { roles: ["ADMIN"] },
        loadComponent: () =>
          import("./pages/documents/documents.page").then(
            (m) => m.DocumentsPage,
          ),
      },
      {
        path: "users",
        canActivate: [roleGuard],
        data: { roles: ["ADMIN"] },
        loadComponent: () =>
          import("./pages/users/users.page").then((m) => m.UsersPage),
      },
      {
        path: "prompt",
        canActivate: [roleGuard],
        data: { roles: ["ADMIN"] },
        loadComponent: () =>
          import("./pages/system-prompt/system-prompt.page").then(
            (m) => m.SystemPromptPage,
          ),
      },
      {
        path: "audit",
        canActivate: [roleGuard],
        data: { roles: ["AUDIT", "ADMIN"] },
        loadComponent: () =>
          import("./pages/audit/audit.page").then((m) => m.AuditPage),
      },
      {
        path: "logs",
        canActivate: [roleGuard],
        data: { roles: ["AUDIT", "ADMIN"] },
        loadComponent: () =>
          import("./pages/logs/logs.page").then((m) => m.LogsPage),
      },
      {
        path: "audit-conversations",
        canActivate: [roleGuard],
        data: { roles: ["AUDIT", "ADMIN"] },
        loadComponent: () =>
          import("./pages/audit-conversations/audit-conversations.page").then(
            (m) => m.AuditConversationsPage,
          ),
      },
      {
        path: "change-password",
        loadComponent: () =>
          import("./pages/change-password/change-password.page").then(
            (m) => m.ChangePasswordPage,
          ),
      },
      {
        path: "forbidden",
        loadComponent: () =>
          import("./pages/forbidden/forbidden.page").then(
            (m) => m.ForbiddenPage,
          ),
      },
    ],
  },
  { path: "**", redirectTo: "" },
];
