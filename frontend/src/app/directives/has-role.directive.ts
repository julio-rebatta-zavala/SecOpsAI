import {
  Directive,
  Input,
  TemplateRef,
  ViewContainerRef,
  inject,
} from "@angular/core";
import { Role } from "../dtos/models";
import { AuthService } from "../services/auth.service";

@Directive({ selector: "[appHasRole]", standalone: true })
export class HasRoleDirective {
  private template = inject(TemplateRef<unknown>);
  private view = inject(ViewContainerRef);
  private auth = inject(AuthService);

  @Input() set appHasRole(roles: Role[]) {
    this.view.clear();
    if (this.auth.hasRole(...roles))
      this.view.createEmbeddedView(this.template);
  }
  
}
