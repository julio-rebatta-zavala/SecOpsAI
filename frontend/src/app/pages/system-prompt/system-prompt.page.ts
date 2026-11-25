import { Component, inject, OnInit, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { InputTextModule } from "primeng/inputtext";
import { AdminService } from "../../services/admin.service";

@Component({
  standalone: true,
  imports: [FormsModule, ButtonModule, InputTextModule],
  template: `<div class="page-head">
      <div>
        <span class="eyebrow">CONFIGURACIÓN CONTROLADA</span>
        <h1>System prompt</h1>
        <p>Cada cambio crea una versión y un evento de auditoría.</p>
      </div>
      <button
        pButton
        icon="pi pi-save"
        label=""
        (click)="save()"
        [disabled]="text().length < 20"
      >Guardar nueva versión</button>
    </div>
    <div class="panel editor">
      <div class="notice">
        <i class="pi pi-info-circle"></i> No incluya secretos. El prompt no se
        expone a usuarios ni auditores.
      </div>
      <textarea
        pInputTextarea
        [ngModel]="text()"
        (ngModelChange)="text.set($event)"
        rows="22"
        maxlength="20000"
      ></textarea
      ><small>Versión {{ version() }} · {{ text().length }} / 20000</small>
    </div>`,
})
export class SystemPromptPage implements OnInit {

  private admin = inject(AdminService);
  text = signal("");
  version = signal(0);

  ngOnInit() {
    this.admin.prompt().subscribe((x) => {
      this.text.set(x.content);
      this.version.set(x.version);
    });
  }

  save() {
    this.admin
      .savePrompt(this.text())
      .subscribe((x) => this.version.set(x.version));
  }
  
}
