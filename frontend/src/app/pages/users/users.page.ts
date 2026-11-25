import { Component, inject, OnInit, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { DialogModule } from "primeng/dialog";
import { InputTextModule } from "primeng/inputtext";
import { PasswordModule } from "primeng/password";
import { SelectModule } from "primeng/select";
import { TableModule } from "primeng/table";
import { TagModule } from "primeng/tag";
import { AdminService } from "../../services/admin.service";
import { Role, UserView } from "../../dtos/models";
import { DateTimePipe } from "../../pipes/date-time.pipe";
@Component({
  standalone: true,
  imports: [
    FormsModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    PasswordModule,
    SelectModule,
    TableModule,
    TagModule,
    DateTimePipe,
  ],
  template: ` <div class="page-head">
      <div>
        <span class="eyebrow">ADMINISTRACIÓN</span>
        <h1>Usuarios y roles</h1>
        <p>RBAC con separación USER, AUDIT y ADMIN.</p>
      </div>
      <button
        pButton
        icon="pi pi-user-plus"
        (click)="show.set(true)"
      >Nuevo usuario</button>
    </div>
    <div class="panel">
      <p-table [value]="items()"
        ><ng-template #header
          ><tr>
            <th>Usuario</th>
            <th>Roles</th>
            <th>Estado</th>
            <th>Cambio obligatorio</th>
            <th>Creado</th>
            <th></th></tr></ng-template
        ><ng-template #body let-u
          ><tr>
            <td>
              <b>{{ u.username }}</b>
            </td>
            <td>
              @for (r of u.roles; track r) {
                <p-tag [value]="r" />
              }
            </td>
            <td>
              <p-tag
                [severity]="u.enabled ? 'success' : 'danger'"
                [value]="u.enabled ? 'Activo' : 'Bloqueado'"
              />
            </td>
            <td>{{ u.mustChangePassword ? "Sí" : "No" }}</td>
            <td>{{ u.createdAt | dateTime }}</td>
            <td>
              @if (u.enabled) {
                <button pButton class="p-button-text" (click)="toggle(u)">Bloquear</button>
              } @else {
                <button pButton class="p-button-text" (click)="toggle(u)">Activar</button>
              }        
            </td></tr></ng-template
      ></p-table>
    </div>
    <p-dialog
      header="Crear usuario"
      [(visible)]="dialog"
      [modal]="true"
      [style]="{ width: 'min(92vw,32rem)' }"
      ><div class="form-card">
        <label>Usuario<input pInputText [(ngModel)]="username" /></label
        ><label
          >Contraseña temporal<p-password
            [(ngModel)]="password"
            [toggleMask]="true" /></label
        ><label>Rol<p-select [options]="roles" [(ngModel)]="role" appendTo="body"/></label>
      </div>
      <ng-template #footer
        ><button
          pButton
          label=""
          class="p-button-text"
          (click)="show.set(false)"
        >Cancelar</button
        ><button
          pButton
          label=""
          (click)="create()"
          [disabled]="!username.trim() || password.length < 14"
        >Crear</button></ng-template
    ></p-dialog>`,
})
export class UsersPage implements OnInit {
  private admin = inject(AdminService);
  items = signal<UserView[]>([]);
  show = signal(false);
  username = "";
  password = "";
  role: Role = "USER";
  roles: Role[] = ["USER", "AUDIT", "ADMIN"];
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
    this.admin.users().subscribe((x) => this.items.set(x.content));
  }
  create() {
    this.admin
      .createUser(this.username, this.password, [this.role])
      .subscribe(() => {
        this.show.set(false);
        this.username = "";
        this.password = "";
        this.load();
      });
  }
  toggle(u: UserView) {
    this.admin
      .updateUser(u.id, !u.enabled, u.roles)
      .subscribe(() => this.load());
  }
}
