// src/app/services/care-request.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CareRequest, Facture } from '../models/care-request.model';

@Injectable({
  providedIn: 'root'
})
export class CareRequestService {
  protected baseUrl = 'http://localhost:8080/api/care';
  
  constructor(private http: HttpClient) {}

  getBaseUrl(): string {
    return this.baseUrl;
  }

  getAll(): Observable<CareRequest[]> {
    return this.http.get<CareRequest[]>(this.baseUrl);
  }

  create(careRequest: CareRequest): Observable<CareRequestWithFactureResponse> {
    return this.http.post<CareRequestWithFactureResponse>(this.baseUrl, careRequest);
  }

  getById(id: string): Observable<CareRequest> {
    return this.http.get<CareRequest>(`${this.baseUrl}/${id}`);
  }

  update(id: string, careRequest: CareRequest): Observable<CareRequest> {
    return this.http.put<CareRequest>(`${this.baseUrl}/${id}`, careRequest);
  }

  delete(id: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }
  
  getCareRequestsByPatient(patientId: string): Observable<CareRequest[]> {
    return this.http.get<CareRequest[]>(`${this.baseUrl}/patient/${patientId}`);
  }
}

export interface CareRequestWithFactureResponse {
  careRequest: CareRequest;
  facture: Facture;
}

