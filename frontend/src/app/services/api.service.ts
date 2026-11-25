import { inject, Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";

@Injectable({ providedIn: "root" })
export class ApiService {
  private http = inject(HttpClient);

  get<T>(url: string, page = 0, size = 50) {
    return this.http.get<T>(url, {
      params: new HttpParams().set("page", page).set("size", size),
    });
  }
  
  post<T>(url: string, body: unknown) {
    return this.http.post<T>(url, body);
  }
  
  put<T>(url: string, body: unknown) {
    console.log("PUT request to:", url, "with body:", body);
    return this.http.put<T>(url, body);
  }
  
  patch<T>(url: string, body: unknown) {
    return this.http.patch<T>(url, body);
  }
  
  upload<T>(url: string, file: File) {
    const f = new FormData();
    f.append("file", file, file.name);
    return this.http.post<T>(url, f);
  }

}
