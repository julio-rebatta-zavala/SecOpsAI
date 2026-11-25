import { Component, inject, OnInit, signal } from "@angular/core";
import { TableModule } from "primeng/table";
import { TagModule } from "primeng/tag";
import { AuditService } from "../../services/audit.service";
import { AuditEvent } from "../../dtos/models";
import { DateTimePipe } from "../../pipes/date-time.pipe";

@Component({
  standalone: true,
  imports: [TableModule, TagModule, DateTimePipe],
  template: `<div class="page-head">
      <div>
        <span class="eyebrow">CUMPLIMIENTO</span>
        <h1>Logs de auditoría</h1>
        <p>Decisiones de política sin contenido sensible.</p>
      </div>
    </div>
    <div class="panel">
      <p-table [value]="items()" stripedRows [paginator]="true" [rows]="20" [rowsPerPageOptions]="[20, 50, 100]"
        ><ng-template #header
          ><tr>
            <th>Fecha</th>
            <th>Actor</th>
            <th>Acción</th>
            <th>Decisión</th>
            <th>Categorías</th>
            <th>Correlation ID</th>
          </tr></ng-template
        ><ng-template #body let-e
          ><tr>
            <td>{{ e.occurredAt | dateTime }}</td>
            <td>{{ e.actor || "sistema" }}</td>
            <td>
              <b>{{ e.action }}</b
              ><small>{{ e.detailSafe }}</small>
            </td>
            <td>
              <p-tag
                [severity]="e.decision === 'ALLOW' ? 'success' : 'danger'"
                [value]="e.decision"
              />
            </td>
            <td>{{ e.categories }}</td>
            <td>
              <code>{{ e.correlationId }}</code>
            </td>
          </tr></ng-template
        ></p-table
      >
    </div>`,
})
export class AuditPage implements OnInit {

  private svc = inject(AuditService);
  items = signal<AuditEvent[]>([]);
  
  ngOnInit() {
    this.svc.events().subscribe((x) => this.items.set(x.content));
  }
  
}
