import { Component, inject, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ButtonModule } from "primeng/button";
import { InputTextModule } from "primeng/inputtext";
import { InputPasswordModule  } from "primeng/inputpassword";
import { AuthService } from "../../services/auth.service";

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, ButtonModule, InputTextModule, InputPasswordModule ],
  template: `<div class="login-page">
    <section class="login-visual">
      <div class="hero-shield">S</div>
      <h1>SecOps AI</h1>
      <p>
        Asistencia segura para operaciones de ciberseguridad, respaldada por
        procedimientos y fuentes verificables.
      </p>
      <div class="trust">
        <span><i class="pi pi-lock"></i> PII protegida</span
        ><span><i class="pi pi-check-circle"></i> Auditoría integral</span>
      </div>
    </section>
    <section class="login-panel">
      <form [formGroup]="form" (ngSubmit)="submit()">
        <div class="eyebrow">ACCESO SEGURO</div>
        <h2>Bienvenido</h2>
        <p>Ingrese sus credenciales para continuar.</p>
        <label
          >Usuario<input
            pInputText
            formControlName="username"
            autocomplete="username" /></label
        ><label
          >Contraseña<input pInputPassword
            formControlName="password"
            autocomplete="current-password" /></label
        ><button
          pButton
          type="submit"
          [disabled]="form.invalid"
        >Ingresar</button>
      </form>
    </section>
  </div>`,
})
export class LoginPage {

  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  loading = signal(false);

  form = this.fb.nonNullable.group({
    username: ["", [Validators.required]],
    password: ["", [Validators.required]],
  });

  submit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.auth
      .login(this.form.getRawValue().username, this.form.getRawValue().password)
      .subscribe({
        next: (x) =>
          void this.router.navigate([
            x.mustChangePassword ? "/change-password" : "/",
          ]),
        error: () => this.loading.set(false),
      });
 
    }
}
