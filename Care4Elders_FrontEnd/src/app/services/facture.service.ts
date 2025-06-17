// billing.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Facture } from '../models/care-request.model';

@Injectable({
  providedIn: 'root'
})
export class BillingService {
  private apiUrl = 'http://localhost:8080/factures'; //  API Gateway

  constructor(private http: HttpClient) {}

//   getFactures(): Observable<Facture[]> {
//     return this.http.get<Facture[]>(this.apiUrl);
//   }
  getFacturesByPatient(patientId: string): Observable<Facture[]> {
    return this.http.get<Facture[]>(`${this.apiUrl}/patient/${patientId}`);
  }

  getFactureById(id: string): Observable<Facture> {
    return this.http.get<Facture>(`${this.apiUrl}/${id}`);
  }

  getFacturePdf(id: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, { responseType: 'blob' });
  }
}
