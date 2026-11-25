import { Component, computed, inject, signal } from "@angular/core";
import { RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";
import { ButtonModule } from "primeng/button";
import { AuthService } from "../services/auth.service";
import { HasRoleDirective } from "../directives/has-role.directive";

@Component({
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    ButtonModule,
    HasRoleDirective,
  ],
  template: ` <div class="shell" [class.nav-open]="open()">
    <header>
      <button
        pButton
        icon="pi pi-bars"
        class="p-button-text mobile"
        aria-label="Menú"
        (click)="open.set(!open())"
      ></button
      ><a class="brand" routerLink="/"
        ><span class="shield">S</span><span>SecOps AI</span></a
      ><span class="spacer"></span
      ><span class="identity"
        ><b>{{ auth.user()?.username }}</b
        ><small>{{ auth.user()?.roles?.join(" · ") }}</small></span
      ><button
        pButton
        icon="pi pi-sign-out"
        class="p-button-text"
        aria-label="Salir"
        (click)="auth.logout()"
      ></button>
    </header>
    <aside>
      <nav>
        <a
          routerLink="/conversations"
          routerLinkActive="active"
          *appHasRole="['USER', 'ADMIN']"
          ><i class="pi pi-comments"></i> Conversaciones</a
        ><a
          routerLink="/documents"
          routerLinkActive="active"
          *appHasRole="['ADMIN']"
          ><i class="pi pi-book"></i> Documentos RAG</a
        ><a
          routerLink="/users"
          routerLinkActive="active"
          *appHasRole="['ADMIN']"
          ><i class="pi pi-users"></i> Usuarios</a
        ><a
          routerLink="/prompt"
          routerLinkActive="active"
          *appHasRole="['ADMIN']"
          ><i class="pi pi-sliders-h"></i> System prompt</a
        ><a
          routerLink="/audit-conversations"
          routerLinkActive="active"
          *appHasRole="['AUDIT', 'ADMIN']"
          ><i class="pi pi-eye"></i> Auditoría de chats</a
        ><a
          routerLink="/audit"
          routerLinkActive="active"
          *appHasRole="['AUDIT', 'ADMIN']"
          ><i class="pi pi-shield"></i> Logs de auditoría</a
        ><a
          routerLink="/logs"
          routerLinkActive="active"
          *appHasRole="['AUDIT', 'ADMIN']"
          ><i class="pi pi-list"></i> Logs aplicación</a
        ><a routerLink="/change-password" routerLinkActive="active"
          ><i class="pi pi-key"></i> Cambiar contraseña</a
        >
      </nav>
      <div class="safe">
        <i class="pi pi-lock"></i
        ><span
          >PII tokenizada localmente<br /><small
            >Guardrails activos</small
          ></span
        >
      </div>
    </aside>
    <main (click)="open.set(false)"><router-outlet /></main>
  </div>`,
})
export class AppLayoutComponent {
  auth = inject(AuthService);
  open = signal(false);
}
