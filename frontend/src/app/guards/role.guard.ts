import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";
import { Role } from "../dtos/models";

export const roleGuard: CanActivateFn = (route) => {
  const roles = route.data["roles"] as Role[];
  return inject(AuthService).hasRole(...roles)
    ? true
    : inject(Router).createUrlTree(["/forbidden"]);
};
