import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
@Component({
  standalone: true,
  imports: [RouterLink],
  template: `<div class="empty full">
    <i class="pi pi-lock"></i>
    <h1>Acceso no autorizado</h1>
    <p>Su rol no permite abrir esta sección.</p>
    <a routerLink="/">Volver al inicio</a>
  </div>`,
})
export class ForbiddenPage {}
