import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, throwError } from "rxjs";
import { MessageService } from "primeng/api";
import { AuthService } from "../services/auth.service";
import { ProblemDetails } from "../dtos/models";

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const messages = inject(MessageService),
        auth = inject(AuthService);
        
  return next(req).pipe(
    catchError((e: HttpErrorResponse) => {
      if (e.status === 401 && auth.loggedIn()) auth.logout();
      const p = e.error as ProblemDetails;
      messages.add({
        severity: e.status === 422 ? "warn" : "error",
        summary: p?.title ?? "Error",
        detail: p?.detail ?? "No se pudo completar la operación",
      });
      return throwError(() => e);
    }),
  );
};
