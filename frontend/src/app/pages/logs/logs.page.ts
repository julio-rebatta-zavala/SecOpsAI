import { Component, inject, OnInit, signal } from "@angular/core";
import { TableModule } from "primeng/table";
import { AuditService } from "../../services/audit.service";
import { AuditEvent } from "../../dtos/models";
import { DateTimePipe } from "../../pipes/date-time.pipe";
@Component({
  standalone: true,
  imports: [TableModule, DateTimePipe],
  template: `<div class="page-head">
      <div>
        <span class="eyebrow">OBSERVABILIDAD</span>
        <h1>Logs de aplicación</h1>
        <p>Vista segura y estructurada; no muestra cuerpos, tokens ni PII.</p>
      </div>
    </div>
    <div class="panel terminal">
      <p-table [value]="items()" stripedRows [paginator]="true" [rows]="20" [rowsPerPageOptions]="[20, 50, 100]"
        ><ng-template #body let-e
          ><tr>
            <td>{{ e.occurredAt | dateTime }}</td>
            <td>
              <code>{{ e.correlationId }}</code>
            </td>
            <td>
              <b>{{ e.action }}</b> {{ e.resourceType }}/{{ e.resourceId }}
            </td>
            <td>{{ e.decision }}</td>
          </tr></ng-template
        ></p-table
      >
    </div>`,
})
export class LogsPage implements OnInit {

  private svc = inject(AuditService);
  items = signal<AuditEvent[]>([]);
  
  ngOnInit() {
    this.svc.logs().subscribe((x) => this.items.set(x.content));
  }
  
}
