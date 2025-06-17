import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Appointment } from '../models/appointment.model';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private baseUrl = 'http://localhost:8080/appointment'; 

  constructor(private http: HttpClient) {}

  getAll(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(this.baseUrl);
  }

  getById(id: string): Observable<Appointment> {
    return this.http.get<Appointment>(`${this.baseUrl}/${id}`);
  }

  create(appointment: Appointment): Observable<Appointment> {
    return this.http.post<Appointment>(this.baseUrl, appointment);
  }

  update(id: string, appointment: Appointment): Observable<Appointment> {
    return this.http.put<Appointment>(`${this.baseUrl}/${id}`, appointment);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
