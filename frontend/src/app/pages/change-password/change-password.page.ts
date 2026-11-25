import { Component, inject, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { PasswordModule } from "primeng/password";
import { AuthService } from "../../services/auth.service";
@Component({
  standalone: true,
  imports: [ReactiveFormsModule, ButtonModule, PasswordModule],
  template: `<div class="page-head">
      <div>
        <span class="eyebrow">CUENTA</span>
        <h1>Cambiar contraseña</h1>
        <p>Al guardar se revocarán todas sus sesiones.</p>
      </div>
    </div>
    <form class="panel form-card" [formGroup]="form" (ngSubmit)="save()">
      <label
        >Contraseña actual<p-password
          formControlName="currentPassword"
          [feedback]="false"
          [toggleMask]="true" /></label
      ><label
        >Nueva contraseña<p-password
          formControlName="newPassword"
          [feedback]="true"
          [toggleMask]="true" /></label
      ><label
        >Confirmar contraseña<p-password
          formControlName="confirm"
          [feedback]="false"
          [toggleMask]="true" /></label
      ><button
        pButton
        type="submit"
        [disabled]="form.invalid"
      >Actualizar y cerrar sesiones</button>
    </form>`,
})
export class ChangePasswordPage {

  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  loading = signal(false);
  
  form = this.fb.nonNullable.group({
    currentPassword: ["", Validators.required],
    newPassword: ["", [Validators.required, Validators.minLength(14)]],
    confirm: ["", Validators.required],
  });

  save() {
    const v = this.form.getRawValue();
    if (v.newPassword !== v.confirm) return;
    this.loading.set(true);
    this.auth
      .changePassword(v.currentPassword, v.newPassword)
      .subscribe({ error: () => this.loading.set(false) });
  }
  
}
