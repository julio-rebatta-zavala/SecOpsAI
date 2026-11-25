import { computed, inject, Injectable, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { tap } from "rxjs";
import { Role, TokenResponse } from "../dtos/models";

@Injectable({ providedIn: "root" })
export class AuthService {

  private http = inject(HttpClient);
  private router = inject(Router);
  private state = signal<TokenResponse | null>(this.read());
  user = computed(() => this.state());
  loggedIn = computed(() => !!this.state()?.accessToken);
  
  hasRole(...roles: Role[]) {
    return !!this.state()?.roles.some((r) => roles.includes(r));
  }
  
  login(username: string, password: string) {
    return this.http
      .post<TokenResponse>("/api/v1/auth/login", { username, password })
      .pipe(tap((x) => this.save(x)));
  }
  
  changePassword(currentPassword: string, newPassword: string) {
    return this.http
      .put<void>("/api/v1/me/password", { currentPassword, newPassword })
      .pipe(tap(() => this.logout()));
  }
  
  token() {
    return this.state()?.accessToken ?? null;
  }
  
  logout() {
    sessionStorage.removeItem("secops.session");
    this.state.set(null);
    void this.router.navigate(["/login"]);
  }
  
  private save(x: TokenResponse) {
    sessionStorage.setItem("secops.session", JSON.stringify(x));
    this.state.set(x);
  }
  
  private read() {
    try {
      return JSON.parse(
        sessionStorage.getItem("secops.session") ?? "null",
      ) as TokenResponse | null;
    } catch {
      return null;
    }
  }
  
}
