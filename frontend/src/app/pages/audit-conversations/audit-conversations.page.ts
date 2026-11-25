import { Component, inject, OnInit, signal } from "@angular/core";
import { DialogModule } from "primeng/dialog";
import { ConversationSummary, ConversationView } from "../../dtos/models";
import { AuditService } from "../../services/audit.service";
import { DateTimePipe } from "../../pipes/date-time.pipe";

@Component({
  standalone: true,
  imports: [DialogModule, DateTimePipe],
  template: `<div class="page-head">
      <div>
        <span class="eyebrow">REVISIÓN AUTORIZADA</span>
        <h1>Conversaciones auditables</h1>
        <p>Cada acceso al contenido completo genera un evento AUDIT_READ.</p>
      </div>
    </div>
    <div class="card-grid">
      @for (c of items(); track c.id) {
        <button class="conversation-card" (click)="open(c.id)">
          <span class="icon"><i class="pi pi-eye"></i></span>
          <div>
            <h3>{{ c.title }}</h3>
            <p>{{ c.updatedAt | dateTime }}</p>
          </div>
          <i class="pi pi-arrow-right"></i>
        </button>
      }
    </div>
    <p-dialog
      header="Contenido auditado"
      [(visible)]="dialog"
      [modal]="true"
      [style]="{ width: 'min(96vw,60rem)' }"
    >
      @for (m of selected()?.messages ?? []; track m.id) {
        <article class="audit-message">
          <b>{{ m.actor }}</b
          ><small>{{ m.createdAt | dateTime }}</small>
          <p>{{ m.content }}</p>
        </article>
      }
    </p-dialog>`,
})
export class AuditConversationsPage implements OnInit {
  
  private svc = inject(AuditService);
  items = signal<ConversationSummary[]>([]);
  selected = signal<ConversationView | null>(null);
  show = signal(false);
  
  get dialog() {
    return this.show();
  }
  
  set dialog(v: boolean) {
    this.show.set(v);
  }
  
  ngOnInit() {
    this.svc.conversations().subscribe((x) => this.items.set(x.content));
  }
  
  open(id: string) {
    this.svc.conversation(id).subscribe((x) => {
      this.selected.set(x);
      this.show.set(true);
    });
  }
  
}
