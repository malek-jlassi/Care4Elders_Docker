import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Teleconsultation } from '../models/teleconsultation.model';


@Injectable({
  providedIn: 'root'
})
export class TeleconsultationService {
  private apiUrl = 'http://localhost:8080/teleconsultations';

  constructor(private http: HttpClient) {}

  getAll(pageNbr: number, pageSize: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}?pageNbr=${pageNbr}&pageSize=${pageSize}`);
  }

  getById(id: string): Observable<Teleconsultation> {
    return this.http.get<Teleconsultation>(`${this.apiUrl}/${id}`);
  }

  add(teleconsultation: Teleconsultation): Observable<Teleconsultation> {
    return this.http.post<Teleconsultation>(this.apiUrl, teleconsultation);
  }

  update(id: string, partialData: Partial<Teleconsultation>): Observable<Teleconsultation> {
    return this.http.patch<Teleconsultation>(`${this.apiUrl}/${id}`, partialData);
  }

  delete(id: string): Observable<boolean> {
    return this.http.delete<boolean>(`${this.apiUrl}/${id}`);
  }
}
