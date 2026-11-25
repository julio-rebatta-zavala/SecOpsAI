import { Component, inject, OnInit, signal } from "@angular/core";
import { ButtonModule } from "primeng/button";
import { TableModule } from "primeng/table";
import { TagModule } from "primeng/tag";
import { AdminService } from "../../services/admin.service";
import { RagDocument } from "../../dtos/models";
import { DateTimePipe } from "../../pipes/date-time.pipe";
@Component({
  standalone: true,
  imports: [ButtonModule, TableModule, TagModule, DateTimePipe],
  template: `<div class="page-head">
      <div>
        <span class="eyebrow">BASE DE CONOCIMIENTO</span>
        <h1>Documentos RAG</h1>
        <p>Solo se indexan documentos únicos y aprobados por los controles.</p>
      </div>
      <label class="upload p-button"
        ><i class="pi pi-upload"></i> Cargar documento<input
          type="file"
          accept=".md,.txt,.pdf"
          hidden
          (change)="upload($event)"
      /></label>
    </div>
    <div class="panel">
      <p-table [value]="items()" stripedRows [paginator]="true" [rows]="10" [rowsPerPageOptions]="[10, 20, 50]"
        ><ng-template #header
          ><tr>
            <th>Documento</th>
            <th>Estado</th>
            <th>Origen</th>
            <th>Chunks</th>
            <th>Fecha</th>
            <th></th></tr></ng-template
        ><ng-template #body let-d
          ><tr>
            <td>
              <b>{{ d.name }}</b
              ><small class="hash">{{ d.hash.slice(0, 16) }}…</small>
            </td>
            <td>
              <p-tag
                [severity]="
                  d.status === 'INDEXED'
                    ? 'success'
                    : d.status === 'QUARANTINED'
                      ? 'danger'
                      : 'warn'
                "
                [value]="d.status"
              />
            </td>
            <td>{{ d.source }}</td>
            <td>{{ d.chunks }}</td>
            <td>{{ d.createdAt | dateTime }}</td>
            <td>
              <button
                pButton
                class="p-button-text p-button-danger"
                [disabled]="d.status === 'DELETED'"
                (click)="remove(d.id)"
                label=""
              >Eliminar</button>
            </td></tr></ng-template
      ></p-table>
    </div>`,
})
export class DocumentsPage implements OnInit {

  private admin = inject(AdminService);
  items = signal<RagDocument[]>([]);

  ngOnInit() {
    this.load();
  }

  load() {
    this.admin.documents().subscribe((x) => this.items.set(x.content));
  }

  upload(e: Event) {
    const f = (e.target as HTMLInputElement).files?.[0];
    if (f) this.admin.upload(f).subscribe(() => this.load());
  }

  remove(id: string) {
    if (confirm("¿Eliminar los vectores de este documento?"))
      this.admin.deleteDocument(id).subscribe(() => this.load());
  }
  
}
