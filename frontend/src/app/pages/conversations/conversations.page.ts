import { Component, inject, OnInit, signal } from "@angular/core";
import { Router } from "@angular/router";
import { ButtonModule } from "primeng/button";
import { DialogModule } from "primeng/dialog";
import { InputTextModule } from "primeng/inputtext";
import { FormsModule } from "@angular/forms";
import { ChatService } from "../../services/chat.service";
import { ConversationSummary } from "../../dtos/models";
import { DateTimePipe } from "../../pipes/date-time.pipe";

@Component({
  standalone: true,
  imports: [
    ButtonModule,
    DialogModule,
    InputTextModule,
    FormsModule,
    DateTimePipe,
  ],
  template: `
      <div class="page-head">
        <div>
          <span class="eyebrow">ASISTENTE</span>
          <h1>Conversaciones</h1>
          <p>Inicie o continúe una consulta con contexto seguro.</p>
        </div>
        <button pButton label="" icon="pi pi-plus" (click)="show.set(true)">Nueva Conversacion</button>
    </div>
    <div class="card-grid">
      @for (c of items(); track c.id) {
        <button class="conversation-card" (click)="open(c.id)">
          <span class="icon"><i class="pi pi-comments"></i></span>
          <div>
            <h3>{{ c.title }}</h3>
            <p>Actualizada {{ c.updatedAt | dateTime }}</p>
          </div>
          <i class="pi pi-arrow-right"></i>
        </button>
      } @empty {
        <div class="empty">
          <i class="pi pi-comment"></i>
          <h3>Aún no hay conversaciones</h3>
          <p>Cree la primera para consultar procedimientos o una CVE.</p>
        </div>
      }
    </div>
    <p-dialog header="Nueva conversación" [(visible)]="dialog" [modal]="true" [style]="{ width: 'min(92vw,30rem)' }">
      <label>Título<input pInputText [(ngModel)]="title" maxlength="160" /></label>
      <ng-template #footer>
        <button type="button" pButton severity="secondary" variant="outlined" (click)="show.set(false)">Cancelar</button>
        <button type="button" pButton (click)="create()" [disabled]="!title.trim()">Crear</button>
      </ng-template>
    </p-dialog>`,
})
export class ConversationsPage implements OnInit {

  private chat = inject(ChatService);
  private router = inject(Router);
  items = signal<ConversationSummary[]>([]);
  show = signal(false);
  title = "";

  get dialog() {
    return this.show();
  }

  set dialog(v: boolean) {
    this.show.set(v);
  }

  ngOnInit() {
    this.load();
  }

  load() {
    this.chat.list().subscribe((x) => this.items.set(x.content));
  }

  create() {
    this.chat.create(this.title).subscribe((x) => {
      this.show.set(false);
      this.title = "";
      this.open(x.id);
    });
  }

  open(id: string) {
    void this.router.navigate(["/chat", id]);
  }
  
}
