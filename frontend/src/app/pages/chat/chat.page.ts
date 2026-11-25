import {
  Component,
  ElementRef,
  inject,
  OnInit,
  signal,
  ViewChild,
} from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { FormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { InputTextModule } from "primeng/inputtext";
import { ChatService } from "../../services/chat.service";
import { ConversationView, MessageView } from "../../dtos/models";
@Component({
  standalone: true,
  imports: [FormsModule, ButtonModule, InputTextModule],
  template: `<div class="chat-page">
    <div class="chat-head">
      <div>
        <span class="online"></span>
        <div>
          <h2>{{ conversation()?.title }}</h2>
          <p>Guardrails y PII tokenization activos</p>
        </div>
      </div>
      <span class="secure"><i class="pi pi-shield"></i> Canal protegido</span>
    </div>
    <div class="messages" #scroll>
      @for (m of conversation()?.messages ?? []; track m.id) {
        <article
          [class.user]="m.actor === 'USER'"
          [class.assistant]="m.actor === 'ASSISTANT'"
        >
          <div class="avatar">
            <i
              [class]="m.actor === 'USER' ? 'pi pi-user' : 'pi pi-sparkles'"
            ></i>
          </div>
          <div class="bubble">
            <div class="who">{{ m.actor === "USER" ? "Tú" : "SecOps" }}</div>
            <p>{{ m.content }}</p>
          </div>
        </article>
      }
      @if (loading()) {
        <article class="assistant">
          <div class="avatar"><i class="pi pi-sparkles"></i></div>
          <div class="bubble typing">Analizando fuentes seguras…</div>
        </article>
      }
    </div>
    <form class="composer" (ngSubmit)="send()">
      <textarea
        pInputTextarea
        [(ngModel)]="text"
        name="message"
        rows="2"
        maxlength="8000"
        placeholder="Pregunte por un procedimiento o una CVE…"
        (keydown.control.enter)="send()"
      ></textarea
      ><button
        pButton
        type="submit"
        label=""
        [disabled]="!text.trim()"
      >Enviar</button
      ><small
        >Ctrl + Enter para enviar · No comparta contraseñas o secretos.</small
      >
    </form>
  </div>`,
})
export class ChatPage implements OnInit {
  private route = inject(ActivatedRoute);
  private chat = inject(ChatService);
  conversation = signal<ConversationView | null>(null);
  loading = signal(false);
  text = "";
  id = "";

  ngOnInit() {
    this.id = this.route.snapshot.paramMap.get("id")!;
    this.load();
  }
  
  load() {
    this.chat.get(this.id).subscribe((x) => this.conversation.set(x));
  }
  
  send() {
    const q = this.text.trim();
    if (!q || this.loading()) return;
    this.text = "";
    this.loading.set(true);
    this.chat.send(this.id, q).subscribe({
      next: () => {
        this.loading.set(false);
        this.load();
      },
      error: () => this.loading.set(false),
    });
  }
  
}
