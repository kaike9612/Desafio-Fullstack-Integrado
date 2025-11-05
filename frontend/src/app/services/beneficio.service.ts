import { Injectable } from "@angular/core"
import type { HttpClient } from "@angular/common/http"
import type { Observable } from "rxjs"
import type { Beneficio, TransferRequest } from "../models/beneficio.model"

@Injectable({
  providedIn: "root",
})
export class BeneficioService {
  private apiUrl = "http://localhost:8080/api/v1/beneficios"

  constructor(private http: HttpClient) {}

  getAll(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.apiUrl)
  }

  getAllActive(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(`${this.apiUrl}/ativos`)
  }

  getById(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.apiUrl}/${id}`)
  }

  create(beneficio: Beneficio): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.apiUrl, beneficio)
  }

  update(id: number, beneficio: Beneficio): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.apiUrl}/${id}`, beneficio)
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
  }

  transfer(request: TransferRequest): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/transferir`, request)
  }
}
